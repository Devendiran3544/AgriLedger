package com.agriledger.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core Palette
val PrimaryForest = Color(0xFF174D38)
val PrimaryForestVariant = Color(0xFF236B4C)
val PrimaryForestDark = Color(0xFF0F3627)
val AccentGreen = Color(0xFF58B368)
val AccentGreenLight = Color(0xFFE2F4E6)
val AccentGreenDark = Color(0xFF388E4B)

// Light Theme Neutrals
val BackgroundLight = Color(0xFFF6F8F5)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEBF2ED)
val TextDark = Color(0xFF17231C)
val TextSecondaryLight = Color(0xFF748078)
val BorderLight = Color(0xFFE0E8E2)

// Dark Theme Neutrals
val BackgroundDark = Color(0xFF101914)
val SurfaceDark = Color(0xFF17241E)
val SurfaceVariantDark = Color(0xFF21332A)
val TextLight = Color(0xFFF0F5F2)
val TextSecondaryDark = Color(0xFF9EAEA4)
val BorderDark = Color(0xFF2B3F34)

// Semantic Accents
val WarningGold = Color(0xFFF0B44D)
val WarningGoldBg = Color(0xFFFFF7E6)
val WarningGoldDarkBg = Color(0xFF332712)

val ErrorCrimson = Color(0xFFD9534F)
val ErrorCrimsonBg = Color(0xFFFFECEB)
val ErrorCrimsonDarkBg = Color(0xFF351A19)

val SuccessGreen = Color(0xFF3B9C67)
val SuccessGreenBg = Color(0xFFE8F6EE)
val SuccessGreenDarkBg = Color(0xFF143021)

val InfoBlue = Color(0xFF2E7BB4)
val InfoBlueBg = Color(0xFFE8F3FA)

// Fintech Gradients
val PrimaryCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF174D38), Color(0xFF246D4F))
)

val AccentCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF236B4C), Color(0xFF3A8A65))
)

val CashCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0E3827), Color(0xFF1D5A40))
)

val GoldCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFC98616), Color(0xFFE5A83B))
)
