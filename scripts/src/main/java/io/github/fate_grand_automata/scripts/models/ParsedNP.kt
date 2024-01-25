package io.github.fate_grand_automata.scripts.models

import io.github.fate_grand_automata.scripts.enums.CardTypeEnum

data class ParsedNP(
    val np: CommandCard.NP,
    val servant: TeamSlot,
    val fieldSlot: FieldSlot?,
    val type: CardTypeEnum,
    val isStunned: Boolean = false
) {
    override fun equals(other: Any?): Boolean =
        other is ParsedNP && np == other.np

    override fun hashCode(): Int = np.hashCode()
}