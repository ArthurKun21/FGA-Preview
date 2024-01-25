package io.github.fate_grand_automata.scripts.models

sealed class FieldSlot(val position: Int) {
    object A: FieldSlot(1)
    object B: FieldSlot(2)
    object C: FieldSlot(3)

    companion object {
        val list by lazy { listOf(A, B, C) }
    }

    override fun toString() = "[$position]"
}

fun FieldSlot.skill1() =
    when (this) {
        FieldSlot.A -> Skill.Servant.AS1
        FieldSlot.B -> Skill.Servant.BS1
        FieldSlot.C -> Skill.Servant.CS1
    }

fun FieldSlot.skill2() =
    when (this) {
        FieldSlot.A -> Skill.Servant.AS2
        FieldSlot.B -> Skill.Servant.BS2
        FieldSlot.C -> Skill.Servant.CS2
    }

fun FieldSlot.skill3() =
    when (this) {
        FieldSlot.A -> Skill.Servant.AS3
        FieldSlot.B -> Skill.Servant.BS3
        FieldSlot.C -> Skill.Servant.CS3
    }

fun FieldSlot.skills() =
    listOf(skill1(), skill2(), skill3())