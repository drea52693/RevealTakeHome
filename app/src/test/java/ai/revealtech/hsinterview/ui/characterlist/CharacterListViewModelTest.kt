package ai.revealtech.hsinterview.ui.characterlist

import ai.revealtech.hsinterview.data.model.CharactersResponse
import ai.revealtech.hsinterview.data.repository.RickAndMortyRepository
import ai.revealtech.hsinterview.testutils.MainDispatcherRule
import ai.revealtech.hsinterview.testutils.TestData
import ai.revealtech.hsinterview.ui.state.UiState
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CharacterListViewModel
    private lateinit var mockRepository: RickAndMortyRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        viewModel = CharacterListViewModel(mockRepository)
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        coEvery { mockRepository.getCharacters(any()) } returns Result.success(TestData.testCharactersResponse)

        // When
        viewModel.uiState.test {
            // Then
            val initialState = awaitItem()
            assertTrue(initialState is UiState.Loading)
        }
    }

    @Test
    fun `loadCharacters updates state to success with characters`() = runTest {
        // Given
        val expectedResponse = TestData.testCharactersResponse
        coEvery { mockRepository.getCharacters(1) } returns Result.success(expectedResponse)

        // When
        viewModel.loadCharacters()

        // Then
        viewModel.uiState.test {
            // Skip loading state
            awaitItem()
            val successState = awaitItem()
            assertTrue(successState is UiState.Success)
            assertEquals(expectedResponse.results, (successState as UiState.Success).data)
        }
    }

    @Test
    fun `loadCharacters updates state to error when repository fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getCharacters(1) } returns Result.failure(IOException(errorMessage))

        // When
        viewModel.loadCharacters()

        // Then
        viewModel.uiState.test {
            // Skip loading state
            awaitItem()
            val errorState = awaitItem()
            assertTrue(errorState is UiState.Error)
            assertEquals(errorMessage, (errorState as UiState.Error).message)
        }
    }

    @Test
    fun `loadMoreCharacters appends new characters to existing list`() = runTest {
        // Given
        val firstPageResponse = TestData.testCharactersResponse
        val secondPageResponse = TestData.testCharactersResponsePage2
        
        coEvery { mockRepository.getCharacters(1) } returns Result.success(firstPageResponse)
        coEvery { mockRepository.getCharacters(2) } returns Result.success(secondPageResponse)

        // When
        viewModel.loadCharacters()
        viewModel.loadMoreCharacters()

        // Then
        viewModel.uiState.test {
            // Skip initial loading
            awaitItem()
            // Skip first success state
            awaitItem()
            // Check final state with both pages
            val finalState = awaitItem()
            assertTrue(finalState is UiState.Success)
            assertEquals(4, (finalState as UiState.Success).data.size) // 2 characters from each page
        }
    }

    @Test
    fun `loadMoreCharacters sets loading more state correctly`() = runTest {
        // Given
        val firstPageResponse = TestData.testCharactersResponse
        val secondPageResponse = TestData.testCharactersResponsePage2
        
        coEvery { mockRepository.getCharacters(1) } returns Result.success(firstPageResponse)
        coEvery { mockRepository.getCharacters(2) } returns Result.success(secondPageResponse)

        // When
        viewModel.loadCharacters()
        viewModel.loadMoreCharacters()

        // Then
        viewModel.isLoadingMore.test {
            val initialLoading = awaitItem()
            assertTrue(!initialLoading) // Initial state is false
            val loadingState = awaitItem()
            assertTrue(loadingState) // Should become true when loading more characters
            val finalLoading = awaitItem()
            assertTrue(!finalLoading) // Should become false when done loading
        }
    }

    @Test
    fun `loadMoreCharacters does not load when no next page`() = runTest {
        // Given
        val lastPageResponse = TestData.testCharactersResponseLastPage
        coEvery { mockRepository.getCharacters(1) } returns Result.success(lastPageResponse)

        // When
        viewModel.loadCharacters()
        viewModel.loadMoreCharacters()

        // Then
        viewModel.uiState.test {
            // Skip loading state
            awaitItem()
            // Should only have one success state (no additional loading)
            val successState = awaitItem()
            assertTrue(successState is UiState.Success)
            assertEquals(1, (successState as UiState.Success).data.size)
        }
    }

    @Test
    fun `refresh resets to first page and reloads characters`() = runTest {
        // Given
        val response = TestData.testCharactersResponse
        coEvery { mockRepository.getCharacters(1) } returns Result.success(response)

        // When
        viewModel.refresh()

        // Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is UiState.Loading)
            val successState = awaitItem()
            assertTrue(successState is UiState.Success)
            assertEquals(response.results, (successState as UiState.Success).data)
        }
    }
}
