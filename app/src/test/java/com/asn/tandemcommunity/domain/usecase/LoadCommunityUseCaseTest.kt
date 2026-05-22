package com.asn.tandemcommunity.domain.usecase

import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.repository.CommunityRepository
import com.asn.tandemcommunity.domain.util.DomainResult
import com.asn.tandemcommunity.test.sampleMember
import com.asn.tandemcommunity.test.sampleMembers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoadCommunityUseCaseTest {

    private val repository: CommunityRepository = mockk()
    private lateinit var useCase: LoadCommunityUseCase

    @Before
    fun setUp() {
        useCase = LoadCommunityUseCase(repository)
    }

    @Test
    fun `first page success accumulates members and advances page`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Success(sampleMembers(20))

        val result = useCase()

        assertTrue(result is LoadCommunityResult.Success)
        val success = result as LoadCommunityResult.Success
        assertEquals(20, success.members.size)
        assertFalse(success.hasReachedEnd)
        assertFalse(useCase.hasReachedEnd)
    }

    @Test
    fun `marks end when page has fewer than page size`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Success(sampleMembers(19))

        val result = useCase() as LoadCommunityResult.Success

        assertEquals(19, result.members.size)
        assertTrue(result.hasReachedEnd)
        assertTrue(useCase.hasReachedEnd)
    }

    @Test
    fun `second page appends to accumulated members`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Success(sampleMembers(20))
        coEvery { repository.getCommunity(2) } returns DomainResult.Success(listOf(sampleMember("21")))

        useCase()
        val result = useCase() as LoadCommunityResult.Success

        assertEquals(21, result.members.size)
        assertTrue(result.hasReachedEnd)
    }

    @Test
    fun `returns AlreadyAtEnd when no more pages`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Success(sampleMembers(19))

        useCase()
        val result = useCase()

        assertEquals(LoadCommunityResult.AlreadyAtEnd, result)
    }

    @Test
    fun `failure on first load is initial load`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Failure(CommunityError.Network)

        val result = useCase() as LoadCommunityResult.Failure

        assertTrue(result.isInitialLoad)
        assertTrue(result.members.isEmpty())
        assertEquals(CommunityError.Network, result.error)
    }

    @Test
    fun `failure on second page keeps accumulated members`() = runTest {
        coEvery { repository.getCommunity(1) } returns DomainResult.Success(sampleMembers(20))
        coEvery { repository.getCommunity(2) } returns DomainResult.Failure(CommunityError.Network)

        useCase()
        val result = useCase() as LoadCommunityResult.Failure

        assertFalse(result.isInitialLoad)
        assertEquals(20, result.members.size)
        assertEquals(CommunityError.Network, result.error)
    }

    @Test
    fun `reset clears state for retry`() = runTest {
        coEvery { repository.getCommunity(1) } returnsMany listOf(
            DomainResult.Failure(CommunityError.Network),
            DomainResult.Success(sampleMembers(20)),
        )

        useCase() as LoadCommunityResult.Failure
        useCase.reset()
        val result = useCase() as LoadCommunityResult.Success

        assertEquals(20, result.members.size)
    }
}
