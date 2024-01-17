package io.github.fate_grand_automata.ui.skill_maker

import io.github.fate_grand_automata.scripts.models.AutoSkillAction
import io.github.fate_grand_automata.scripts.models.ServantTarget
import io.github.fate_grand_automata.scripts.models.Skill

sealed class SkillMakerEntry(
    open val wave: Int,
    open val turn: Int
) {
    class Action(
        val action: AutoSkillAction,
        override val wave: Int = action.wave,
        override val turn: Int = action.turn
    ) : SkillMakerEntry(
        wave = wave,
        turn = turn
    ) {
        private fun toString(skill: Skill, target: ServantTarget?) = when (target) {
            null -> "${skill.autoSkillCode}"
            else -> "${skill.autoSkillCode}${target.autoSkillCode}"
        }


        private fun toString(skill: Skill, targets: List<ServantTarget>) = when {
            targets.isEmpty() -> "${skill.autoSkillCode}"
            targets.size == 1 -> "${skill.autoSkillCode}${targets[0].autoSkillCode}"
            else -> "${skill.autoSkillCode}(${targets.map(ServantTarget::autoSkillCode).joinToString("")})"
        }

        override fun toString() = when (action) {
            is AutoSkillAction.Atk -> {
                if (action == AutoSkillAction.Atk.noOp(action.wave, action.turn)) {
                    "0"
                } else {
                    val cardsBeforeNP = if (action.numberOfCardsBeforeNP > 0) {
                        "n${action.numberOfCardsBeforeNP}"
                    } else ""

                    cardsBeforeNP + action.nps.joinToString("") {
                        it.autoSkillCode.toString()
                    }
                }
            }

            is AutoSkillAction.ServantSkill -> toString(action.skill, action.targets)
            is AutoSkillAction.MasterSkill -> toString(action.skill, action.target)
            is AutoSkillAction.TargetEnemy -> "t${action.enemy.autoSkillCode}"
            is AutoSkillAction.OrderChange -> "x${action.starting.autoSkillCode}${action.sub.autoSkillCode}"
        }
    }

    object Start : SkillMakerEntry(0, 0) {
        override fun toString() = ""
    }

    sealed class Next(
        val action: AutoSkillAction.Atk,
        override val wave: Int,
        override val turn: Int
    ) : SkillMakerEntry(
        wave = wave,
        turn = turn
    ) {
        protected fun AutoSkillAction.Atk.str() = when (action) {
            is AutoSkillAction.Atk.noOp -> ""
            else -> Action(this).toString()
        }

        class Wave(action: AutoSkillAction.Atk) : Next(action, action.wave, action.turn) {
            override fun toString() = "${action.str()},#,"
        }

        class Turn(action: AutoSkillAction.Atk) : Next(action, action.wave, action.turn) {
            override fun toString() = "${action.str()},"
        }
    }
}