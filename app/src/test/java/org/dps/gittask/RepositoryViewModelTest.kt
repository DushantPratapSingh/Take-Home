package org.dps.gittask

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.dps.gittask.ui.theme.Repository.GitHubRepository
import org.dps.gittask.ui.theme.ViewModels.ReposUiState
import org.dps.gittask.ui.theme.ViewModels.RepositoryViewModel
import org.dps.gittask.ui.theme.model.Repository
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Rule

@ExperimentalCoroutinesApi
class RepositoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mock GitHubRepository
    private val repository = mockk<GitHubRepository>()

    // ViewModel under test
    private lateinit var viewModel: RepositoryViewModel

    // Coroutine Test Dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the test dispatcher as the main dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = RepositoryViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset the dispatcher after each test
        Dispatchers.resetMain()
    }

    @Test
    fun `Validate when repositories are fetched successfully`() = runTest {
        // Arrange: Prepare fake repository data
        val fakeRepos = listOf(
            Repository("Repo1", "Description1", stargazers_count = 100, forks = 10),
            Repository("Repo2", "Description2", stargazers_count = 200, forks = 20)
        )

        // Mock repository to return the fake repos
        coEvery { repository.getUserRepos("testuser") } returns fakeRepos

        // Act: Call the ViewModel's fetchUserRepos function
        viewModel.fetchUserRepos("testuser")

        // Let coroutines run until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Verify that uiState is updated with success
        assertEquals(ReposUiState.Success(fakeRepos), viewModel.uiState.value)
    }

    @Test
    fun `Validate network is handled when exception occurs`() = runTest {
        // Arrange: Simulate an exception when fetching repositories
        coEvery { repository.getUserRepos("testuser") } throws Exception("Network Error")

        // Act: Call the ViewModel's fetchUserRepos function
        viewModel.fetchUserRepos("testuser")

        // Let coroutines run until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Verify that uiState is updated with error
        val errorState = viewModel.uiState.value
        assert(errorState is ReposUiState.Error)
        assertEquals("Network Error", (errorState as ReposUiState.Error).message)
    }

}
