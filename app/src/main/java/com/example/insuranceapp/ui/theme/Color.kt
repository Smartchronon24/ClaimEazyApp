package com.example.insuranceapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Palette
val IndigoDeep = Color(0xFF3949AB) // Dark Mode Primary
val IndigoLight = Color(0xFF3F51B5) // Light Mode Primary
val CyanAccent = Color(0xFF4DD0E1) // Dark Mode Secondary
val CyanDeep = Color(0xFF00ACC1) // Light Mode Secondary

// Success/Status Colors (Production Palette)
val SuccessGreen = Color(0xFF4CAF50)
val PendingAmber = Color(0xFFFFB300)
val RejectedRed = Color(0xFFE53935)

// Dark Theme Surfaces
val DarkSurface = Color(0xFF111827)
val DarkBackground = Color(0xFF080C14)
val DarkSurfaceVariant = Color(0xFF1F2937)

// Light Theme Surfaces
val LightSurface = Color(0xFFFFFFFF)
val LightBackground = Color(0xFFF8FAFC)
val LightSurfaceVariant = Color(0xFFE2E8F0)

// Text Colors
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF1E293B)
val TextSecondaryLight = Color(0xFF64748B)

// Gradients
val PrimaryGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF3949AB), Color(0xFF1A237E))
)

val PrimaryGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFF3F51B5), Color(0xFF283593))
)

val BackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF080C14), Color(0xFF111827))
)

val BackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
)

val AccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF818CF8), Color(0xFF4DD0E1))
)

val SuccessGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
)

val WarningGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFA726), Color(0xFFFB8C00))
)

val DangerGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEF5350), Color(0xFFE53935))
)
