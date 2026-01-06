package com.vishal2376.echo.domain_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.echo.domain_screen.state.ConnectionStatus
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.F1Accent
import com.vishal2376.echo.ui.theme.StatusColors

@Composable
fun DomainInputCard(
    domain: String,
    isLoading: Boolean,
    onDomainChange: (String) -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CatppuccinMocha.Surface0.copy(alpha = 0.7f),
                        CatppuccinMocha.Base
                    )
                )
            )
            .border(
                width = 1.dp,
                color = CatppuccinMocha.Surface1,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // Input Field
            TextField(
                value = domain,
                onValueChange = onDomainChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Enter domain...",
                        color = CatppuccinMocha.Overlay0
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        focusManager.clearFocus()
                        onCheckClick()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CatppuccinMocha.Surface0,
                    unfocusedContainerColor = CatppuccinMocha.Mantle,
                    focusedTextColor = CatppuccinMocha.Text,
                    unfocusedTextColor = CatppuccinMocha.Text,
                    cursorColor = F1Accent.Primary,
                    focusedIndicatorColor = F1Accent.Primary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Check Button with F1 Red
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onCheckClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && domain.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = F1Accent.Primary,
                    disabledContainerColor = CatppuccinMocha.Surface1
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Check",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ANALYZE",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText) = when (status) {
        ConnectionStatus.LOADING -> Pair(CatppuccinMocha.Peach, "Checking...")
        ConnectionStatus.NOT_BLOCKED -> Pair(CatppuccinMocha.Green, "Not Blocked")
        ConnectionStatus.DNS_BLOCKED -> Pair(F1Accent.Primary, "DNS Blocked")
        ConnectionStatus.FIREWALL_BLOCKED -> Pair(F1Accent.Primary, "Firewall Blocked")
        ConnectionStatus.SSL_BLOCKED -> Pair(F1Accent.Primary, "SSL Blocked")
        ConnectionStatus.HTTPS_BLOCKED -> Pair(F1Accent.Primary, "HTTPS Blocked")
        ConnectionStatus.TIMEOUT -> Pair(CatppuccinMocha.Yellow, "Timeout")
        ConnectionStatus.MITM_DETECTED -> Pair(F1Accent.Primary, "MITM Detected")
        ConnectionStatus.UNKNOWN_ERROR -> Pair(CatppuccinMocha.Yellow, "Error")
        ConnectionStatus.IDLE -> Pair(CatppuccinMocha.Overlay0, "Ready")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CatppuccinMocha.Surface0.copy(alpha = 0.7f),
                        CatppuccinMocha.Base
                    )
                )
            )
            .border(
                width = 1.dp,
                color = CatppuccinMocha.Surface1,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status accent bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(statusColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Status",
                style = MaterialTheme.typography.bodyMedium,
                color = CatppuccinMocha.Overlay1
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}
