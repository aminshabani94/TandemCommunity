package com.asn.tandemcommunity.domain.repository

import com.asn.tandemcommunity.domain.model.CommunityMember
import com.asn.tandemcommunity.domain.util.DomainResult
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    suspend fun getCommunity(page: Int): DomainResult<List<CommunityMember>>
    fun observeLikedIds(): Flow<Set<String>>
    suspend fun toggleLike(memberId: String)
}
