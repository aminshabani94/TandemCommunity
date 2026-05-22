package com.asn.tandemcommunity.domain.usecase

import com.asn.tandemcommunity.domain.model.CommunityMember
import com.asn.tandemcommunity.domain.repository.CommunityRepository
import com.asn.tandemcommunity.domain.util.DomainResult

class LoadCommunityUseCase(
    private val repository: CommunityRepository,
) {
    private var currentPage = 1
    private var accumulatedMembers: List<CommunityMember> = emptyList()

    val hasReachedEnd: Boolean
        get() = reachedEnd
    private var reachedEnd = false

    fun reset() {
        currentPage = 1
        accumulatedMembers = emptyList()
        reachedEnd = false
    }

    suspend operator fun invoke(): LoadCommunityResult {
        if (reachedEnd) return LoadCommunityResult.AlreadyAtEnd

        return when (val result = repository.getCommunity(currentPage)) {
            is DomainResult.Success -> {
                val newMembers = result.data
                val pageReachedEnd = newMembers.size < PAGE_SIZE
                accumulatedMembers = accumulatedMembers + newMembers
                reachedEnd = pageReachedEnd
                if (!pageReachedEnd) currentPage++
                LoadCommunityResult.Success(
                    members = accumulatedMembers,
                    hasReachedEnd = reachedEnd,
                )
            }

            is DomainResult.Failure -> LoadCommunityResult.Failure(
                error = result.error,
                members = accumulatedMembers,
                isInitialLoad = accumulatedMembers.isEmpty(),
            )
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
