package com.asn.tandemcommunity.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.presentation.community.CommunityScreen
import com.asn.tandemcommunity.presentation.theme.Dimens
import com.asn.tandemcommunity.presentation.theme.TandemCommunityTheme
import com.asn.tandemcommunity.presentation.theme.TandemHeaderBackground
import com.asn.tandemcommunity.presentation.theme.TandemListBackground
import com.asn.tandemcommunity.presentation.theme.TandemTextPrimary
import androidx.compose.ui.graphics.Color as ComposeColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            TandemCommunityTheme {
                CommunityRoot()
            }
        }
    }
}

@Composable
private fun CommunityRoot() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TandemListBackground),
    ) {
        CommunityHeader()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            CommunityScreen()
        }
    }
}

@Composable
private fun CommunityHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TandemHeaderBackground)
            .statusBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.community_title),
            style = MaterialTheme.typography.titleLarge,
            color = TandemTextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.screenPaddingHorizontal,
                    vertical = Dimens.cardPaddingVertical,
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.headerShadowHeight)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ComposeColor(0x1A000000), ComposeColor.Transparent),
                    ),
                ),
        )
    }
}
