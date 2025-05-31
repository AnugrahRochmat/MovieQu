package anugrah.rochmat.moviequ.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GenreResponse(
    @Json(name = "genres") val genres: List<GenreData>
)

@JsonClass(generateAdapter = true)
class GenreData(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)