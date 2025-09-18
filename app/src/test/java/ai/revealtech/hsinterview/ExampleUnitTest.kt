package ai.revealtech.hsinterview

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Location
import ai.revealtech.hsinterview.ui.state.UiState
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `UiState Success contains correct data`() {
        // Given
        val testData = "Test Data"
        
        // When
        val successState = UiState.Success(testData)
        
        // Then
        assertEquals(testData, (successState as UiState.Success).data)
    }

    @Test
    fun `UiState Error contains correct message`() {
        // Given
        val errorMessage = "Test Error"
        
        // When
        val errorState = UiState.Error(errorMessage)
        
        // Then
        assertEquals(errorMessage, (errorState as UiState.Error).message)
    }

    @Test
    fun `UiState Loading is correctly identified`() {
        // Given
        val loadingState = UiState.Loading
        
        // When & Then
        assertTrue(loadingState is UiState.Loading)
    }

    @Test
    fun `Character model has correct properties`() {
        // Given
        val location = Location("Earth", "https://example.com/location/1")
        val character = Character(
            id = 1,
            name = "Test Character",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = location,
            location = location,
            image = "https://example.com/image.jpg",
            episode = emptyList(),
            url = "https://example.com/character/1",
            created = "2023-01-01T00:00:00.000Z"
        )
        
        // When & Then
        assertEquals(1, character.id)
        assertEquals("Test Character", character.name)
        assertEquals("Alive", character.status)
        assertEquals("Human", character.species)
        assertEquals("Male", character.gender)
        assertEquals("Earth", character.origin.name)
        assertEquals("Earth", character.location.name)
        assertEquals("https://example.com/image.jpg", character.image)
        assertTrue(character.episode.isEmpty())
        assertEquals("https://example.com/character/1", character.url)
        assertEquals("2023-01-01T00:00:00.000Z", character.created)
    }
}