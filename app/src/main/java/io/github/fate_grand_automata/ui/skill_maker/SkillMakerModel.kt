package io.github.fate_grand_automata.ui.skill_maker

import androidx.compose.runtime.toMutableStateList
import io.github.fate_grand_automata.scripts.models.AutoSkillAction
import io.github.fate_grand_automata.scripts.models.AutoSkillCommand

class SkillMakerModel(skillString: String) {

    /**
     * Reduce the list of [SkillMakerEntry] by combining consecutive [SkillMakerEntry.Action]s
     * into a single [SkillMakerEntry.Action] with a [SkillMakerEntry.Action.action] of type [AutoSkillAction.Atk].
     * The [SkillMakerEntry.Action.action] of the resulting [SkillMakerEntry.Action] will be the last [AutoSkillAction.Atk]
     * in the consecutive [SkillMakerEntry.Action]s.
     *
     * @param acc The accumulator list
     * @param add The list to add
     * @param separator The separator to add between the accumulator and the list to add
     *
     * @return The reduced list
     */
    private fun reduce(
        acc: List<SkillMakerEntry>,
        add: List<SkillMakerEntry>,
        separator: (AutoSkillAction.Atk) -> SkillMakerEntry.Next
    ): List<SkillMakerEntry> {
        if (acc.isNotEmpty()) {
            val last = acc.last()

            if (last is SkillMakerEntry.Action && last.action is AutoSkillAction.Atk) {
                return acc.subList(0, acc.lastIndex) + separator(last.action) + add
            }
        }
        val first = add.first()
        val wave = first.wave - 1
        val turn = first.turn - 1
        return acc + separator(AutoSkillAction.Atk.NoOp(wave, turn)) + add
    }

    val skillCommand = AutoSkillCommand.parse(skillString)
        .stages
        .map { turns ->
            turns
                .map { turn ->
                    turn.map<AutoSkillAction, SkillMakerEntry> {
                        SkillMakerEntry.Action(it)
                    }
                }
                .reduce { acc, turn ->
                    reduce(acc, turn) { SkillMakerEntry.Next.Turn(it) }
                }
        }
        .reduce { acc, stage ->
            reduce(acc, stage) { SkillMakerEntry.Next.Wave(it) }
        }
        .let { listOf(SkillMakerEntry.Start) + it }
        .toMutableStateList()

    override fun toString(): String {
        fun getSkillCmd(): List<SkillMakerEntry> {
            if (skillCommand.isNotEmpty()) {
                val last = skillCommand.last()

                // remove trailing ',' or ',#,'
                if (last is SkillMakerEntry.Next) {
                    return skillCommand.subList(0, skillCommand.lastIndex) + SkillMakerEntry.Action(last.action)
                }
            }

            return skillCommand
        }

        return getSkillCmd().joinToString("")
    }
}