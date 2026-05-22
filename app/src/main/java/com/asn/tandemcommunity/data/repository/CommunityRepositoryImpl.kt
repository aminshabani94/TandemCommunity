package com.asn.tandemcommunity.data.repository

import com.asn.tandemcommunity.data.local.LikeLocalDataSource
import com.asn.tandemcommunity.data.remote.CommunityApi
import com.asn.tandemcommunity.data.remote.dto.toDomainModel
import com.asn.tandemcommunity.domain.model.CommunityMember
import com.asn.tandemcommunity.domain.repository.CommunityRepository
import com.asn.tandemcommunity.domain.util.DomainResult
import kotlinx.coroutines.flow.Flow

class CommunityRepositoryImpl(
    private val api: CommunityApi,
    private val likeLocalDataSource: LikeLocalDataSource,
) : CommunityRepository {

    override suspend fun getCommunity(page: Int): DomainResult<List<CommunityMember>> =
        when (val result = api.getCommunity(page)) {
            is DomainResult.Success -> DomainResult.Success(
                result.data.map { it.toDomainModel() }
            )

            is DomainResult.Failure -> DomainResult.Failure(result.error)
        }

    override fun observeLikedIds(): Flow<Set<String>> =
        likeLocalDataSource.observeLikedIds()

    override suspend fun toggleLike(memberId: String) =
        likeLocalDataSource.toggle(memberId)
}
