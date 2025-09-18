package ai.revealtech.hsinterview.ui.characterdetail

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Episode
import ai.revealtech.hsinterview.testutils.TestData
import ai.revealtech.hsinterview.ui.state.UiState
import androidx.compose.ui.test.*
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun characterDetail_displaysErrorState() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val errorMessage = "Character not found"

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Error(errorMessage))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Loading)
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun characterDetail_displaysCharacterInformation_whenSuccessful() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val character = TestData.testCharacter
        val episodes = listOf(TestData.testEpisode, TestData.testEpisode2)

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Success(character))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Success(episodes))
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Character Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alive - Human").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gender").assertIsDisplayed()
        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
        composeTestRule.onNodeWithText("Origin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Earth (Replacement Dimension)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("Earth (C-137)").assertIsDisplayed()
    }

    @Test
    fun characterDetail_displaysEpisodesSection() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val character = TestData.testCharacter
        val episodes = listOf(TestData.testEpisode, TestData.testEpisode2)

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Success(character))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Success(episodes))
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pilot").assertIsDisplayed()
        composeTestRule.onNodeWithText("S01E01").assertIsDisplayed()
        composeTestRule.onNodeWithText("December 2, 2013").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lawnmower Dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("S01E02").assertIsDisplayed()
        composeTestRule.onNodeWithText("December 9, 2013").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersNavigation() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val character = TestData.testCharacter

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Success(character))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Success(emptyList()))
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verify { mockOnNavigateBack() }
    }

    @Test
    fun retryButton_callsLoadCharacterDetail_whenErrorOccurs() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val errorMessage = "Character not found"

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Error(errorMessage))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Loading)
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Retry").performClick()
        verify { mockViewModel.loadCharacterDetail(characterId) }
    }

    @Test
    fun episodesLoadingState_displaysLoadingIndicator() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val character = TestData.testCharacter

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Success(character))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Loading)
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(true)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
    }

    @Test
    fun episodesErrorState_displaysErrorMessage() {
        // Given
        val mockViewModel = mockk<CharacterDetailViewModel>(relaxed = true)
        val mockOnNavigateBack: () -> Unit = mockk(relaxed = true)
        val characterId = 1
        val character = TestData.testCharacter
        val errorMessage = "Failed to load episodes"

        every { mockViewModel.characterState } returns MutableStateFlow(UiState.Success(character))
        every { mockViewModel.episodesState } returns MutableStateFlow(UiState.Error(errorMessage))
        every { mockViewModel.isLoadingMoreEpisodes } returns MutableStateFlow(false)
        every { mockViewModel.loadCharacterDetail(characterId) } just Runs

        // When
        composeTestRule.setContent {
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = mockOnNavigateBack,
                viewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
    }
}
