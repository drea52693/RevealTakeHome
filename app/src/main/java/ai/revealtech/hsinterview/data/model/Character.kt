package ai.revealtech.hsinterview.data.model

import com.google.gson.annotations.SerializedName

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Location,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class Location(
    val name: String,
    val url: String
)

data class Episode(
    val id: Int,
    val name: String,
    @SerializedName("air_date")
    val airDate: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String
)

data class CharactersResponse(
    val info: Info,
    val results: List<Character>
)

data class EpisodesResponse(
    val info: Info,
    val results: List<Episode>
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
