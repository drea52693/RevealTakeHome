package ai.revealtech.hsinterview.data.api

import ai.revealtech.hsinterview.data.model.CharactersResponse
import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.data.model.Episode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): Response<CharactersResponse>

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): Response<Character>

    @GET("episode/{id}")
    suspend fun getEpisode(
        @Path("id") id: Int
    ): Response<Episode>
}

