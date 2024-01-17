package io.github.fate_grand_automata.scripts.models

sealed class EnemyTarget(val autoSkillCode: Char) {
    data object A : EnemyTarget('1')
    data object B : EnemyTarget('2')
    data object C : EnemyTarget('3')

    companion object {
        val list by lazy { listOf(A, B, C) }
    }
}