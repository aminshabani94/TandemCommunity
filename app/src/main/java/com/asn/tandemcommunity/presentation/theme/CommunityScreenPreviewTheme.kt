package com.asn.tandemcommunity.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommunityScreenPreviewTheme(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TandemCommunityTheme {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
