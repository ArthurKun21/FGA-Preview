package io.github.fate_grand_automata.scripts.models

sealed class OrderChangeMember(val autoSkillCode: Char) {
    sealed class Starting(autoSkillCode: Char) : OrderChangeMember(autoSkillCode) {
        data object A : Starting('1')
        data object B : Starting('2')
        data object C : Starting('3')

        companion object {
            val list by lazy { listOf(A, B, C) }
        }
    }

    sealed class Sub(autoSkillCode: Char) : OrderChangeMember(autoSkillCode) {
        data object A : Sub('1')
        data object B : Sub('2')
        data object C : Sub('3')

        companion object {
            val list by lazy { listOf(A, B, C) }
        }
    }
}