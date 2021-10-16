package com.mathewsachin.fategrandautomata.scripts.models

import com.mathewsachin.fategrandautomata.scripts.enums.CardAffinityEnum
import com.mathewsachin.fategrandautomata.scripts.enums.CardTypeEnum

data class ParsedCard(
    val card: CommandCard.Face,
    val type: CardTypeEnum,
    val affinity: CardAffinityEnum,
    val isStunned: Boolean,
    val servant: TeamSlot
) {
    override fun equals(other: Any?) =
        other is ParsedCard && card == other.card

    override fun hashCode() = card.hashCode()
}