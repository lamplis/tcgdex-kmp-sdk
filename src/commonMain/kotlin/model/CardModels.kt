package app.cardium.tcgdex.sdk.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TcgdxCard(
    val id: String,
    val localId: String? = null,
    val name: String,
    val image: String? = null,
    val imageSmall: String? = null,
    val imageLarge: String? = null,
    val set: TcgdxSet? = null,
    val number: String? = null,
    val category: String? = null,
    val rarity: String? = null,
    val illustrator: String? = null,
    val hp: Int? = null,
    val types: List<String>? = null,
    val attacks: List<TcgdxAttack>? = null,
    val abilities: List<TcgdxAbility>? = null,
    val weaknesses: List<TcgdxResistanceWeakness>? = null,
    val resistances: List<TcgdxResistanceWeakness>? = null,
    val retreat: Int? = null,
    // Pricing integrations
    val prices: TcgdxPrices? = null,
    val tcgplayer: TcgdxTcgPlayer? = null,
    val cardmarket: TcgdxCardMarket? = null,
    // v2 REST pricing container (preferred shape when available)
    val pricing: TcgdxPricing? = null,
)

@Serializable
data class TcgdxAttack(
    val name: String = "",
    val cost: List<String>? = null,
    val effect: String? = null,
    val damage: String? = null,
)

@Serializable
data class TcgdxAbility(
    val name: String = "",
    val type: String? = null,
    val effect: String? = null,
)

@Serializable
data class TcgdxResistanceWeakness(
    val type: String,
    val value: String? = null,
)

// ===== Pricing models =====

@Serializable
data class TcgdxPrices(
    val normal: TcgdxPriceInfo? = null,
    val holofoil: TcgdxPriceInfo? = null,
    val reverseHolofoil: TcgdxPriceInfo? = null,
    val firstEditionHolofoil: TcgdxPriceInfo? = null,
    val unlimitedHolofoil: TcgdxPriceInfo? = null,
)

@Serializable
data class TcgdxPriceInfo(
    val low: Double? = null,
    val mid: Double? = null,
    val high: Double? = null,
    val market: Double? = null,
    val directLow: Double? = null,
)

@Serializable
data class TcgdxTcgPlayer(
    val url: String? = null,
    val updatedAt: String? = null,
    val prices: TcgdxTcgPlayerPrices? = null,
)

@Serializable
data class TcgdxTcgPlayerPrices(
    val normal: TcgdxPriceInfo? = null,
    val holofoil: TcgdxPriceInfo? = null,
    val reverseHolofoil: TcgdxPriceInfo? = null,
    val firstEditionHolofoil: TcgdxPriceInfo? = null,
    val firstEditionNormal: TcgdxPriceInfo? = null,
)

@Serializable
data class TcgdxCardMarket(
    val url: String? = null,
    val updatedAt: String? = null,
    val prices: TcgdxCardMarketPrices? = null,
)

@Serializable
data class TcgdxCardMarketPrices(
    val averageSellPrice: Double? = null,
    val lowPrice: Double? = null,
    val trendPrice: Double? = null,
    val germanProLow: Double? = null,
    val suggestedPrice: Double? = null,
    val reverseHoloSell: Double? = null,
    val reverseHoloLow: Double? = null,
    val reverseHoloTrend: Double? = null,
    val lowPriceExPlus: Double? = null,
    val avg1: Double? = null,
    val avg7: Double? = null,
    val avg30: Double? = null,
    val reverseHoloAvg1: Double? = null,
    val reverseHoloAvg7: Double? = null,
    val reverseHoloAvg30: Double? = null,
)

// ===== v2 REST pricing container =====

@Serializable
data class TcgdxPricing(
    val cardmarket: TcgdxV2CardMarket? = null,
    val tcgplayer: TcgdxV2TcgPlayer? = null,
)

@Serializable
data class TcgdxV2CardMarket(
    val updated: String? = null,
    val unit: String? = null,
    @SerialName("avg") val avg: Double? = null,
    @SerialName("low") val low: Double? = null,
    @SerialName("trend") val trend: Double? = null,
    @SerialName("avg1") val avg1: Double? = null,
    @SerialName("avg7") val avg7: Double? = null,
    @SerialName("avg30") val avg30: Double? = null,
    @SerialName("avg-holo") val avgHolo: Double? = null,
    @SerialName("low-holo") val lowHolo: Double? = null,
    @SerialName("trend-holo") val trendHolo: Double? = null,
    @SerialName("avg1-holo") val avg1Holo: Double? = null,
    @SerialName("avg7-holo") val avg7Holo: Double? = null,
    @SerialName("avg30-holo") val avg30Holo: Double? = null,
)

@Serializable
data class TcgdxV2TcgPlayer(
    val updated: String? = null,
    val unit: String? = null,
    val normal: TcgdxV2PriceInfo? = null,
    @SerialName("reverse-holofoil") val reverseHolofoil: TcgdxV2PriceInfo? = null,
    val holofoil: TcgdxV2PriceInfo? = null,
)

@Serializable
data class TcgdxV2PriceInfo(
    @SerialName("lowPrice") val lowPrice: Double? = null,
    @SerialName("midPrice") val midPrice: Double? = null,
    @SerialName("highPrice") val highPrice: Double? = null,
    @SerialName("marketPrice") val marketPrice: Double? = null,
    @SerialName("directLowPrice") val directLowPrice: Double? = null,
)
