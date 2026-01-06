package com.vishal2376.echo.domain_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.echo.domain_screen.action.DomainActions
import com.vishal2376.echo.domain_screen.events.DomainEvents
import com.vishal2376.echo.domain_screen.state.BlockingFactor
import com.vishal2376.echo.domain_screen.state.ConnectionStatus
import com.vishal2376.echo.domain_screen.state.DnsDebugInfo
import com.vishal2376.echo.domain_screen.state.DnsInfo
import com.vishal2376.echo.domain_screen.state.DnsResponseType
import com.vishal2376.echo.domain_screen.state.DomainState
import com.vishal2376.echo.domain_screen.state.ExceptionInfo
import com.vishal2376.echo.domain_screen.state.HttpInfo
import com.vishal2376.echo.domain_screen.state.Importance
import com.vishal2376.echo.domain_screen.state.IpAnalysis
import com.vishal2376.echo.domain_screen.state.IpClass
import com.vishal2376.echo.domain_screen.state.SslInfo
import com.vishal2376.echo.domain_screen.state.TcpInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DomainViewModel : ViewModel() {

    private val _state = MutableStateFlow(DomainState())
    val state: StateFlow<DomainState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DomainEvents>()
    val events = _events.asSharedFlow()

    private val blockedIpRegex = Regex(
        "^(0\\.0\\.0\\.0|127\\.0\\.0\\.1|::1|" +
                "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" +
                "172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3}|" +
                "192\\.168\\.\\d{1,3}\\.\\d{1,3})$"
    )

    fun onAction(action: DomainActions) {
        when (action) {
            is DomainActions.UpdateDomain -> {
                _state.update { it.copy(domain = action.domain) }
            }
            is DomainActions.CheckDomain -> {
                checkDomain()
            }
            is DomainActions.ClearResults -> {
                _state.update {
                    DomainState(domain = it.domain)
                }
            }
            is DomainActions.ShareResults -> {
                shareResults()
            }
        }
    }

    private fun shareResults() {
        val state = _state.value
        val shareText = generateShareText(state)
        viewModelScope.launch {
            _events.emit(DomainEvents.ShareText(shareText))
        }
    }

    private fun generateShareText(state: DomainState): String = buildString {
        appendLine("DOMAIN CHECK REPORT")
        appendLine("=".repeat(40))
        appendLine()
        appendLine("Domain: ${state.domain}")
        appendLine("Status: ${state.overallStatus.name}")
        state.lastCheckedAt?.let {
            appendLine("Timestamp: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(it))}")
        }
        appendLine()

        // DNS Info
        state.dnsInfo?.let { dns ->
            appendLine("DNS INFORMATION")
            appendLine("-".repeat(40))
            appendLine("IP Addresses: ${dns.ipAddresses.joinToString(", ")}")
            dns.canonicalHostName?.let { appendLine("Canonical Name: $it") }
            appendLine("Resolution Time: ${dns.resolutionTimeMs}ms")
            appendLine("IPv6 Available: ${if (dns.isIpv6Available) "Yes" else "No"}")
            
            dns.ipAnalyses.forEach { analysis ->
                appendLine()
                appendLine("  IP: ${analysis.ip}")
                appendLine("  Class: ${analysis.ipClass.description}")
                appendLine("  Private: ${if (analysis.isPrivate) "Yes" else "No"}")
                appendLine("  Blocked: ${if (analysis.isBlocked) "Yes - ${analysis.blockReason}" else "No"}")
                analysis.reverseDns?.let { appendLine("  Reverse DNS: $it") }
            }
            
            dns.debugInfo?.let { debug ->
                appendLine()
                appendLine("  [Debug]")
                appendLine("  Response Type: ${debug.dnsResponseType.name}")
                appendLine("  Query Time: ${debug.queryTimeMs}ms")
                debug.exceptionType?.let { appendLine("  Exception: $it") }
                debug.exceptionMessage?.let { appendLine("  Message: $it") }
            }
            appendLine()
        }

        // TCP Info
        state.tcpInfo?.let { tcp ->
            appendLine("TCP CONNECTION")
            appendLine("-".repeat(40))
            appendLine("Port 443 (HTTPS): ${if (tcp.port443Reachable) "Reachable" else "Blocked"}")
            appendLine("Port 80 (HTTP): ${if (tcp.port80Reachable) "Reachable" else "Blocked"}")
            if (tcp.connectionLatencyMs > 0) appendLine("Latency: ${tcp.connectionLatencyMs}ms")
            tcp.exceptionType?.let { appendLine("Exception: $it") }
            appendLine()
        }

        // SSL Info
        state.sslInfo?.let { ssl ->
            appendLine("SSL/TLS CERTIFICATE")
            appendLine("-".repeat(40))
            appendLine("Valid: ${if (ssl.isValid) "Yes" else "No"}")
            ssl.protocol?.let { appendLine("Protocol: $it") }
            ssl.issuer?.let { appendLine("Issuer: ${it.take(80)}") }
            ssl.subject?.let { appendLine("Subject: ${it.take(80)}") }
            ssl.expiryDate?.let { appendLine("Expires: $it") }
            if (ssl.isSelfSigned) appendLine("WARNING: Self-signed certificate")
            if (ssl.issuerMismatch) appendLine("WARNING: Possible proxy/MITM")
            appendLine()
        }

        // HTTP Info
        state.httpInfo?.let { http ->
            appendLine("HTTP RESPONSE")
            appendLine("-".repeat(40))
            appendLine("Status: ${http.responseCode} ${http.responseMessage ?: ""}")
            appendLine("Response Time: ${http.responseTimeMs}ms")
            http.serverHeader?.let { appendLine("Server: $it") }
            http.contentType?.let { appendLine("Content-Type: $it") }
            http.blockedByHeader?.let { appendLine("Block Header: $it") }
            appendLine()
        }

        // Blocking Factors
        val detectedFactors = state.blockingFactors.filter { it.detected }
        if (detectedFactors.isNotEmpty()) {
            appendLine("BLOCKING FACTORS")
            appendLine("-".repeat(40))
            detectedFactors.forEach { factor ->
                appendLine()
                appendLine("[${factor.importance.name}] ${factor.name}")
                appendLine("  ${factor.description}")
                factor.technicalDetail?.let { appendLine("  Detail: $it") }
            }
            appendLine()
        }

        // Exceptions
        if (state.exceptions.isNotEmpty()) {
            appendLine("EXCEPTIONS")
            appendLine("-".repeat(40))
            state.exceptions.forEach { ex ->
                appendLine("[${ex.phase}] ${ex.type}: ${ex.message ?: "No message"}")
            }
        }
    }

    private fun checkDomain() {
        val domain = _state.value.domain.trim()
        if (domain.isBlank()) {
            viewModelScope.launch {
                _events.emit(DomainEvents.ShowError("Please enter a domain"))
            }
            return
        }

        _state.update { 
            it.copy(
                isLoading = true, 
                overallStatus = ConnectionStatus.LOADING,
                dnsInfo = null,
                tcpInfo = null,
                sslInfo = null,
                httpInfo = null,
                exceptions = emptyList(),
                blockingFactors = emptyList()
            ) 
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val exceptions = mutableListOf<ExceptionInfo>()
                val blockingFactors = mutableListOf<BlockingFactor>()
                var overallStatus = ConnectionStatus.NOT_BLOCKED

                // Phase 1: DNS Resolution
                val dnsInfo = performDnsCheck(domain, exceptions, blockingFactors)
                _state.update { it.copy(dnsInfo = dnsInfo, blockingFactors = blockingFactors.toList()) }

                if (dnsInfo.ipAddresses.isEmpty()) {
                    overallStatus = ConnectionStatus.DNS_BLOCKED
                    // Note: blocking factor already added by performDnsCheck
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            overallStatus = overallStatus,
                            exceptions = exceptions,
                            blockingFactors = blockingFactors,
                            lastCheckedAt = System.currentTimeMillis()
                        ) 
                    }
                    return@withContext
                }

                // Check for blocked IPs
                val hasBlockedIp = dnsInfo.ipAnalyses.any { it.isBlocked }
                if (hasBlockedIp) {
                    overallStatus = ConnectionStatus.DNS_BLOCKED
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            overallStatus = overallStatus,
                            exceptions = exceptions,
                            blockingFactors = blockingFactors,
                            lastCheckedAt = System.currentTimeMillis()
                        ) 
                    }
                    return@withContext
                }

                // Phase 2: TCP Connection
                val tcpInfo = performTcpCheck(dnsInfo.ipAddresses.first(), exceptions, blockingFactors)
                _state.update { it.copy(tcpInfo = tcpInfo, blockingFactors = blockingFactors.toList()) }

                if (!tcpInfo.port443Reachable && !tcpInfo.port80Reachable) {
                    overallStatus = ConnectionStatus.FIREWALL_BLOCKED
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            overallStatus = overallStatus,
                            exceptions = exceptions,
                            blockingFactors = blockingFactors,
                            lastCheckedAt = System.currentTimeMillis()
                        ) 
                    }
                    return@withContext
                }

                // Phase 3: SSL Certificate Check
                val sslInfo = performSslCheck(domain, exceptions, blockingFactors)
                _state.update { it.copy(sslInfo = sslInfo, blockingFactors = blockingFactors.toList()) }

                if (sslInfo != null && !sslInfo.isValid) {
                    overallStatus = ConnectionStatus.SSL_BLOCKED
                }

                // Phase 4: HTTPS Connection
                val httpInfo = performHttpCheck(domain, exceptions, blockingFactors)
                _state.update { it.copy(httpInfo = httpInfo, blockingFactors = blockingFactors.toList()) }

                if (httpInfo != null) {
                    overallStatus = when {
                        httpInfo.responseCode in 200..399 -> ConnectionStatus.NOT_BLOCKED
                        httpInfo.responseCode == 0 -> ConnectionStatus.HTTPS_BLOCKED
                        else -> ConnectionStatus.HTTPS_BLOCKED
                    }
                }

                _state.update { 
                    it.copy(
                        isLoading = false, 
                        overallStatus = overallStatus,
                        exceptions = exceptions,
                        blockingFactors = blockingFactors,
                        lastCheckedAt = System.currentTimeMillis()
                    ) 
                }
            }
        }
    }

    private fun classifyIp(ip: String): IpClass {
        if (ip.contains(":")) {
            return if (ip == "::1") IpClass.IPV6_LOOPBACK else IpClass.IPV6
        }

        val parts = ip.split(".").mapNotNull { it.toIntOrNull() }
        if (parts.size != 4) return IpClass.UNKNOWN

        val first = parts[0]
        val second = parts[1]

        return when {
            ip == "0.0.0.0" -> IpClass.NULL_IP
            ip == "127.0.0.1" -> IpClass.LOCALHOST
            first == 127 -> IpClass.LOOPBACK
            first == 10 -> IpClass.PRIVATE_A
            first == 172 && second in 16..31 -> IpClass.PRIVATE_B
            first == 192 && second == 168 -> IpClass.PRIVATE_C
            first == 169 && second == 254 -> IpClass.LINK_LOCAL
            first in 1..126 -> IpClass.CLASS_A
            first in 128..191 -> IpClass.CLASS_B
            first in 192..223 -> IpClass.CLASS_C
            first in 224..239 -> IpClass.CLASS_D
            first in 240..255 -> IpClass.CLASS_E
            else -> IpClass.UNKNOWN
        }
    }

    private fun isBlockedIp(ipClass: IpClass): Boolean {
        return ipClass in listOf(
            IpClass.NULL_IP,
            IpClass.LOCALHOST,
            IpClass.LOOPBACK,
            IpClass.PRIVATE_A,
            IpClass.PRIVATE_B,
            IpClass.PRIVATE_C,
            IpClass.LINK_LOCAL,
            IpClass.IPV6_LOOPBACK
        )
    }

    private fun getBlockReason(ipClass: IpClass): String {
        return when (ipClass) {
            IpClass.NULL_IP -> "IP is null-routed (0.0.0.0) - Common ad blocker technique"
            IpClass.LOCALHOST -> "Redirected to localhost (127.0.0.1) - Ad blocker sinkhole"
            IpClass.LOOPBACK -> "Loopback address - Requests sent to local machine"
            IpClass.PRIVATE_A -> "Private Class A network (10.x.x.x) - Local network redirect"
            IpClass.PRIVATE_B -> "Private Class B network (172.16-31.x.x) - Enterprise blocking"
            IpClass.PRIVATE_C -> "Private Class C network (192.168.x.x) - Home router blocking"
            IpClass.LINK_LOCAL -> "Link-local address - Network misconfiguration or blocking"
            IpClass.IPV6_LOOPBACK -> "IPv6 loopback (::1) - Ad blocker sinkhole"
            else -> "Unknown blocking method"
        }
    }

    private fun performDnsCheck(
        domain: String, 
        exceptions: MutableList<ExceptionInfo>,
        blockingFactors: MutableList<BlockingFactor>
    ): DnsInfo {
        val startTime = System.currentTimeMillis()
        val threadInfo = "Thread: ${Thread.currentThread().name}, ID: ${Thread.currentThread().id}"
        
        return try {
            val addresses = InetAddress.getAllByName(domain)
            val resolutionTime = System.currentTimeMillis() - startTime

            val ipAddresses = addresses.mapNotNull { it.hostAddress }
            val canonicalHostName = addresses.firstOrNull()?.canonicalHostName
            val hasIpv6 = ipAddresses.any { it.contains(":") }

            // Determine DNS response type
            val responseType = when {
                ipAddresses.isEmpty() -> DnsResponseType.EMPTY_RESPONSE
                ipAddresses.any { blockedIpRegex.matches(it) } -> DnsResponseType.BLOCKED_IP_RETURNED
                else -> DnsResponseType.SUCCESS
            }

            // Analyze each IP
            val ipAnalyses = ipAddresses.map { ip ->
                val ipClass = classifyIp(ip)
                val isBlocked = isBlockedIp(ipClass)
                val reverseDns = try {
                    InetAddress.getByName(ip).canonicalHostName
                } catch (e: Exception) { null }

                if (isBlocked) {
                    blockingFactors.add(
                        BlockingFactor(
                            name = "Blocked IP Detected",
                            detected = true,
                            importance = Importance.CRITICAL,
                            description = getBlockReason(ipClass),
                            technicalDetail = "IP: $ip classified as ${ipClass.description}"
                        )
                    )
                }

                IpAnalysis(
                    ip = ip,
                    ipClass = ipClass,
                    isPrivate = ipClass in listOf(IpClass.PRIVATE_A, IpClass.PRIVATE_B, IpClass.PRIVATE_C),
                    isBlocked = isBlocked,
                    reverseDns = reverseDns,
                    blockReason = if (isBlocked) getBlockReason(ipClass) else null
                )
            }

            // Add blocking factor for slow DNS
            if (resolutionTime > 500) {
                blockingFactors.add(
                    BlockingFactor(
                        name = "Slow DNS Resolution",
                        detected = true,
                        importance = Importance.LOW,
                        description = "DNS took ${resolutionTime}ms - May indicate DNS filtering",
                        technicalDetail = "Normal DNS resolution is typically under 100ms"
                    )
                )
            }

            // Create debug info for success case
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = true,
                dnsResponseType = responseType,
                javaNetworkInfo = "InetAddress.getAllByName() returned ${addresses.size} address(es)",
                threadInfo = threadInfo
            )

            DnsInfo(
                ipAddresses = ipAddresses,
                canonicalHostName = canonicalHostName,
                resolutionTimeMs = resolutionTime,
                isIpv6Available = hasIpv6,
                ipAnalyses = ipAnalyses,
                debugInfo = debugInfo
            )
        } catch (e: java.net.UnknownHostException) {
            val resolutionTime = System.currentTimeMillis() - startTime
            val stackTrace = e.stackTrace.take(10).joinToString("\n") { "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            
            exceptions.add(ExceptionInfo("UnknownHostException", e.message, "DNS"))
            blockingFactors.add(
                BlockingFactor(
                    name = "DNS Lookup Failed",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = "Domain does not exist or is blocked at DNS level",
                    technicalDetail = "NXDOMAIN or SERVFAIL response from DNS server"
                )
            )
            
            // Return DnsInfo with debug info even on failure!
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = false,
                exceptionType = "java.net.UnknownHostException",
                exceptionMessage = e.message ?: "Unable to resolve host \"$domain\": No address associated with hostname",
                exceptionStackTrace = stackTrace,
                dnsResponseType = DnsResponseType.NXDOMAIN,
                javaNetworkInfo = buildString {
                    appendLine("InetAddress.getAllByName(\"$domain\") threw UnknownHostException")
                    appendLine("Message: ${e.message}")
                    appendLine("Cause: ${e.cause?.toString() ?: "null"}")
                    appendLine("LocalizedMessage: ${e.localizedMessage}")
                },
                threadInfo = threadInfo
            )
            
            DnsInfo(
                ipAddresses = emptyList(),
                resolutionTimeMs = resolutionTime,
                debugInfo = debugInfo
            )
        } catch (e: java.net.SocketTimeoutException) {
            val resolutionTime = System.currentTimeMillis() - startTime
            val stackTrace = e.stackTrace.take(10).joinToString("\n") { "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            
            exceptions.add(ExceptionInfo("SocketTimeoutException", e.message, "DNS"))
            blockingFactors.add(
                BlockingFactor(
                    name = "DNS Query Timeout",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = "DNS query timed out - Network level blocking",
                    technicalDetail = "No response from DNS server within timeout period"
                )
            )
            
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = false,
                exceptionType = "java.net.SocketTimeoutException",
                exceptionMessage = e.message ?: "DNS query timed out",
                exceptionStackTrace = stackTrace,
                dnsResponseType = DnsResponseType.TIMEOUT,
                javaNetworkInfo = "DNS lookup exceeded timeout threshold",
                threadInfo = threadInfo
            )
            
            DnsInfo(
                ipAddresses = emptyList(),
                resolutionTimeMs = resolutionTime,
                debugInfo = debugInfo
            )
        } catch (e: java.net.SocketException) {
            val resolutionTime = System.currentTimeMillis() - startTime
            val stackTrace = e.stackTrace.take(10).joinToString("\n") { "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            
            exceptions.add(ExceptionInfo("SocketException", e.message, "DNS"))
            
            val responseType = when {
                e.message?.contains("Network is unreachable") == true -> DnsResponseType.NETWORK_UNREACHABLE
                e.message?.contains("refused") == true -> DnsResponseType.REFUSED
                else -> DnsResponseType.SERVFAIL
            }
            
            blockingFactors.add(
                BlockingFactor(
                    name = "DNS Network Error",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = responseType.description,
                    technicalDetail = responseType.developerHint
                )
            )
            
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = false,
                exceptionType = "java.net.SocketException",
                exceptionMessage = e.message,
                exceptionStackTrace = stackTrace,
                dnsResponseType = responseType,
                javaNetworkInfo = "Socket-level failure during DNS resolution",
                threadInfo = threadInfo
            )
            
            DnsInfo(
                ipAddresses = emptyList(),
                resolutionTimeMs = resolutionTime,
                debugInfo = debugInfo
            )
        } catch (e: SecurityException) {
            val resolutionTime = System.currentTimeMillis() - startTime
            val stackTrace = e.stackTrace.take(10).joinToString("\n") { "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            
            exceptions.add(ExceptionInfo("SecurityException", e.message, "DNS"))
            blockingFactors.add(
                BlockingFactor(
                    name = "DNS Permission Denied",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = "Security manager blocked DNS query",
                    technicalDetail = "App may be missing INTERNET permission or security policy blocks DNS"
                )
            )
            
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = false,
                exceptionType = "java.lang.SecurityException",
                exceptionMessage = e.message,
                exceptionStackTrace = stackTrace,
                dnsResponseType = DnsResponseType.REFUSED,
                javaNetworkInfo = "SecurityManager checkConnect() denied access",
                threadInfo = threadInfo
            )
            
            DnsInfo(
                ipAddresses = emptyList(),
                resolutionTimeMs = resolutionTime,
                debugInfo = debugInfo
            )
        } catch (e: Exception) {
            val resolutionTime = System.currentTimeMillis() - startTime
            val stackTrace = e.stackTrace.take(10).joinToString("\n") { "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            
            exceptions.add(ExceptionInfo(e::class.simpleName ?: "Exception", e.message, "DNS"))
            
            val debugInfo = DnsDebugInfo(
                queryDomain = domain,
                queryTimeMs = resolutionTime,
                resolved = false,
                exceptionType = e::class.qualifiedName ?: "Unknown",
                exceptionMessage = e.message,
                exceptionStackTrace = stackTrace,
                dnsResponseType = DnsResponseType.UNKNOWN,
                javaNetworkInfo = "Unexpected exception during DNS resolution: ${e::class.simpleName}",
                threadInfo = threadInfo
            )
            
            DnsInfo(
                ipAddresses = emptyList(),
                resolutionTimeMs = resolutionTime,
                debugInfo = debugInfo
            )
        }
    }

    private fun performTcpCheck(
        ip: String, 
        exceptions: MutableList<ExceptionInfo>,
        blockingFactors: MutableList<BlockingFactor>
    ): TcpInfo {
        var port443Reachable = false
        var port80Reachable = false
        var latency = 0L
        var exceptionType: String? = null

        // Check port 443
        try {
            val startTime = System.currentTimeMillis()
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, 443), 5000)
                latency = System.currentTimeMillis() - startTime
                port443Reachable = true
            }
        } catch (e: java.net.ConnectException) {
            exceptionType = "ConnectException"
            exceptions.add(ExceptionInfo("ConnectException", e.message, "TCP:443"))
            blockingFactors.add(
                BlockingFactor(
                    name = "HTTPS Port Blocked",
                    detected = true,
                    importance = Importance.HIGH,
                    description = "Connection to port 443 refused - Firewall blocking",
                    technicalDetail = "TCP RST received or connection refused"
                )
            )
        } catch (e: java.net.SocketTimeoutException) {
            exceptionType = "SocketTimeoutException"
            exceptions.add(ExceptionInfo("SocketTimeoutException", e.message, "TCP:443"))
            blockingFactors.add(
                BlockingFactor(
                    name = "Connection Timeout",
                    detected = true,
                    importance = Importance.HIGH,
                    description = "Connection timed out - Possible deep packet inspection",
                    technicalDetail = "No response within 5 seconds, packets may be dropped silently"
                )
            )
        } catch (e: Exception) {
            exceptionType = e::class.simpleName
            exceptions.add(ExceptionInfo(e::class.simpleName ?: "Exception", e.message, "TCP:443"))
        }

        // Check port 80
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, 80), 5000)
                port80Reachable = true
            }
        } catch (e: Exception) {
            exceptions.add(ExceptionInfo(e::class.simpleName ?: "Exception", e.message, "TCP:80"))
        }

        // Add factor for high latency
        if (port443Reachable && latency > 300) {
            blockingFactors.add(
                BlockingFactor(
                    name = "High Connection Latency",
                    detected = true,
                    importance = Importance.MEDIUM,
                    description = "TCP connection took ${latency}ms - Possible traffic shaping",
                    technicalDetail = "Normal TCP handshake is typically under 100ms for nearby servers"
                )
            )
        }

        return TcpInfo(
            port443Reachable = port443Reachable,
            port80Reachable = port80Reachable,
            connectionLatencyMs = latency,
            exceptionType = exceptionType
        )
    }

    private fun performSslCheck(
        domain: String, 
        exceptions: MutableList<ExceptionInfo>,
        blockingFactors: MutableList<BlockingFactor>
    ): SslInfo? {
        return try {
            val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
            (factory.createSocket(domain, 443) as SSLSocket).use { socket ->
                socket.soTimeout = 10000
                socket.startHandshake()

                val session = socket.session
                val certs = session.peerCertificates
                val x509Cert = certs.firstOrNull() as? X509Certificate

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                
                val issuer = x509Cert?.issuerDN?.name
                val subject = x509Cert?.subjectDN?.name
                
                // Check for self-signed
                val isSelfSigned = issuer == subject
                if (isSelfSigned) {
                    blockingFactors.add(
                        BlockingFactor(
                            name = "Self-Signed Certificate",
                            detected = true,
                            importance = Importance.CRITICAL,
                            description = "Certificate is self-signed - Possible MITM proxy",
                            technicalDetail = "Issuer and Subject are identical, not from trusted CA"
                        )
                    )
                }

                // Check for corporate proxy indicators
                val isCorpProxy = issuer?.contains("proxy", ignoreCase = true) == true ||
                        issuer?.contains("firewall", ignoreCase = true) == true ||
                        issuer?.contains("security", ignoreCase = true) == true

                if (isCorpProxy) {
                    blockingFactors.add(
                        BlockingFactor(
                            name = "Corporate Proxy Detected",
                            detected = true,
                            importance = Importance.HIGH,
                            description = "Certificate issued by corporate security infrastructure",
                            technicalDetail = "Issuer contains proxy/firewall/security keywords"
                        )
                    )
                }

                SslInfo(
                    isValid = true,
                    issuer = issuer,
                    subject = subject,
                    protocol = session.protocol,
                    expiryDate = x509Cert?.notAfter?.let { dateFormat.format(it) },
                    subjectAlternativeNames = x509Cert?.subjectAlternativeNames
                        ?.mapNotNull { it.getOrNull(1)?.toString() }
                        ?: emptyList(),
                    isSelfSigned = isSelfSigned,
                    issuerMismatch = isCorpProxy
                )
            }
        } catch (e: javax.net.ssl.SSLHandshakeException) {
            exceptions.add(ExceptionInfo("SSLHandshakeException", e.message, "SSL"))
            blockingFactors.add(
                BlockingFactor(
                    name = "SSL Handshake Failed",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = "TLS handshake failed - Certificate rejected or MITM detected",
                    technicalDetail = e.message ?: "SSL handshake error"
                )
            )
            SslInfo(isValid = false)
        } catch (e: javax.net.ssl.SSLPeerUnverifiedException) {
            exceptions.add(ExceptionInfo("SSLPeerUnverifiedException", e.message, "SSL"))
            blockingFactors.add(
                BlockingFactor(
                    name = "Certificate Verification Failed",
                    detected = true,
                    importance = Importance.CRITICAL,
                    description = "Server certificate could not be verified - MITM attack possible",
                    technicalDetail = "Certificate chain validation failed"
                )
            )
            SslInfo(isValid = false)
        } catch (e: Exception) {
            exceptions.add(ExceptionInfo(e::class.simpleName ?: "Exception", e.message, "SSL"))
            null
        }
    }

    private fun performHttpCheck(
        domain: String, 
        exceptions: MutableList<ExceptionInfo>,
        blockingFactors: MutableList<BlockingFactor>
    ): HttpInfo? {
        return try {
            val startTime = System.currentTimeMillis()
            val url = URL("https://$domain")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.instanceFollowRedirects = true

            val responseCode = connection.responseCode
            val responseTime = System.currentTimeMillis() - startTime

            // Check for block page indicators in headers
            val blockedByHeader = connection.getHeaderField("X-Blocked-By")
                ?: connection.getHeaderField("X-Block-Reason")
                ?: connection.getHeaderField("X-Squid-Error")

            if (blockedByHeader != null) {
                blockingFactors.add(
                    BlockingFactor(
                        name = "Block Header Detected",
                        detected = true,
                        importance = Importance.CRITICAL,
                        description = "Server returned blocking header: $blockedByHeader",
                        technicalDetail = "HTTP response contains explicit block indicator"
                    )
                )
            }

            // Check response codes
            when (responseCode) {
                403 -> blockingFactors.add(
                    BlockingFactor(
                        name = "Access Forbidden",
                        detected = true,
                        importance = Importance.HIGH,
                        description = "HTTP 403 Forbidden - Access denied by server or proxy",
                        technicalDetail = "Server explicitly refused the request"
                    )
                )
                502, 503 -> blockingFactors.add(
                    BlockingFactor(
                        name = "Gateway Error",
                        detected = true,
                        importance = Importance.MEDIUM,
                        description = "HTTP $responseCode - Upstream server error or blocking",
                        technicalDetail = "Proxy or gateway returned an error"
                    )
                )
            }

            val httpInfo = HttpInfo(
                responseCode = responseCode,
                responseMessage = connection.responseMessage,
                serverHeader = connection.getHeaderField("Server"),
                responseTimeMs = responseTime,
                contentType = connection.contentType,
                blockedByHeader = blockedByHeader
            )

            connection.disconnect()
            httpInfo
        } catch (e: Exception) {
            exceptions.add(ExceptionInfo(e::class.simpleName ?: "Exception", e.message, "HTTP"))
            blockingFactors.add(
                BlockingFactor(
                    name = "HTTPS Request Failed",
                    detected = true,
                    importance = Importance.HIGH,
                    description = "Could not complete HTTPS request",
                    technicalDetail = e.message ?: "Unknown HTTP error"
                )
            )
            HttpInfo(responseCode = 0, responseMessage = e.message)
        }
    }
}
