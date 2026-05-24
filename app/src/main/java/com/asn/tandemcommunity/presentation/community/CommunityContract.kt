package com.asn.tandemcommunity.presentation.community

import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.model.CommunityMember

data class CommunityState(
    val isInitialLoading: Boolean = true,
    val members: List<CommunityMember> = emptyList(),
    val likedIds: Set<String> = emptySet(),
    val isLoadingMore: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val loadMoreError: CommunityError? = null,
    val fullScreenError: CommunityError? = null,
) {
    fun isLoading(): Boolean = isInitialLoading && members.isEmpty()

    fun showFullScreenError(): Boolean = fullScreenError != null && members.isEmpty()

    fun isLiked(memberId: String): Boolean = memberId in likedIds

    fun canLoadMore(): Boolean =
        members.isNotEmpty() && !hasReachedEnd && !isLoadingMore && loadMoreError == null
}

sealed interface CommunityEvent {
    data object LoadNextPage : CommunityEvent
    data class ToggleLike(val memberId: String) : CommunityEvent
    data object Retry : CommunityEvent
    data object RetryLoadMore : CommunityEvent
    data object DismissLoadMoreError : CommunityEvent
}
