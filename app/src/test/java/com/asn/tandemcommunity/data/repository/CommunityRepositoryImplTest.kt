package com.asn.tandemcommunity.data.repository

import com.asn.tandemcommunity.data.local.LikeLocalDataSource
import com.asn.tandemcommunity.data.remote.CommunityApi
import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.util.DomainResult
import com.asn.tandemcommunity.test.sampleMemberDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityRepositoryImplTest {

    private val api: CommunityApi = mockk()
    private val likes: LikeLocalDataSource = mockk(relaxed = true)
    private val repository = CommunityRepositoryImpl(api, likes)

    @Test
    fun `getCommunity maps dto to domain with isLiked false`() = runTest {
        coEvery { api.getCommunity(1) } returns DomainResult.Success(listOf(sampleMemberDto(id = 42L)))

        val result = repository.getCommunity(1)

        assertTrue(result is DomainResult.Success)
        val member = (result as DomainResult.Success).data.single()
        assertEquals("42", member.id)
        assertEquals("Tobi", member.name)
        assertFalse(member.isLiked)
        assertTrue(member.isNew)
    }

    @Test
    fun `getCommunity propagates typed api error`() = runTest {
        coEvery { api.getCommunity(2) } returns DomainResult.Failure(
            CommunityError.ApiResponse("NOT_FOUND"),
        )

        val result = repository.getCommunity(2)

        assertTrue(result is DomainResult.Failure)
        assertEquals(
            CommunityError.ApiResponse("NOT_FOUND"),
            (result as DomainResult.Failure).error,
        )
    }
}
