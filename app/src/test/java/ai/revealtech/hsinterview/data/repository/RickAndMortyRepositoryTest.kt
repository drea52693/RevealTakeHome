package ai.revealtech.hsinterview.data.repository

import ai.revealtech.hsinterview.data.api.RickAndMortyApi
import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.CharactersResponse
import ai.revealtech.hsinterview.data.model.Episode
import ai.revealtech.hsinterview.testutils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class RickAndMortyRepositoryTest {

    @get:Rule
    val mainDispatcherRule = ai.revealtech.hsinterview.testutils.MainDispatcherRule()

    private lateinit var repository: RickAndMortyRepository
    private lateinit var mockApi: RickAndMortyApi

    @Before
    fun setup() {
        mockApi = mockk()
        repository = RickAndMortyRepository(mockApi)
    }

    @Test
    fun `getCharacters returns success when API call succeeds`() = runTest {
        // Given
        val page = 1
        val expectedResponse = TestData.testCharactersResponse
        coEvery { mockApi.getCharacters(page) } returns Response.success(expectedResponse)

        // When
        val result = repository.getCharacters(page)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
    }

    @Test
    fun `getCharacters returns failure when API call fails`() = runTest {
        // Given
        val page = 1
        coEvery { mockApi.getCharacters(page) } returns Response.error(404, "Not Found".toResponseBody())

        // When
        val result = repository.getCharacters(page)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("HTTP 404") == true)
    }

    @Test
    fun `getCharacters returns failure when API call throws exception`() = runTest {
        // Given
        val page = 1
        val exception = IOException("Network error")
        coEvery { mockApi.getCharacters(page) } throws exception

        // When
        val result = repository.getCharacters(page)

        // Then
        assertFalse(result.isSuccess)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getCharacters returns failure when response body is null`() = runTest {
        // Given
        val page = 1
        coEvery { mockApi.getCharacters(page) } returns Response.success(null)

        // When
        val result = repository.getCharacters(page)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("Empty response body") == true)
    }

    @Test
    fun `getCharacter returns success when API call succeeds`() = runTest {
        // Given
        val characterId = 1
        val expectedCharacter = TestData.testCharacter
        coEvery { mockApi.getCharacter(characterId) } returns Response.success(expectedCharacter)

        // When
        val result = repository.getCharacter(characterId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedCharacter, result.getOrNull())
    }

    @Test
    fun `getCharacter returns failure when API call fails`() = runTest {
        // Given
        val characterId = 1
        coEvery { mockApi.getCharacter(characterId) } returns Response.error(404, "Not Found".toResponseBody())

        // When
        val result = repository.getCharacter(characterId)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("HTTP 404") == true)
    }

    @Test
    fun `getEpisode returns success when API call succeeds`() = runTest {
        // Given
        val episodeId = 1
        val expectedEpisode = TestData.testEpisode
        coEvery { mockApi.getEpisode(episodeId) } returns Response.success(expectedEpisode)

        // When
        val result = repository.getEpisode(episodeId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedEpisode, result.getOrNull())
    }

    @Test
    fun `getEpisode returns failure when API call fails`() = runTest {
        // Given
        val episodeId = 1
        coEvery { mockApi.getEpisode(episodeId) } returns Response.error(404, "Not Found".toResponseBody())

        // When
        val result = repository.getEpisode(episodeId)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("HTTP 404") == true)
    }
}
