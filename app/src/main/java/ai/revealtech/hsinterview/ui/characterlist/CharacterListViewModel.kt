package ai.revealtech.hsinterview.ui.characterlist

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.repository.RickAndMortyRepository
import ai.revealtech.hsinterview.ui.state.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: RickAndMortyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Character>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Character>>> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = INITIAL_PAGE
    private var hasNextPage = true
    private val allCharacters = mutableListOf<Character>()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            currentPage = INITIAL_PAGE
            allCharacters.clear()

            repository.getCharacters(currentPage)
                .onSuccess { response ->
                    allCharacters.addAll(response.results)
                    hasNextPage = response.info.next != null
                    _uiState.value = UiState.Success(allCharacters.toList())
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: DEFAULT_ERROR_MESSAGE)
                }
        }
    }

    fun loadMoreCharacters() {
        if (!hasNextPage || _isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage++

            repository.getCharacters(currentPage)
                .onSuccess { response ->
                    allCharacters.addAll(response.results)
                    hasNextPage = response.info.next != null
                    _uiState.value = UiState.Success(allCharacters.toList())
                }
                .onFailure { error ->
                    currentPage--
                    _uiState.value = UiState.Error(error.message ?: LOAD_MORE_ERROR_MESSAGE)
                }
                .also {
                    _isLoadingMore.value = false
                }
        }
    }

    fun refresh() {
        loadCharacters()
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val DEFAULT_ERROR_MESSAGE = "Unknown error occurred"
        private const val LOAD_MORE_ERROR_MESSAGE = "Failed to load more characters"
    }
}
