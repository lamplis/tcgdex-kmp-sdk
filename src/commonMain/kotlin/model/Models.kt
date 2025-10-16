package app.cardium.tcgdex.sdk.model

import kotlinx.serialization.Serializable

@Serializable
data class TcgdxSet(
    val id: String,
    val name: String,
    val logo: String? = null,
    val symbol: String? = null,
    val releaseDate: String? = null,
    val cardCount: TcgdxCardCount? = null,
    val serie: TcgdxSerie? = null,
)

@Serializable
data class TcgdxCardCount(
    val official: Int? = null,
    val total: Int? = null,
)

@Serializable
data class TcgdxSerie(
    val id: String,
    val name: String,
)

@Serializable
data class TcgdxSetResponse(
    val data: List<TcgdxSet>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
)
