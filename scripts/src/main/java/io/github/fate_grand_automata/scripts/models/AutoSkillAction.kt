package io.github.fate_grand_automata.scripts.models

sealed class AutoSkillAction(
    open val wave: Int,
    open val turn: Int
) {
    sealed class Atk(
        open val nps: Set<CommandCard.NP>,
        open val cardsBeforeNP: Int,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    ) {

        operator fun plus(other: Atk): Atk {
            val nps = nps + other.nps
            val cardsBeforeNP = cardsBeforeNP + other.cardsBeforeNP
            return when {
                nps.isNotEmpty() -> np(nps, wave, turn)
                cardsBeforeNP > 0 -> cardsBeforeNPAction(cardsBeforeNP, wave, turn)
                else -> noOp(wave, turn)
            }
        }

        data class noOp(
            override val wave: Int,
            override val turn: Int
        ): Atk(
            emptySet(),
            0,
            wave,
            turn
        )

        data class np(
            override val nps: Set<CommandCard.NP>,
            override val wave: Int,
            override val turn: Int
        ): Atk(
            nps,
            0,
            wave,
            turn
        )

        data class cardsBeforeNPAction(
            override val cardsBeforeNP: Int,
            override val wave: Int,
            override val turn: Int
        ): Atk(
            emptySet(),
            cardsBeforeNP,
            wave,
            turn
        ) {
            init {
                require(cardsBeforeNP in 0..2) { "Only 0, 1 or 2 cards can be used before NP" }
            }
        }

        fun toNPUsage() =
            NPUsage(nps, cardsBeforeNP)


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