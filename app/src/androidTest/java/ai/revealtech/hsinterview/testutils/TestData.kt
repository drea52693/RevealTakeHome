package ai.revealtech.hsinterview.testutils

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Episode
import ai.revealtech.hsinterview.data.model.Location
import ai.revealtech.hsinterview.data.model.CharactersResponse
import ai.revealtech.hsinterview.data.model.Info

object TestData {

    val testLocation = Location(
        name = "Earth (C-137)",
        url = "https://rickandmortyapi.com/api/location/1"
    )

    val testOrigin = Location(
        name = "Earth (Replacement Dimension)",
        url = "https://rickandmortyapi.com/api/location/20"
    )

    val testCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = testOrigin,
        location = testLocation,
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2"
        ),
        url = "https://rickandmortyapi.com/api/character/1",
        created = "2017-11-04T18:48:46.250Z"
    )

    val testLocation2 = Location(
        name = "Citadel of Ricks",
        url = "https://rickandmortyapi.com/api/location/3"
    )

    val testCharacter2 = Character(
        id = 2,
        name = "Morty Smith",
        status = "Dead",
        species = "Alien",
        type = "",
        gender = "Male",
        origin = testOrigin,
        location = testLocation2,
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        episode = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2"
        ),
        url = "https://rickandmortyapi.com/api/character/2",
        created = "2017-11-04T18:50:21.651Z"
    )

    val testEpisode = Episode(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = "S01E01",
        characters = listOf(
            "https://rickandmortyapi.com/api/character/1",
            "https://rickandmortyapi.com/api/character/2"
        ),
        url = "https://rickandmortyapi.com/api/episode/1",
        created = "2017-11-10T12:56:33.798Z"
    )

    val testEpisode2 = Episode(
        id = 2,
        name = "Lawnmower Dog",
        airDate = "December 9, 2013",
        episode = "S01E02",
        characters = listOf(
            "https://rickandmortyapi.com/api/character/1",
            "https://rickandmortyapi.com/api/character/2"
        ),
        url = "https://rickandmortyapi.com/api/episode/2",
        created = "2017-11-10T12:56:33.798Z"
    )

    val testInfo = Info(
        count = 826,
        pages = 42,
        next = "https://rickandmortyapi.com/api/character/?page=2",
        prev = null
    )

    val testCharactersResponse = CharactersResponse(
        info = testInfo,
        results = listOf(testCharacter, testCharacter2)
    )

    val testCharactersResponsePage2 = CharactersResponse(
        info = Info(
            count = 826,
            pages = 42,
            next = "https://rickandmortyapi.com/api/character/?page=3",
            prev = "https://rickandmortyapi.com/api/character/?page=1"
        ),
        results = listOf(testCharacter, testCharacter2)
    )

    val testCharactersResponseLastPage = CharactersResponse(
        info = Info(
            count = 826,
            pages = 42,
            next = null,
            prev = "https://rickandmortyapi.com/api/character/?page=41"
        ),
        results = listOf(testCharacter)
    )
}
