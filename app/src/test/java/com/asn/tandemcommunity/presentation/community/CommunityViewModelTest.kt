package com.asn.tandemcommunity.presentation.community

import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.usecase.LoadCommunityResult
import com.asn.tandemcommunity.domain.usecase.LoadCommunityUseCase
import com.asn.tandemcommunity.domain.usecase.ObserveLikedIdsUseCase
import com.asn.tandemcommunity.domain.usecase.ToggleLikeUseCase
import com.asn.tandemcommunity.test.sampleMember
import com.asn.tandemcommunity.test.sampleMembers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loadCommunityUseCase: LoadCommunityUseCase = mockk()
    private val toggleLikeUseCase: ToggleLikeUseCase = mockk(relaxed = true)
    private val observeLikedIdsUseCase: ObserveLikedIdsUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { observeLikedIdsUseCase() } returns flowOf(emptySet())
        every { loadCommunityUseCase.hasReachedEnd } returns false
        every { loadCommunityUseCase.reset() } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CommunityViewModel =
        CommunityViewModel(loadCommunityUseCase, toggleLikeUseCase, observeLikedIdsUseCase)

    @Test
    fun `initial load emits content`() = runTest(testDispatcher) {
        coEvery { loadCommunityUseCase() } returns LoadCommunityResult.Success(
            members = listOf(sampleMember()),
            hasReachedEnd = true,
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(1, state.members.size)
        assertTrue(state.hasReachedEnd)
        assertFalse(state.isLoading())
    }

    @Test
    fun `initial load failure emits full screen error`() = runTest(testDispatcher) {
        coEvery { loadCommunityUseCase() } returns LoadCommunityResult.Failure(
            error = CommunityError.Network,
            members = emptyList(),
            isInitialLoad = true,
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertTrue(state.showFullScreenError())
        assertEquals(CommunityError.Network, state.fullScreenError)
    }

    @Test
    fun `load more failure sets loadMoreError`() = runTest(testDispatcher) {
        val firstPage = sampleMembers(20)
        coEvery { loadCommunityUseCase() } returnsMany listOf(
            LoadCommunityResult.Success(firstPage, hasReachedEnd = false),
            LoadCommunityResult.Failure(
                error = CommunityError.Network,
                members = firstPage,
                isInitialLoad = false,
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.sendEvent(CommunityEvent.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(20, state.members.size)
        assertEquals(CommunityError.Network, state.loadMoreError)
    }

    @Test
    fun `retry load more clears error and loads again`() = runTest(testDispatcher) {
        val firstPage = sampleMembers(20)
        val allMembers = firstPage + sampleMember("21")
        coEvery { loadCommunityUseCase() } returnsMany listOf(
            LoadCommunityResult.Success(firstPage, hasReachedEnd = false),
            LoadCommunityResult.Failure(
                error = CommunityError.Network,
                members = firstPage,
                isInitialLoad = false,
            ),
            LoadCommunityResult.Success(allMembers, hasReachedEnd = true),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.sendEvent(CommunityEvent.LoadNextPage)
        advanceUntilIdle()
        viewModel.sendEvent(CommunityEvent.RetryLoadMore)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertNull(state.loadMoreError)
        assertEquals(21, state.members.size)
        coVerify(exactly = 3) { loadCommunityUseCase() }
    }

    @Test
    fun `restores persisted likes when list loads after datastore emits`() =
        runTest(testDispatcher) {
            coEvery { observeLikedIdsUseCase() } returns flowOf(setOf("7"))
            coEvery { loadCommunityUseCase() } returns LoadCommunityResult.Success(
                members = listOf(sampleMember("7"), sampleMember("8")),
                hasReachedEnd = true,
            )

        val viewModel = createViewModel()
        advanceUntilIdle()

            val state = viewModel.viewState.value
            assertTrue(state.isLiked("7"))
            assertFalse(state.isLiked("8"))
    }

    @Test
    fun `liked ids update without mutating members`() = runTest(testDispatcher) {
        coEvery { loadCommunityUseCase() } returns LoadCommunityResult.Success(
            members = listOf(sampleMember("7")),
            hasReachedEnd = true,
        )
        coEvery { observeLikedIdsUseCase() } returns flowOf(setOf("7"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertTrue(state.isLiked("7"))
        assertFalse(state.members.single().isLiked)
    }

    @Test
    fun `toggle like delegates to use case`() = runTest(testDispatcher) {
        coEvery { loadCommunityUseCase() } returns LoadCommunityResult.Success(
            members = listOf(sampleMember("3")),
            hasReachedEnd = true,
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendEvent(CommunityEvent.ToggleLike("3"))
        advanceUntilIdle()

        coVerify { toggleLikeUseCase("3") }
    }

    @Test
    fun `retry preserves liked ids when datastore does not re-emit`() = runTest(testDispatcher) {
        coEvery { observeLikedIdsUseCase() } returns flowOf(setOf("7"))
        coEvery { loadCommunityUseCase() } returnsMany listOf(
            LoadCommunityResult.Failure(
                error = CommunityError.Network,
                members = emptyList(),
                isInitialLoad = true,
            ),
            LoadCommunityResult.Success(
                members = listOf(sampleMember("7"), sampleMember("8")),
                hasReachedEnd = true,
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.sendEvent(CommunityEvent.Retry)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertTrue(state.isLiked("7"))
        assertFalse(state.isLiked("8"))
    }

    @Test
    fun `retry resets use case and reloads`() = runTest(testDispatcher) {
        coEvery { loadCommunityUseCase() } returnsMany listOf(
            LoadCommunityResult.Failure(
                error = CommunityError.Network,
                members = emptyList(),
                isInitialLoad = true,
            ),
            LoadCommunityResult.Success(sampleMembers(20), hasReachedEnd = false),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.sendEvent(CommunityEvent.Retry)
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading())
        assertEquals(20, viewModel.viewState.value.members.size)
        verify { loadCommunityUseCase.reset() }
        coVerify(atLeast = 2) { loadCommunityUseCase() }
    }
}
