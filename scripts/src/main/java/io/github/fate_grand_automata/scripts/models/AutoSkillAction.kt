package io.github.fate_grand_automata.scripts.models

sealed class AutoSkillAction(
    open val wave: Int,
    open val turn: Int
) {
    sealed class Atk(
        open val nps: Set<CommandCard.NP>,
        open val numberOfCardsBeforeNP: Int,
        override val wave: Int,
        override val turn: Int
    ) : AutoSkillAction(
        wave,
        turn
    ) {
        companion object {
            /**
             * Construct an Atk action.
             * @param nps The NPs used in this action.
             * @param numberOfCardsBeforeNP The number of cards used before NP.
             * @param wave The wave of this action.
             * @param turn The turn of this action.
             * @throws IllegalArgumentException if numberOfCardsBeforeNP is not in 0..2
             * @return The constructed Atk action.
             */
            fun construct(
                nps: Set<CommandCard.NP> = emptySet(),
                numberOfCardsBeforeNP: Int = 0,
                wave: Int = 0,
                turn: Int = 0
            ) = when {
                nps.isNotEmpty() -> NP(nps, wave, turn)
                numberOfCardsBeforeNP > 0 -> CardsBeforeNP(numberOfCardsBeforeNP, wave, turn)
                else -> NoOp(wave, turn)
            }
        }
        operator fun plus(other: Atk): Atk {
            val nps = nps + other.nps
            val cardsBeforeNP = numberOfCardsBeforeNP + other.numberOfCardsBeforeNP
            return when {
                nps.isNotEmpty() -> NP(nps, wave, turn)
                cardsBeforeNP > 0 -> CardsBeforeNP(cardsBeforeNP, wave, turn)
                else -> NoOp(wave, turn)
            }
        }

        /**
         * NoOp is used to represent a move where no attack is performed.
         */
        data class NoOp(
            override val wave: Int,
            override val turn: Int
        ): Atk(
            emptySet(),
            0,
            wave,
            turn
        )

        /**
         * NP is used to represent a move where NP is used and no cards are used before NP.
         */
        data class NP(
            override val nps: Set<CommandCard.NP>,
            override val wave: Int,
            override val turn: Int
        ): Atk(
            nps,
            0,
            wave,
            turn
        )

        /**
         * CardsBeforeNP is used to represent a move where cards are used before NP.
         * @param numberOfCardsBeforeNP The number of cards used before NP.
         * @throws IllegalArgumentException if numberOfCardsBeforeNP is not in 0..2
         */
        data class CardsBeforeNP(
            override val numberOfCardsBeforeNP: Int,
            override val wave: Int,
            override val turn: Int
        ): Atk(
            emptySet(),
            numberOfCardsBeforeNP,
            wave,
            turn
        ) {
            init {
                require(numberOfCardsBeforeNP in 0..2) { "Only 0, 1 or 2 cards can be used before NP" }
            }
        }

        fun toNPUsage() =
            NPUsage(nps, numberOfCardsBeforeNP)


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