package com.vishal2376.echo.domain_screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vishal2376.echo.domain_screen.action.DomainActions
import com.vishal2376.echo.domain_screen.components.BlockingFactorsCard
import com.vishal2376.echo.domain_screen.components.ConnectionStatusCard
import com.vishal2376.echo.domain_screen.components.DnsDebugCard
import com.vishal2376.echo.domain_screen.components.DnsInfoCard
import com.vishal2376.echo.domain_screen.components.DomainInputCard
import com.vishal2376.echo.domain_screen.components.ExceptionsCard
import com.vishal2376.echo.domain_screen.components.F1TopBar
import com.vishal2376.echo.domain_screen.components.HttpInfoCard
import com.vishal2376.echo.domain_screen.components.SslInfoCard
import com.vishal2376.echo.domain_screen.components.TcpInfoCard
import com.vishal2376.echo.domain_screen.events.DomainEvents
import com.vishal2376.echo.domain_screen.state.ConnectionStatus
import com.vishal2376.echo.domain_screen.viewmodel.DomainViewModel
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.F1Accent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DomainScreen(
    viewModel: DomainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DomainEvents.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is DomainEvents.CopiedToClipboard -> {
                    snackbarHostState.showSnackbar("Copied: ${event.content}")
                }
                is DomainEvents.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is DomainEvents.ShareText -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.text)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CatppuccinMocha.Mantle
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CatppuccinMocha.Mantle)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // F1 Style Top Bar
                F1TopBar()
                
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Domain Input
                    DomainInputCard(
                        domain = state.domain,
                        isLoading = state.isLoading,
                        onDomainChange = { viewModel.onAction(DomainActions.UpdateDomain(it)) },
                        onCheckClick = { viewModel.onAction(DomainActions.CheckDomain) }
                    )

                    // Quick Tracker Checks
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(
                            text = "QUICK CHECK",
                            style = MaterialTheme.typography.labelSmall,
                            color = CatppuccinMocha.Overlay1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            SuggestionChip(
                                onClick = {
                                    viewModel.onAction(DomainActions.UpdateDomain("app-measurement.com"))
                                    viewModel.onAction(DomainActions.CheckDomain)
                                },
                                label = { Text("Firebase Analytics", color = CatppuccinMocha.Text) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CatppuccinMocha.Surface0
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = F1Accent.Primary.copy(alpha = 0.5f)
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    viewModel.onAction(DomainActions.UpdateDomain("api.mixpanel.com"))
                                    viewModel.onAction(DomainActions.CheckDomain)
                                },
                                label = { Text("Mixpanel API", color = CatppuccinMocha.Text) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CatppuccinMocha.Surface0
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = F1Accent.Primary.copy(alpha = 0.5f)
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    viewModel.onAction(DomainActions.UpdateDomain("api.clevertap.com"))
                                    viewModel.onAction(DomainActions.CheckDomain)
                                },
                                label = { Text("CleverTap API", color = CatppuccinMocha.Text) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CatppuccinMocha.Surface0
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = F1Accent.Primary.copy(alpha = 0.5f)
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    viewModel.onAction(DomainActions.UpdateDomain("graph.facebook.com"))
                                    viewModel.onAction(DomainActions.CheckDomain)
                                },
                                label = { Text("Facebook Graph", color = CatppuccinMocha.Text) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CatppuccinMocha.Surface0
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = F1Accent.Primary.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    // Connection Status
                    AnimatedVisibility(
                        visible = state.overallStatus != ConnectionStatus.IDLE,
                        enter = fadeIn() + slideInVertically { -it / 2 },
                        exit = fadeOut()
                    ) {
                        ConnectionStatusCard(status = state.overallStatus)
                    }

                    // Blocking Factors (Most Important!)
                    AnimatedVisibility(
                        visible = state.blockingFactors.isNotEmpty(),
                        enter = fadeIn() + slideInVertically { -it / 2 }
                    ) {
                        BlockingFactorsCard(factors = state.blockingFactors)
                    }

                    // DNS Info
                    state.dnsInfo?.let { dnsInfo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { -it / 2 }
                        ) {
                            DnsInfoCard(dnsInfo = dnsInfo)
                        }
                        
                        // Developer DNS Debug Info
                        dnsInfo.debugInfo?.let { debugInfo ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically { -it / 2 }
                            ) {
                                DnsDebugCard(debugInfo = debugInfo)
                            }
                        }
                    }

                    // TCP Info
                    state.tcpInfo?.let { tcpInfo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { -it / 2 }
                        ) {
                            TcpInfoCard(tcpInfo = tcpInfo)
                        }
                    }

                    // SSL Info
                    state.sslInfo?.let { sslInfo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { -it / 2 }
                        ) {
                            SslInfoCard(sslInfo = sslInfo)
                        }
                    }

                    // HTTP Info
                    state.httpInfo?.let { httpInfo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { -it / 2 }
                        ) {
                            HttpInfoCard(httpInfo = httpInfo)
                        }
                    }

                    // Exceptions
                    if (state.exceptions.isNotEmpty()) {
                        ExceptionsCard(exceptions = state.exceptions)
                    }

                    // Share Button
                    AnimatedVisibility(
                        visible = state.overallStatus != ConnectionStatus.IDLE && 
                                  state.overallStatus != ConnectionStatus.LOADING,
                        enter = fadeIn() + slideInVertically { -it / 2 }
                    ) {
                        Button(
                            onClick = { viewModel.onAction(DomainActions.ShareResults) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = F1Accent.Primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SHARE REPORT",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
