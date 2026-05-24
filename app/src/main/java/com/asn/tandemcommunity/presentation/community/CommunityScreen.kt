package com.asn.tandemcommunity.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asn.tandemcommunity.R
import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.presentation.components.MemberCard
import com.asn.tandemcommunity.presentation.preview.ProvideTandemCommunityPreview
import com.asn.tandemcommunity.presentation.preview.previewCommunityMembers
import com.asn.tandemcommunity.presentation.theme.Dimens
import com.asn.tandemcommunity.presentation.theme.TandemDivider
import com.asn.tandemcommunity.presentation.theme.TandemListBackground
import com.asn.tandemcommunity.presentation.theme.TandemTextSecondary
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

private object CommunityListKeys {
    const val LOAD_MORE_ERROR = "load_more_error"
    const val LOAD_MORE_PROGRESS = "load_more_progress"
}

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    CommunityScreenContent(
        state = state,
        sendEvent = viewModel::sendEvent,
    )
}

@Composable
fun CommunityScreenContent(
    state: CommunityState,
    sendEvent: (CommunityEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading() -> CenteredLoading(modifier)
        state.showFullScreenError() -> ErrorView(
            error = state.fullScreenError!!,
            onRetry = { sendEvent(CommunityEvent.Retry) },
            modifier = modifier,
        )

        else -> CommunityList(
            state = state,
            sendEvent = sendEvent,
            modifier = modifier,
        )
    }
}

@Composable
private fun CommunityList(
    state: CommunityState,
    sendEvent: (CommunityEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val memberCount = state.members.size

    LaunchedEffect(
        listState,
        memberCount,
        state.hasReachedEnd,
        state.isLoadingMore,
        state.loadMoreError,
    ) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= memberCount - 3 && state.canLoadMore()
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) sendEvent(CommunityEvent.LoadNextPage)
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(TandemListBackground),
    ) {
        items(state.members.size, key = { state.members[it].id }) { index ->
            val member = state.members[index]
            MemberCard(
                member = member,
                isLiked = state.isLiked(member.id),
                onLikeClick = { sendEvent(CommunityEvent.ToggleLike(member.id)) },
            )
            HorizontalDivider(color = TandemDivider)
        }
        if (state.loadMoreError != null) {
            item(key = CommunityListKeys.LOAD_MORE_ERROR) {
                LoadMoreErrorBanner(
                    error = state.loadMoreError,
                    onRetry = { sendEvent(CommunityEvent.RetryLoadMore) },
                    onDismiss = { sendEvent(CommunityEvent.DismissLoadMoreError) },
                )
            }
        }
        if (state.isLoadingMore) {
            item(key = CommunityListKeys.LOAD_MORE_PROGRESS) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(Dimens.screenPaddingHorizontal),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LoadMoreErrorBanner(
    error: CommunityError,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TandemListBackground)
            .padding(horizontal = Dimens.screenPaddingHorizontal, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = error.resolveMessage(isLoadMore = true),
            color = TandemTextSecondary,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_dismiss)) }
    }
}

@Composable
private fun CenteredLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TandemListBackground),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(
    error: CommunityError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TandemListBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(error.resolveMessage(), color = TandemTextSecondary)
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview(
    @PreviewParameter(CommunityStatePreviewProvider::class) state: CommunityState,
) {
    ProvideTandemCommunityPreview(modifier = Modifier.fillMaxSize()) {
        CommunityScreenContent(state = state, sendEvent = {})
    }
}

class CommunityStatePreviewProvider : PreviewParameterProvider<CommunityState> {
    override val values = sequenceOf(
        CommunityState(isInitialLoading = true),
        CommunityState(
            isInitialLoading = false,
            fullScreenError = CommunityError.Network,
        ),
        CommunityState(
            members = previewCommunityMembers,
            likedIds = setOf("2"),
            isLoadingMore = false,
            hasReachedEnd = false,
            isInitialLoading = false,
        ),
        CommunityState(
            members = previewCommunityMembers,
            likedIds = emptySet(),
            isLoadingMore = true,
            hasReachedEnd = false,
            isInitialLoading = false,
        ),
        CommunityState(
            members = previewCommunityMembers,
            likedIds = emptySet(),
            isLoadingMore = false,
            hasReachedEnd = false,
            loadMoreError = CommunityError.Unknown,
            isInitialLoading = false,
        ),
    )

    override fun getDisplayName(index: Int): String = when (index) {
        0 -> "Loading"
        1 -> "Error"
        2 -> "Content"
        3 -> "Content – loading more"
        else -> "Content – load more error"
    }
}
