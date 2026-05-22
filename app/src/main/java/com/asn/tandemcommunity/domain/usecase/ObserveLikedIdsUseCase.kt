package com.asn.tandemcommunity.domain.usecase

import com.asn.tandemcommunity.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class ObserveLikedIdsUseCase(
    private val repository: CommunityRepository
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeLikedIds()
}