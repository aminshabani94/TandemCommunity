package com.asn.tandemcommunity.presentation.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.asn.tandemcommunity.presentation.theme.TandemCommunityTheme

@Composable
fun ProvideTandemCommunityPreview(
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
