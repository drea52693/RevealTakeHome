package ai.revealtech.hsinterview.data.repository

import ai.revealtech.hsinterview.data.api.RickAndMortyApi
import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.CharactersResponse
import ai.revealtech.hsinterview.data.model.Episode
import timber.log.Timber
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RickAndMortyRepository @Inject constructor(
    private val api: RickAndMortyApi
) {

    suspend fun getCharacters(page: Int = 1): Result<CharactersResponse> {
        return executeApiCall(
            operation = "characters for page $page",
            apiCall = { api.getCharacters(page) },
            onSuccess = { response ->
                Timber.d("Successfully fetched ${response.results.size} characters for page $page")
            }
        )
    }

    suspend fun getCharacter(id: Int): Result<Character> {
        return executeApiCall(
            operation = "character with ID $id",
            apiCall = { api.getCharacter(id) },
            onSuccess = { response ->
                Timber.d("Successfully fetched character: ${response.name}")
            }
        )
    }

    suspend fun getEpisode(id: Int): Result<Episode> {
        return executeApiCall(
            operation = "episode with ID $id",
            apiCall = { api.getEpisode(id) },
            onSuccess = { response ->
                Timber.d("Successfully fetched episode: ${response.name}")
            }
        )
    }

    private suspend fun <T> executeApiCall(
        operation: String,
        apiCall: suspend () -> Response<T>,
        onSuccess: (T) -> Unit
    ): Result<T> {
        return try {
            Timber.d("Fetching $operation")
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    onSuccess(body)
                    Result.success(body)
                } ?: run {
                    Timber.w("Empty response body for $operation")
                    Result.failure(Exception(EMPTY_RESPONSE_MESSAGE))
                }
            } else {
                Timber.e("HTTP error fetching $operation: ${response.code()} - ${response.message()}")
                Result.failure(Exception("$HTTP_ERROR_PREFIX ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception fetching $operation")
            Result.failure(e)
        }
    }

    companion object {
        private const val EMPTY_RESPONSE_MESSAGE = "Empty response body"
        private const val HTTP_ERROR_PREFIX = "HTTP"
    }
}
