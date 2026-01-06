package com.vishal2376.echo.domain_screen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.echo.ui.theme.CatppuccinMocha
import com.vishal2376.echo.ui.theme.F1Accent

@Composable
fun F1TopBar(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "topbar")

    // Racing stripe animation
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stripe"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CatppuccinMocha.Mantle)
    ) {
        // Racing stripe accent at top
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(3.dp)
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            Color.Transparent,
//                            F1Accent.Primary,
//                            F1Accent.Light,
//                            F1Accent.Primary,
//                            Color.Transparent
//                        ),
//                        start = Offset(animatedOffset, 0f),
//                        end = Offset(animatedOffset + 200f, 0f)
//                    )
//                )
//        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // F1-style Logo Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    F1Accent.Primary,
                                    F1Accent.Dark
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // ECHO Title
                    Text(
                        text = "ECHO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = CatppuccinMocha.Text,
                        letterSpacing = 4.sp
                    )

                    // Subtitle with racing accent
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(2.dp)
                                .background(F1Accent.Primary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NETWORK DIAGNOSTICS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = CatppuccinMocha.Overlay1,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gradient divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                F1Accent.Primary,
                                F1Accent.Primary.copy(alpha = 0.5f),
                                CatppuccinMocha.Surface0,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
