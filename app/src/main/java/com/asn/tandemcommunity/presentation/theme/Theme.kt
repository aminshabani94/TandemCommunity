package com.asn.tandemcommunity.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TandemColorScheme = lightColorScheme(
    primary = TandemLikeActive,
    onPrimary = TandemTextPrimary,
    background = TandemListBackground,
    onBackground = TandemTextPrimary,
    surface = TandemListBackground,
    onSurface = TandemTextPrimary,
    surfaceVariant = TandemHeaderBackground,
    onSurfaceVariant = TandemTextSecondary,
    outline = TandemDivider
)

@Composable
fun TandemCommunityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TandemColorScheme,
        typography = Typography,
        content = content
    )
}
