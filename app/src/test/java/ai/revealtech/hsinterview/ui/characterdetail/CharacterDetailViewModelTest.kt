package ai.revealtech.hsinterview.ui.characterdetail

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Episode
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
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CharacterDetailViewModel
    private lateinit var mockRepository: RickAndMortyRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        viewModel = CharacterDetailViewModel(mockRepository)
    }

    @Test
    fun `loadCharacterDetail updates character state to success`() = runTest {
        // Given
        val characterId = 1
        val expectedCharacter = TestData.testCharacter
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(expectedCharacter)
        coEvery { mockRepository.getEpisode(any()) } returns Result.success(TestData.testEpisode)

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        viewModel.characterState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is UiState.Loading)
            val successState = awaitItem()
            assertTrue(successState is UiState.Success)
            assertEquals(expectedCharacter, (successState as UiState.Success).data)
        }
    }

    @Test
    fun `loadCharacterDetail updates character state to error when repository fails`() = runTest {
        // Given
        val characterId = 1
        val errorMessage = "Character not found"
        coEvery { mockRepository.getCharacter(characterId) } returns Result.failure(IOException(errorMessage))

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        viewModel.characterState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is UiState.Loading)
            val errorState = awaitItem()
            assertTrue(errorState is UiState.Error)
            assertEquals(errorMessage, (errorState as UiState.Error).message)
        }
    }

    @Test
    fun `loadCharacterDetail loads episodes after successful character load`() = runTest {
        // Given
        val characterId = 1
        val expectedCharacter = TestData.testCharacter
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(expectedCharacter)
        coEvery { mockRepository.getEpisode(1) } returns Result.success(TestData.testEpisode)
        coEvery { mockRepository.getEpisode(2) } returns Result.success(TestData.testEpisode2)

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        viewModel.episodesState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is UiState.Loading)
            val successState = awaitItem()
            assertTrue(successState is UiState.Success)
            assertEquals(2, (successState as UiState.Success).data.size)
        }
    }

    @Test
    fun `loadCharacterDetail handles episodes load failure gracefully`() = runTest {
        // Given
        val characterId = 1
        val expectedCharacter = TestData.testCharacter
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(expectedCharacter)
        coEvery { mockRepository.getEpisode(any()) } returns Result.failure(IOException("Episode not found"))

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        viewModel.episodesState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is UiState.Loading)
            val errorState = awaitItem()
            assertTrue(errorState is UiState.Error)
        }
    }

    @Test
    fun `hasMoreEpisodes returns true when there are more episodes to load`() = runTest {
        // Given
        val characterId = 1
        val characterWithManyEpisodes = TestData.testCharacter.copy(
            episode = (1..15).map { "https://rickandmortyapi.com/api/episode/$it" }
        )
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(characterWithManyEpisodes)
        coEvery { mockRepository.getEpisode(any()) } returns Result.success(TestData.testEpisode)

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        // Wait for episodes to start loading
        viewModel.episodesState.test {
            awaitItem() // Loading
            awaitItem() // First batch loaded
        }
        
        // Should have more episodes to load (15 episodes, loading 10 per batch)
        assertTrue(viewModel.hasMoreEpisodes())
    }

    @Test
    fun `hasMoreEpisodes returns false when all episodes are loaded`() = runTest {
        // Given
        val characterId = 1
        val characterWithFewEpisodes = TestData.testCharacter.copy(
            episode = (1..5).map { "https://rickandmortyapi.com/api/episode/$it" }
        )
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(characterWithFewEpisodes)
        coEvery { mockRepository.getEpisode(any()) } returns Result.success(TestData.testEpisode)

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        // Wait for all episodes to load
        viewModel.episodesState.test {
            awaitItem() // Loading
            awaitItem() // All episodes loaded
        }
        
        // Should not have more episodes to load (5 episodes, all loaded in first batch)
        assertTrue(!viewModel.hasMoreEpisodes())
    }

    @Test
    fun `isLoadingMoreEpisodes reflects loading state correctly`() = runTest {
        // Given
        val characterId = 1
        val characterWithManyEpisodes = TestData.testCharacter.copy(
            episode = (1..15).map { "https://rickandmortyapi.com/api/episode/$it" }
        )
        coEvery { mockRepository.getCharacter(characterId) } returns Result.success(characterWithManyEpisodes)
        coEvery { mockRepository.getEpisode(any()) } returns Result.success(TestData.testEpisode)

        // When
        viewModel.loadCharacterDetail(characterId)

        // Then
        viewModel.isLoadingMoreEpisodes.test {
            val initialLoading = awaitItem()
            assertTrue(!initialLoading) // Initial state is false
            val loadingState = awaitItem()
            assertTrue(loadingState) // Should become true when loading episodes
            val finalLoading = awaitItem()
            assertTrue(!finalLoading) // Should become false when done loading
        }
    }
}
