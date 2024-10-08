import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.dps.gittask.ui.theme.Repository.GitHubRepository
import org.dps.gittask.ui.theme.ViewModels.UserUiState
import org.dps.gittask.ui.theme.ViewModels.UserViewModel
import org.dps.gittask.ui.theme.model.User
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Rule

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mock GitHubRepository
    private val repository = mockk<GitHubRepository>()

    // ViewModel under test
    private lateinit var viewModel: UserViewModel

    // Coroutine Test Dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the test dispatcher as the main dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validate user data is fetched successfully`() = runTest {
        // Arrange: Prepare fake user data
        val fakeUser = User("TestUser", "https://avatar.url")

        // Mock repository to return the fake user
        coEvery { repository.getUser("testuser") } returns fakeUser

        // Act: Call the ViewModel's fetchUser function
        viewModel.fetchUser("testuser")

        // Let coroutines run until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Verify that uiState is updated with success
        assertEquals(UserUiState.Success(fakeUser), viewModel.uiState.value)
    }

    @Test
    fun `Validate network error is handled when exception occurs`() = runTest {
        // Arrange: Simulate an exception when fetching user data
        coEvery { repository.getUser("testuser") } throws Exception("Network Error")

        // Act: Call the ViewModel's fetchUser function
        viewModel.fetchUser("testuser")

        // Let coroutines run until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Verify that uiState is updated with error
        val errorState = viewModel.uiState.value
        assert(errorState is UserUiState.Error)
        assertEquals("Network Error", (errorState as UserUiState.Error).message)
    }
}
