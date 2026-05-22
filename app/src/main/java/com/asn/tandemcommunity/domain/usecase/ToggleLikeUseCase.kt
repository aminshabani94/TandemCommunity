package com.asn.tandemcommunity.domain.usecase

import com.asn.tandemcommunity.domain.repository.CommunityRepository

class ToggleLikeUseCase(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(memberId: String) = repository.toggleLike(memberId)
}