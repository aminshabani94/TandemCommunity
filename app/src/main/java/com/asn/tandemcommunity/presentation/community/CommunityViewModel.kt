package com.asn.tandemcommunity.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asn.tandemcommunity.domain.usecase.LoadCommunityResult
import com.asn.tandemcommunity.domain.usecase.LoadCommunityUseCase
import com.asn.tandemcommunity.domain.usecase.ObserveLikedIdsUseCase
import com.asn.tandemcommunity.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val loadCommunityUseCase: LoadCommunityUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val observeLikedIdsUseCase: ObserveLikedIdsUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(CommunityState())
    val viewState: StateFlow<CommunityState> = _viewState.asStateFlow()

    private var isLoading = false

    init {
        startObservingLikes()
        sendEvent(CommunityEvent.LoadNextPage)
    }

    fun sendEvent(event: CommunityEvent) {
        when (event) {
            CommunityEvent.LoadNextPage -> loadNextPage(isInitial = getState().members.isEmpty())
            CommunityEvent.Retry -> retry()
            CommunityEvent.RetryLoadMore -> retryLoadMore()
            CommunityEvent.DismissLoadMoreError -> dismissLoadMoreError()
            is CommunityEvent.ToggleLike -> onToggleLike(event.memberId)
        }
    }

    private fun getState(): CommunityState = _viewState.value

    private fun updateState(reducer: (CommunityState) -> CommunityState) {
        _viewState.update(reducer)
    }

    private fun startObservingLikes() {
        viewModelScope.launch {
            observeLikedIdsUseCase().collect { likedIds ->
                updateState { it.copy(likedIds = likedIds) }
            }
        }
    }

    private fun loadNextPage(isInitial: Boolean) {
        if (isLoading) return
        val state = getState()
        if (state.hasReachedEnd) return
        if (!isInitial && state.loadMoreError != null) return

        isLoading = true
        if (!isInitial) {
            updateState { it.copy(isLoadingMore = true, loadMoreError = null) }
        }

        viewModelScope.launch {
            when (val result = loadCommunityUseCase()) {
                is LoadCommunityResult.Success -> {
                    updateState {
                        it.copy(
                            isInitialLoading = false,
                            members = result.members,
                            isLoadingMore = false,
                            hasReachedEnd = result.hasReachedEnd,
                            loadMoreError = null,
                            fullScreenError = null,
                        )
                    }
                }

                is LoadCommunityResult.Failure -> {
                    updateState {
                        if (result.isInitialLoad) {
                            it.copy(
                                isInitialLoading = false,
                                fullScreenError = result.error,
                            )
                        } else {
                            it.copy(
                                members = result.members,
                                isLoadingMore = false,
                                hasReachedEnd = loadCommunityUseCase.hasReachedEnd,
                                loadMoreError = result.error,
                            )
                        }
                    }
                }

                LoadCommunityResult.AlreadyAtEnd -> Unit
            }
            isLoading = false
        }
    }

    private fun retryLoadMore() {
        updateState { it.copy(loadMoreError = null) }
        loadNextPage(isInitial = false)
    }

    private fun dismissLoadMoreError() {
        updateState { it.copy(loadMoreError = null) }
    }

    private fun onToggleLike(memberId: String) {
        viewModelScope.launch { toggleLikeUseCase(memberId) }
    }

    private fun retry() {
        loadCommunityUseCase.reset()
        isLoading = false
        updateState {
            CommunityState(likedIds = it.likedIds)
        }
        loadNextPage(isInitial = true)
    }
}
