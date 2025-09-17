package ai.revealtech.hsinterview.ui.characterdetail

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Episode
import ai.revealtech.hsinterview.data.repository.RickAndMortyRepository
import ai.revealtech.hsinterview.ui.state.UiState
import timber.log.Timber
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: RickAndMortyRepository
) : ViewModel() {

    private val _characterState = MutableStateFlow<UiState<Character>>(UiState.Loading)
    val characterState: StateFlow<UiState<Character>> = _characterState.asStateFlow()

    private val _episodesState = MutableStateFlow<UiState<List<Episode>>>(UiState.Loading)
    val episodesState: StateFlow<UiState<List<Episode>>> = _episodesState.asStateFlow()

    private val _isLoadingMoreEpisodes = MutableStateFlow(false)
    val isLoadingMoreEpisodes: StateFlow<Boolean> = _isLoadingMoreEpisodes.asStateFlow()

    private val allEpisodes = mutableListOf<Episode>()
    private val allEpisodeIds = mutableListOf<Int>()
    private var currentEpisodePage = 0

    fun loadCharacterDetail(characterId: Int) {
        loadCharacter(characterId)
    }

    private fun loadCharacter(characterId: Int) {
        viewModelScope.launch {
            _characterState.value = UiState.Loading

            repository.getCharacter(characterId)
                .onSuccess { character ->
                    _characterState.value = UiState.Success(character)
                    loadEpisodes(character.episode)
                }
                .onFailure { error ->
                    _characterState.value = UiState.Error(error.message ?: DEFAULT_ERROR_MESSAGE)
                }
        }
    }

    private fun loadEpisodes(episodeUrls: List<String>) {
        viewModelScope.launch {
            Timber.d("Starting to load episodes, URLs: $episodeUrls")
            _episodesState.value = UiState.Loading

            val episodeIds = episodeUrls.mapNotNull { url ->
                url.split("/").lastOrNull()?.toIntOrNull()
            }
            Timber.d("Extracted episode IDs: $episodeIds")

            if (episodeIds.isEmpty()) {
                Timber.d("No episode IDs found")
                _episodesState.value = UiState.Success(emptyList())
                return@launch
            }
            resetPaginationState(episodeIds)

            Timber.d("Starting batch loading for ${allEpisodeIds.size} episodes")
            loadAllEpisodesInBatches()
        }
    }

    fun resetPaginationState(episodeIds: List<Int>) {
        allEpisodeIds.clear()
        allEpisodeIds.addAll(episodeIds.sorted())
        allEpisodes.clear()
        currentEpisodePage = 0
    }

    private fun loadAllEpisodesInBatches() {
        viewModelScope.launch {
            try {
                Timber.d("Starting batch loading loop")
                while (currentEpisodePage * EPISODES_PER_PAGE < allEpisodeIds.size) {
                    _isLoadingMoreEpisodes.value = true

                    val startIndex = currentEpisodePage * EPISODES_PER_PAGE
                    val endIndex = minOf(startIndex + EPISODES_PER_PAGE, allEpisodeIds.size)
                    val episodeIdsToLoad = allEpisodeIds.subList(startIndex, endIndex)

                    Timber.d("Loading batch ${currentEpisodePage + 1}: episodes $startIndex to ${endIndex - 1}")

                    val episodes = mutableListOf<Episode>()
                    var hasError = false

                    episodeIdsToLoad.forEach { episodeId ->
                        try {
                            Timber.d("Loading episode $episodeId")
                            repository.getEpisode(episodeId)
                                .onSuccess { episode ->
                                    Timber.d("Successfully loaded episode: ${episode.name}")
                                    episodes.add(episode)
                                }
                                .onFailure { error ->
                                    Timber.e("Failed to load episode $episodeId: ${error.message}")
                                    hasError = true
                                }
                        } catch (e: Exception) {
                            Timber.e("Exception loading episode $episodeId: ${e.message}")
                            hasError = true
                        }
                    }

                    if (hasError && episodes.isEmpty() && allEpisodes.isEmpty()) {
                        Timber.e("Failed to load any episodes in batch")
                        _episodesState.value = UiState.Error(EPISODES_LOAD_ERROR_MESSAGE)
                        return@launch
                    } else {
                        allEpisodes.addAll(episodes.sortedBy { it.id })
                        _episodesState.value = UiState.Success(allEpisodes.toList())
                        currentEpisodePage++
                        Timber.d("Batch loaded successfully. Total episodes: ${allEpisodes.size}")
                    }

                    _isLoadingMoreEpisodes.value = false

                    // Small delay between batches to avoid overwhelming the API
                    delay(BATCH_DELAY_MS)
                }
                Timber.d("All episodes loaded successfully. Total: ${allEpisodes.size}")
            } catch (e: Exception) {
                Timber.e("Exception in batch loading: ${e.message}")
                _episodesState.value = UiState.Error("$EPISODES_ERROR_PREFIX: ${e.message}")
                _isLoadingMoreEpisodes.value = false
            }
        }
    }

    fun hasMoreEpisodes(): Boolean {
        return (currentEpisodePage * EPISODES_PER_PAGE) < allEpisodeIds.size
    }

    companion object {
        private const val EPISODES_PER_PAGE = 10
        private const val BATCH_DELAY_MS = 100L
        private const val DEFAULT_ERROR_MESSAGE = "Failed to load character"
        private const val EPISODES_LOAD_ERROR_MESSAGE = "Failed to load episodes"
        private const val EPISODES_ERROR_PREFIX = "Error loading episodes"

    }
}
