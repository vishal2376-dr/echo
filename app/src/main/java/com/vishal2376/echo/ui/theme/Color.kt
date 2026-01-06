package com.vishal2376.echo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Catppuccin Mocha Theme with F1 Red Accent
 * https://github.com/catppuccin/catppuccin
 */
object CatppuccinMocha {
    // Base Colors
    val Base = Color(0xFF1e1e2e)
    val Mantle = Color(0xFF181825)
    val Crust = Color(0xFF11111b)
    
    // Surface Colors
    val Surface0 = Color(0xFF313244)
    val Surface1 = Color(0xFF45475a)
    val Surface2 = Color(0xFF585b70)
    
    // Overlay Colors
    val Overlay0 = Color(0xFF6c7086)
    val Overlay1 = Color(0xFF7f849c)
    val Overlay2 = Color(0xFF9399b2)
    
    // Text Colors
    val Subtext0 = Color(0xFFa6adc8)
    val Subtext1 = Color(0xFFbac2de)
    val Text = Color(0xFFcdd6f4)
    
    // F1 Red as Primary Accent
    val Red = Color(0xFFE10600)      // F1 Racing Red
    val Maroon = Color(0xFFeba0ac)
    
    // Other Accent Colors
    val Peach = Color(0xFFfab387)
    val Yellow = Color(0xFFf9e2af)
    val Green = Color(0xFFa6e3a1)
    val Teal = Color(0xFF94e2d5)
    val Sky = Color(0xFF89dceb)
    val Sapphire = Color(0xFF74c7ec)
    val Blue = Color(0xFF89b4fa)
    val Lavender = Color(0xFFb4befe)
    val Mauve = Color(0xFFcba6f7)
    val Pink = Color(0xFFf5c2e7)
    val Flamingo = Color(0xFFf2cdcd)
    val Rosewater = Color(0xFFf5e0dc)
}

/**
 * F1 Red Accent Colors
 */
object F1Accent {
    val Primary = Color(0xFFE10600)      // Official F1 Red
    val Light = Color(0xFFFF1801)        // Bright red
    val Dark = Color(0xFF8B0000)         // Deep red
}

/**
 * Status-specific colors
 */
object StatusColors {
    val Success = CatppuccinMocha.Green
    val Warning = CatppuccinMocha.Yellow
    val Error = F1Accent.Primary
    val Info = CatppuccinMocha.Blue
    val Blocked = F1Accent.Primary
    val Pending = CatppuccinMocha.Peach
}
