package com.vishal2376.echo.domain_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vishal2376.echo.domain_screen.state.ExceptionInfo
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.StatusColors

@Composable
fun ExceptionsCard(
    exceptions: List<ExceptionInfo>,
    modifier: Modifier = Modifier
) {
    if (exceptions.isEmpty()) return

    InfoCard(
        title = "Exceptions (${exceptions.size})",
        accentColor = CatppuccinMocha.Red,
        modifier = modifier,
        initiallyExpanded = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            exceptions.forEachIndexed { index, exception ->
                InfoRow(
                    label = "[${exception.phase}]",
                    value = exception.type,
                    valueColor = StatusColors.Error,
                    isMono = true
                )
                if (exception.message != null) {
                    InfoRow(
                        label = "",
                        value = exception.message,
                        valueColor = CatppuccinMocha.Subtext0,
                        isMono = true
                    )
                }
                if (index < exceptions.lastIndex) {
                    InfoDivider()
                }
            }
        }
    }
}
