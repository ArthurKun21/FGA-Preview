package io.github.fate_grand_automata.scripts.models

sealed class AutoSkillAction(
    open val wave: Int,
    open val turn: Int
) {
    data class Atk(
        val nps: Set<CommandCard.NP>,
        val cardsBeforeNP: Int,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    ) {
        init {
            require(cardsBeforeNP in 0..2) { "Only 0, 1 or 2 cards can be used before NP" }
        }

        operator fun plus(other: Atk) =
            Atk(
                nps + other.nps,
                cardsBeforeNP + other.cardsBeforeNP,
                other.wave,
                other.turn
            )

        fun toNPUsage() =
            NPUsage(nps, cardsBeforeNP)

        companion object {
            fun noOp(
                wave: Int,
                turn: Int
            ) = Atk(
                emptySet(),
                0,
                wave,
                turn
            )

            fun np(
                np: CommandCard.NP,
                wave: Int,
                turn: Int
            ) = Atk(
                setOf(np),
                0,
                wave,
                turn
            )

            fun cardsBeforeNP(
                cards: Int,
                wave: Int,
                turn: Int
            ) = Atk(
                emptySet(),
                cards,
                wave,
                turn
            )
        }
    }

    class ServantSkill(
        val skill: Skill.Servant,
        val targets: List<ServantTarget>,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    )

    class MasterSkill(
        val skill: Skill.Master,
        val target: ServantTarget?,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    )

    class TargetEnemy(
        val enemy: EnemyTarget,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    )

    class OrderChange(
        val starting: OrderChangeMember.Starting,
        val sub: OrderChangeMember.Sub,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    )
}