package com.vishal2376.echo.domain_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.echo.domain_screen.state.BlockingFactor
import com.vishal2376.echo.domain_screen.state.Importance
import com.vishal2376.echo.ui.theme.CatppuccinMocha

@Composable
fun BlockingFactorsCard(
    factors: List<BlockingFactor>,
    modifier: Modifier = Modifier
) {
    if (factors.isEmpty()) return

    val detectedFactors = factors.filter { it.detected }
    if (detectedFactors.isEmpty()) return

    InfoCard(
        title = "Blocking Factors Detected (${detectedFactors.size})",
        accentColor = CatppuccinMocha.Red,
        modifier = modifier,
        initiallyExpanded = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            detectedFactors.forEach { factor ->
                BlockingFactorItem(factor = factor)
            }
        }
    }
}

@Composable
private fun BlockingFactorItem(
    factor: BlockingFactor,
    modifier: Modifier = Modifier
) {
    val (importanceColor, importanceLabel) = when (factor.importance) {
        Importance.CRITICAL -> CatppuccinMocha.Red to "CRITICAL"
        Importance.HIGH -> CatppuccinMocha.Peach to "HIGH"
        Importance.MEDIUM -> CatppuccinMocha.Yellow to "MEDIUM"
        Importance.LOW -> CatppuccinMocha.Blue to "LOW"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(importanceColor.copy(alpha = 0.1f))
            .border(1.dp, importanceColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Importance badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(importanceColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = importanceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CatppuccinMocha.Base,
                    fontSize = 10.sp
                )
            }

            Text(
                text = factor.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = CatppuccinMocha.Text
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = factor.description,
            style = MaterialTheme.typography.bodyMedium,
            color = CatppuccinMocha.Subtext1
        )

        if (factor.technicalDetail != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "💡 ${factor.technicalDetail}",
                style = MaterialTheme.typography.bodySmall,
                color = CatppuccinMocha.Overlay1,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ImportanceLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = CatppuccinMocha.Red, label = "Critical")
        LegendItem(color = CatppuccinMocha.Peach, label = "High")
        LegendItem(color = CatppuccinMocha.Yellow, label = "Medium")
        LegendItem(color = CatppuccinMocha.Blue, label = "Low")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CatppuccinMocha.Subtext0
        )
    }
}
