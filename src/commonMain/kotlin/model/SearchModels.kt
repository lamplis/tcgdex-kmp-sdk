package app.cardium.tcgdex.sdk.model

import kotlinx.serialization.Serializable

@Serializable
data class TcgdxSearchResponse(
    val data: List<TcgdxCard>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
)


