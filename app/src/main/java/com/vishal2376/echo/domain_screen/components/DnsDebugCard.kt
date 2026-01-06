package com.vishal2376.echo.domain_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.echo.domain_screen.state.DnsDebugInfo
import com.vishal2376.echo.domain_screen.state.DnsResponseType
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.StatusColors

@Composable
fun DnsDebugCard(
    debugInfo: DnsDebugInfo,
    modifier: Modifier = Modifier
) {
    val accentColor = if (debugInfo.resolved) CatppuccinMocha.Green else CatppuccinMocha.Red

    InfoCard(
        title = "Developer DNS Debug",
        accentColor = CatppuccinMocha.Lavender,
        modifier = modifier,
        initiallyExpanded = !debugInfo.resolved // Auto-expand if failed
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Query Info Section
            DebugSection(title = "Query Info") {
                DebugRow("Domain", debugInfo.queryDomain)
                DebugRow("Query Time", "${debugInfo.queryTimeMs}ms")
                DebugRow("Resolved", if (debugInfo.resolved) "✓ Yes" else "✗ No", 
                    if (debugInfo.resolved) StatusColors.Success else StatusColors.Error)
            }

            // DNS Response Type
            DebugSection(title = "DNS Response") {
                DebugRow("Type", debugInfo.dnsResponseType.name)
                DebugRow("Description", debugInfo.dnsResponseType.description)
                
                // Developer hint in a code box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CatppuccinMocha.Mantle)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "💡 ${debugInfo.dnsResponseType.developerHint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CatppuccinMocha.Subtext1,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            // Exception Info (if failed)
            if (debugInfo.exceptionType != null) {
                DebugSection(title = "Exception Details") {
                    DebugRow("Type", debugInfo.exceptionType, StatusColors.Error)
                    debugInfo.exceptionMessage?.let {
                        DebugRow("Message", it)
                    }
                }

                // Stack Trace
                debugInfo.exceptionStackTrace?.let { stackTrace ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stack Trace",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CatppuccinMocha.Overlay1
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CatppuccinMocha.Crust)
                            .horizontalScroll(rememberScrollState())
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stackTrace,
                            style = MaterialTheme.typography.bodySmall,
                            color = CatppuccinMocha.Red,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Java Network Info
            debugInfo.javaNetworkInfo?.let {
                DebugSection(title = "Java Network Info") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CatppuccinMocha.Mantle)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = CatppuccinMocha.Subtext0,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Thread Info
            debugInfo.threadInfo?.let {
                DebugRow("Thread", it, CatppuccinMocha.Overlay1)
            }
        }
    }
}

@Composable
private fun DebugSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = CatppuccinMocha.Mauve
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun DebugRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = CatppuccinMocha.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = CatppuccinMocha.Overlay1,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = valueColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.65f)
        )
    }
}
