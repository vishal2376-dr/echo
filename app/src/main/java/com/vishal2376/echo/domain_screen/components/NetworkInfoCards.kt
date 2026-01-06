package com.vishal2376.echo.domain_screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal2376.echo.domain_screen.state.DnsInfo
import com.vishal2376.echo.domain_screen.state.HttpInfo
import com.vishal2376.echo.domain_screen.state.SslInfo
import com.vishal2376.echo.domain_screen.state.TcpInfo
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.StatusColors

@Composable
fun DnsInfoCard(
    dnsInfo: DnsInfo,
    modifier: Modifier = Modifier
) {
    InfoCard(
        title = "DNS Information",
        accentColor = CatppuccinMocha.Blue,
        modifier = modifier
    ) {
        InfoRow(
            label = "IP Addresses",
            value = dnsInfo.ipAddresses.joinToString("\n"),
            isMono = true
        )

        // Show IP analyses if available
        dnsInfo.ipAnalyses.forEach { analysis ->
            InfoDivider()
            InfoRow(
                label = "IP Class",
                value = analysis.ipClass.description,
                valueColor = if (analysis.isBlocked) StatusColors.Error else CatppuccinMocha.Text
            )
            if (analysis.isPrivate) {
                InfoRow(
                    label = "Type",
                    value = "⚠️ Private Network",
                    valueColor = StatusColors.Warning
                )
            }
            if (analysis.isBlocked && analysis.blockReason != null) {
                InfoRow(
                    label = "Block Reason",
                    value = analysis.blockReason,
                    valueColor = StatusColors.Error
                )
            }
            if (analysis.reverseDns != null && analysis.reverseDns != analysis.ip) {
                InfoRow(
                    label = "Reverse DNS",
                    value = analysis.reverseDns,
                    isMono = true
                )
            }
        }

        if (dnsInfo.canonicalHostName != null) {
            InfoDivider()
            InfoRow(
                label = "Canonical Name",
                value = dnsInfo.canonicalHostName,
                isMono = true
            )
        }

        InfoRow(
            label = "Resolution Time",
            value = "${dnsInfo.resolutionTimeMs}ms",
            valueColor = if (dnsInfo.resolutionTimeMs < 100) StatusColors.Success
            else if (dnsInfo.resolutionTimeMs < 500) StatusColors.Warning
            else StatusColors.Error
        )

        InfoRow(
            label = "IPv6 Available",
            value = if (dnsInfo.isIpv6Available) "Yes ✓" else "No",
            valueColor = if (dnsInfo.isIpv6Available) StatusColors.Success else CatppuccinMocha.Overlay1
        )
    }
}

@Composable
fun TcpInfoCard(
    tcpInfo: TcpInfo,
    modifier: Modifier = Modifier
) {
    InfoCard(
        title = "TCP Connection",
        accentColor = CatppuccinMocha.Teal,
        modifier = modifier
    ) {
        InfoRow(
            label = "Port 443 (HTTPS)",
            value = if (tcpInfo.port443Reachable) "✓ Reachable" else "✗ Blocked",
            valueColor = if (tcpInfo.port443Reachable) StatusColors.Success else StatusColors.Error
        )

        InfoRow(
            label = "Port 80 (HTTP)",
            value = if (tcpInfo.port80Reachable) "✓ Reachable" else "✗ Blocked",
            valueColor = if (tcpInfo.port80Reachable) StatusColors.Success else StatusColors.Error
        )

        if (tcpInfo.connectionLatencyMs > 0) {
            InfoRow(
                label = "Connection Latency",
                value = "${tcpInfo.connectionLatencyMs}ms",
                valueColor = if (tcpInfo.connectionLatencyMs < 100) StatusColors.Success
                else if (tcpInfo.connectionLatencyMs < 300) StatusColors.Warning
                else StatusColors.Error
            )
        }

        if (tcpInfo.exceptionType != null) {
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(
                label = "Exception",
                value = tcpInfo.exceptionType,
                valueColor = StatusColors.Error,
                isMono = true
            )
        }
    }
}

@Composable
fun SslInfoCard(
    sslInfo: SslInfo,
    modifier: Modifier = Modifier
) {
    InfoCard(
        title = "SSL/TLS Certificate",
        accentColor = CatppuccinMocha.Green,
        modifier = modifier
    ) {
        InfoRow(
            label = "Valid",
            value = if (sslInfo.isValid) "✓ Yes" else "✗ No",
            valueColor = if (sslInfo.isValid) StatusColors.Success else StatusColors.Error
        )

        if (sslInfo.isSelfSigned) {
            InfoRow(
                label = "Self-Signed",
                value = "⚠️ Yes - MITM Risk!",
                valueColor = StatusColors.Error
            )
        }

        if (sslInfo.issuerMismatch) {
            InfoRow(
                label = "Proxy Detected",
                value = "⚠️ Corporate/Security proxy",
                valueColor = StatusColors.Warning
            )
        }

        if (sslInfo.protocol != null) {
            InfoRow(
                label = "Protocol",
                value = sslInfo.protocol,
                valueColor = if (sslInfo.protocol.contains("1.3")) StatusColors.Success
                else if (sslInfo.protocol.contains("1.2")) CatppuccinMocha.Text
                else StatusColors.Warning
            )
        }

        if (sslInfo.issuer != null) {
            InfoDivider()
            InfoRow(
                label = "Issuer",
                value = sslInfo.issuer.take(60) + if (sslInfo.issuer.length > 60) "..." else "",
                isMono = true
            )
        }

        if (sslInfo.subject != null) {
            InfoRow(
                label = "Subject",
                value = sslInfo.subject.take(60) + if (sslInfo.subject.length > 60) "..." else "",
                isMono = true
            )
        }

        if (sslInfo.expiryDate != null) {
            InfoRow(
                label = "Expires",
                value = sslInfo.expiryDate
            )
        }

        if (sslInfo.subjectAlternativeNames.isNotEmpty()) {
            InfoDivider()
            InfoRow(
                label = "SANs",
                value = sslInfo.subjectAlternativeNames.take(5).joinToString("\n") +
                        if (sslInfo.subjectAlternativeNames.size > 5) "\n+${sslInfo.subjectAlternativeNames.size - 5} more" else "",
                isMono = true
            )
        }
    }
}

@Composable
fun HttpInfoCard(
    httpInfo: HttpInfo,
    modifier: Modifier = Modifier
) {
    InfoCard(
        title = "HTTP Response",
        accentColor = CatppuccinMocha.Peach,
        modifier = modifier
    ) {
        InfoRow(
            label = "Status Code",
            value = "${httpInfo.responseCode} ${httpInfo.responseMessage ?: ""}",
            valueColor = when {
                httpInfo.responseCode in 200..299 -> StatusColors.Success
                httpInfo.responseCode in 300..399 -> StatusColors.Warning
                else -> StatusColors.Error
            }
        )

        if (httpInfo.blockedByHeader != null) {
            InfoRow(
                label = "Block Header",
                value = "⚠️ ${httpInfo.blockedByHeader}",
                valueColor = StatusColors.Error
            )
        }

        InfoRow(
            label = "Response Time",
            value = "${httpInfo.responseTimeMs}ms",
            valueColor = if (httpInfo.responseTimeMs < 500) StatusColors.Success
            else if (httpInfo.responseTimeMs < 2000) StatusColors.Warning
            else StatusColors.Error
        )

        if (httpInfo.serverHeader != null) {
            InfoRow(
                label = "Server",
                value = httpInfo.serverHeader,
                isMono = true
            )
        }

        if (httpInfo.contentType != null) {
            InfoRow(
                label = "Content-Type",
                value = httpInfo.contentType,
                isMono = true
            )
        }
    }
}
