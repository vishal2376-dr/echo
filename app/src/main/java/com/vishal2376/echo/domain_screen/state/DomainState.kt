package com.vishal2376.echo.domain_screen.state

/**
 * Connection check status
 */
enum class ConnectionStatus {
    IDLE,
    LOADING,
    NOT_BLOCKED,
    DNS_BLOCKED,
    FIREWALL_BLOCKED,
    SSL_BLOCKED,
    HTTPS_BLOCKED,
    TIMEOUT,
    MITM_DETECTED,
    UNKNOWN_ERROR
}

/**
 * IP Address Classification
 */
enum class IpClass(val description: String) {
    CLASS_A("Class A (1.0.0.0 - 127.255.255.255)"),
    CLASS_B("Class B (128.0.0.0 - 191.255.255.255)"),
    CLASS_C("Class C (192.0.0.0 - 223.255.255.255)"),
    CLASS_D("Class D Multicast (224.0.0.0 - 239.255.255.255)"),
    CLASS_E("Class E Reserved (240.0.0.0 - 255.255.255.255)"),
    LOOPBACK("Loopback (127.x.x.x)"),
    PRIVATE_A("Private Class A (10.x.x.x)"),
    PRIVATE_B("Private Class B (172.16-31.x.x)"),
    PRIVATE_C("Private Class C (192.168.x.x)"),
    LINK_LOCAL("Link-Local (169.254.x.x)"),
    LOCALHOST("Localhost (127.0.0.1)"),
    NULL_IP("Null/Blocked (0.0.0.0)"),
    IPV6_LOOPBACK("IPv6 Loopback (::1)"),
    IPV6("IPv6 Address"),
    UNKNOWN("Unknown")
}

/**
 * Blocking factor with importance level and description
 */
data class BlockingFactor(
    val name: String,
    val detected: Boolean,
    val importance: Importance,
    val description: String,
    val technicalDetail: String? = null
)

enum class Importance {
    CRITICAL,  // Definite ad blocker indicator
    HIGH,      // Strong indicator
    MEDIUM,    // Moderate indicator
    LOW        // Weak indicator
}

/**
 * IP Analysis Result
 */
data class IpAnalysis(
    val ip: String,
    val ipClass: IpClass,
    val isPrivate: Boolean,
    val isBlocked: Boolean,
    val reverseDns: String? = null,
    val blockReason: String? = null
)

/**
 * Developer-level DNS debugging information
 */
data class DnsDebugInfo(
    val queryDomain: String,
    val queryTimeMs: Long,
    val resolved: Boolean,
    val exceptionType: String? = null,
    val exceptionMessage: String? = null,
    val exceptionStackTrace: String? = null,
    val dnsResponseType: DnsResponseType = DnsResponseType.UNKNOWN,
    val systemDnsServers: List<String> = emptyList(),
    val javaNetworkInfo: String? = null,
    val threadInfo: String? = null
)

enum class DnsResponseType(val description: String, val developerHint: String) {
    SUCCESS("Resolved successfully", "DNS query returned valid IP addresses"),
    NXDOMAIN("Domain does not exist", "DNS server responded that domain doesn't exist - Could be DNS-level blocking via null zone"),
    SERVFAIL("Server failure", "DNS server encountered an error - Could indicate upstream blocking"),
    REFUSED("Query refused", "DNS server refused to answer - Explicit blocking by DNS provider"),
    TIMEOUT("Query timed out", "No response from DNS server - Network level blocking or DNS server down"),
    NETWORK_UNREACHABLE("Network unreachable", "Cannot reach DNS server - Check network connectivity"),
    EMPTY_RESPONSE("Empty response", "DNS returned no records - Possible NODATA response or blocking"),
    BLOCKED_IP_RETURNED("Blocked IP returned", "DNS returned sinkhole IP (0.0.0.0, 127.0.0.1, etc.) - Ad blocker active"),
    UNKNOWN("Unknown", "Unexpected error during DNS resolution")
}

/**
 * DNS resolution information
 */
data class DnsInfo(
    val ipAddresses: List<String> = emptyList(),
    val canonicalHostName: String? = null,
    val resolutionTimeMs: Long = 0,
    val isIpv6Available: Boolean = false,
    val ipAnalyses: List<IpAnalysis> = emptyList(),
    val debugInfo: DnsDebugInfo? = null
)

/**
 * TCP connection information
 */
data class TcpInfo(
    val port443Reachable: Boolean = false,
    val port80Reachable: Boolean = false,
    val connectionLatencyMs: Long = 0,
    val exceptionType: String? = null
)

/**
 * SSL/TLS certificate information
 */
data class SslInfo(
    val isValid: Boolean = false,
    val issuer: String? = null,
    val subject: String? = null,
    val protocol: String? = null,
    val expiryDate: String? = null,
    val subjectAlternativeNames: List<String> = emptyList(),
    val isSelfSigned: Boolean = false,
    val issuerMismatch: Boolean = false
)

/**
 * HTTP response information
 */
data class HttpInfo(
    val responseCode: Int = 0,
    val responseMessage: String? = null,
    val serverHeader: String? = null,
    val responseTimeMs: Long = 0,
    val contentType: String? = null,
    val blockedByHeader: String? = null
)

/**
 * Exception details for debugging
 */
data class ExceptionInfo(
    val type: String,
    val message: String?,
    val phase: String // DNS, TCP, SSL, HTTP
)

/**
 * Main screen state - single source of truth
 */
data class DomainState(
    val domain: String = "google.com",
    val isLoading: Boolean = false,
    val overallStatus: ConnectionStatus = ConnectionStatus.IDLE,
    val dnsInfo: DnsInfo? = null,
    val tcpInfo: TcpInfo? = null,
    val sslInfo: SslInfo? = null,
    val httpInfo: HttpInfo? = null,
    val exceptions: List<ExceptionInfo> = emptyList(),
    val blockingFactors: List<BlockingFactor> = emptyList(),
    val lastCheckedAt: Long? = null
)

