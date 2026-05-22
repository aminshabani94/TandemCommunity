package com.asn.tandemcommunity.domain.usecase

import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.model.CommunityMember

sealed interface LoadCommunityResult {
    data class Success(
        val members: List<CommunityMember>,
        val hasReachedEnd: Boolean,
    ) : LoadCommunityResult

    data class Failure(
        val error: CommunityError,
        val members: List<CommunityMember>,
        val isInitialLoad: Boolean,
    ) : LoadCommunityResult

    data object AlreadyAtEnd : LoadCommunityResult
}
