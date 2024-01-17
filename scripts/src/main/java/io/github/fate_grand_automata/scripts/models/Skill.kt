package io.github.fate_grand_automata.scripts.models

sealed class Skill(val autoSkillCode: Char) {
    sealed class Servant(autoSkillCode: Char) : Skill(autoSkillCode) {
        data object AS1 : Servant('a')
        data object AS2 : Servant('b')
        data object AS3 : Servant('c')

        data object BS1 : Servant('d')
        data object BS2 : Servant('e')
        data object BS3 : Servant('f')

        data object CS1 : Servant('g')
        data object CS2 : Servant('h')
        data object CS3 : Servant('i')

        companion object {
            val list by lazy {
                listOf(
                    AS1,
                    AS2,
                    AS3,
                    BS1,
                    BS2,
                    BS3,
                    CS1,
                    CS2,
                    CS3,
                )
            }
        }
    }

    sealed class Master(autoSkillCode: Char) : Skill(autoSkillCode) {
        data object S1 : Master('j')
        data object S2 : Master('k')
        data object S3 : Master('l')

        companion object {
            val list by lazy { listOf(S1, S2, S3) }
        }
    }
}