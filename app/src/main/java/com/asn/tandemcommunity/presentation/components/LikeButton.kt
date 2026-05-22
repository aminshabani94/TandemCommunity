package com.asn.tandemcommunity.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.presentation.theme.CommunityScreenPreviewTheme
import com.asn.tandemcommunity.presentation.theme.Dimens
import com.asn.tandemcommunity.presentation.theme.TandemTextPrimary

@Composable
fun LikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactive: Boolean = true
) {
    val clickModifier = if (interactive) Modifier.clickable(onClick = onClick) else Modifier
    Icon(
        painter = painterResource(
            if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outlined
        ),
        contentDescription = stringResource(
            if (isLiked) R.string.content_description_liked else R.string.content_description_like
        ),
        tint = if (isLiked) Color.Unspecified else TandemTextPrimary,
        modifier = modifier
            .size(Dimens.likeIconSize)
            .then(clickModifier)
    )
}

@Preview(showBackground = true)
@Composable
private fun LikeButtonPreview(
    @PreviewParameter(LikeButtonPreviewProvider::class) isLiked: Boolean
) {
    CommunityScreenPreviewTheme {
        LikeButton(isLiked = isLiked, onClick = {}, modifier = Modifier.padding(16.dp))
    }
}

class LikeButtonPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)

    override fun getDisplayName(index: Int): String = when (index) {
        0 -> "Like – default"
        else -> "Like – liked"
    }
}
