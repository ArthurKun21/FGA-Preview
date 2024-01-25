package io.github.fate_grand_automata.scripts.models

import io.github.fate_grand_automata.scripts.entrypoints.AutoBattle
import java.util.ArrayDeque
import java.util.Deque
import java.util.Queue

class AutoSkillCommand private constructor(
    val stages: List<List<List<AutoSkillAction>>>
) {
    operator fun get(stage: Int, turn: Int): List<AutoSkillAction> {
        if (stage < stages.size) {
            val turns = stages[stage]

            if (turn < turns.size) {
                return turns[turn]
            }
        }

        return emptyList()
    }

    companion object {
        private fun getTarget(queue: Queue<Char>): ServantTarget? {
            val peekTarget = queue.peek()
            val target = ServantTarget.list.firstOrNull { it.autoSkillCode == peekTarget }
            if (target != null) {
                queue.remove()
            }

            return target
        }

        private fun getTargets(queue: Queue<Char>): List<ServantTarget> {
            val targets = mutableListOf<ServantTarget>()
            val nextChar = queue.peek()
            if (nextChar == '(') {
                queue.remove()
                var char: Char? = null
                while (queue.isNotEmpty()) {
                    char = queue.remove()
                    if (char == ')') break
                    val target = ServantTarget.list.firstOrNull { it.autoSkillCode == char }
                    target?.let(targets::add)
                }
                if (char != ')') {
                    throw Exception("Found ( but no matching ) in Skill Command")
                }
            } else {
                getTarget(queue)?.let(targets::add)
            }

            return targets
        }

        private fun parseAction(
            queue: Queue<Char>,
            wave: Int = 0,
            turn: Int = 0
        ): AutoSkillAction {
            try {
                return when (val c = queue.remove()) {
                    in Skill.Servant.list.map { it.autoSkillCode } -> {
                        val skill = Skill.Servant.list.first { it.autoSkillCode == c }
                        val targets = getTargets(queue)

                        AutoSkillAction.ServantSkill(
                            skill,
                            targets,
                            wave,
                            turn
                        )
                    }

                    in Skill.Master.list.map { it.autoSkillCode } -> {
                        val skill = Skill.Master.list.first { it.autoSkillCode == c }
                        val target = getTarget(queue)

                        AutoSkillAction.MasterSkill(
                            skill,
                            target,
                            wave,
                            turn
                        )
                    }

                    in CommandCard.NP.list.map { it.autoSkillCode } -> {
                        val np = CommandCard.NP.list.first { it.autoSkillCode == c }

                        AutoSkillAction.Atk.NP(
                            nps = setOf(np),
                            wave,
                            turn
                        )
                    }

                    't' -> {
                        val code = queue.remove()
                        val target = EnemyTarget.list.first { it.autoSkillCode == code }
                        AutoSkillAction.TargetEnemy(
                            target,
                            wave,
                            turn
                        )
                    }

                    'n' -> {
                        val code = queue.remove()
                        val count = code.toString().toInt()
                        AutoSkillAction.Atk.CardsBeforeNP(
                            count,
                            wave,
                            turn
                        )
                    }

                    'x' -> {
                        val startingCode = queue.remove()
                        val starting = OrderChangeMember.Starting.list
                            .first { it.autoSkillCode == startingCode }

                        val subCode = queue.remove()
                        val sub = OrderChangeMember.Sub.list
                            .first { it.autoSkillCode == subCode }

                        AutoSkillAction.OrderChange(
                            starting,
                            sub,
                            wave,
                            turn
                        )
                    }

                    '0' -> AutoSkillAction.Atk.NoOp(
                        wave,
                        turn
                    )

                    else -> throw Exception("Unknown character: $c")
                }
            } catch (e: Exception) {
                throw AutoBattle.BattleExitException(AutoBattle.ExitReason.SkillCommandParseError(e))
            }
        }

        /**
         * Parses the given command string into an `AutoSkillCommand`.
         *
         * The command string is expected to be a sequence of actions
         * separated by commas, with each wave of actions separated by ",#,".
         * Each action is parsed into an `AutoSkillAction` and added to a list of actions for each turn.
         * Actions of type `AutoSkillAction.Atk` are merged with the previous action if it is also of the same type.
         *
         * @param command The command string to parse.
         * @return An `AutoSkillCommand` containing the parsed actions for each turn of each wave.
         * @see AutoSkillAction
         */
        fun parse(command: String): AutoSkillCommand {
            val waves = command
                .split(",#,")

            var currentWaveCount = 0
            var currentTurnCount = 0

            val commandTable = waves
                .map { waveCommandList ->
                    // Track the wave
                    currentWaveCount += 1
                    val turns = waveCommandList.split(',')
                    turns.map { cmd ->
                        // Track the turn
                        currentTurnCount += 1

                        val queue: Deque<Char> = ArrayDeque(cmd.length)
                        queue.addAll(cmd.asIterable())

                        val actions = mutableListOf<AutoSkillAction>()

                        while (!queue.isEmpty()) {
                            val action = parseAction(
                                queue,
                                wave = currentWaveCount,
                                turn = currentTurnCount
                            )

                            // merge NPs and cards before NPs
                            if (actions.isNotEmpty() && action is AutoSkillAction.Atk) {
                                val last = actions.last()

                                if (last is AutoSkillAction.Atk) {
                                    actions[actions.lastIndex] = last + action

                                    continue
                                }
                            }

                            actions.add(action)
                        }

                        actions
                    }
                }

            return AutoSkillCommand(commandTable)
        }
    }
}