package com.asn.tandemcommunity.presentation.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.model.CommunityErrorCode

@Composable
fun CommunityError.resolveMessage(isLoadMore: Boolean = false): String = when (code) {
    CommunityErrorCode.NETWORK -> stringResource(R.string.error_network)
    CommunityErrorCode.HTTP_NOT_FOUND -> stringResource(R.string.error_not_found)
    CommunityErrorCode.HTTP_CLIENT,
    CommunityErrorCode.HTTP_SERVER -> stringResource(R.string.error_http)

    CommunityErrorCode.API_RESPONSE -> stringResource(R.string.error_api_response)
    CommunityErrorCode.PARSE -> stringResource(R.string.error_parse)
    CommunityErrorCode.UNKNOWN -> if (isLoadMore) {
        stringResource(R.string.error_load_more)
    } else {
        stringResource(R.string.error_generic)
    }
}
