package app.cardium.tcgdex.sdk.model

import kotlinx.serialization.Serializable

@Serializable
data class TcgdxCard(
    val id: String,
    val localId: String? = null,
    val name: String,
    val image: String? = null,
    val imageSmall: String? = null,
    val imageLarge: String? = null,
    val set: TcgdxSet? = null,
    val rarity: String? = null,
    val illustrator: String? = null,
    val hp: Int? = null,
    val types: List<String>? = null,
    val attacks: List<TcgdxAttack>? = null,
    val abilities: List<TcgdxAbility>? = null,
    val weaknesses: List<TcgdxResistanceWeakness>? = null,
    val resistances: List<TcgdxResistanceWeakness>? = null,
    val retreat: Int? = null,
)

@Serializable
data class TcgdxAttack(
    val name: String,
    val cost: List<String>? = null,
    val effect: String? = null,
    val damage: String? = null,
)

@Serializable
data class TcgdxAbility(
    val name: String,
    val type: String? = null,
    val effect: String? = null,
)

@Serializable
data class TcgdxResistanceWeakness(
    val type: String,
    val value: String? = null,
)


