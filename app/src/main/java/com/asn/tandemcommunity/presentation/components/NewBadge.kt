package com.asn.tandemcommunity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.presentation.theme.CommunityScreenPreviewTheme
import com.asn.tandemcommunity.presentation.theme.TandemNewBadge

@Composable
fun NewBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.label_new_badge),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(TandemNewBadge, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp)
    )
}

@Preview(showBackground = true, name = "New badge")
@Composable
private fun NewBadgePreview() {
    CommunityScreenPreviewTheme {
        NewBadge(Modifier.padding(16.dp))
    }
}
