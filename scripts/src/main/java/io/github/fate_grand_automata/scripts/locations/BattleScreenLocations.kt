package io.github.fate_grand_automata.scripts.locations

import io.github.fate_grand_automata.scripts.models.EnemyTarget
import io.github.fate_grand_automata.scripts.models.FieldSlot
import io.github.fate_grand_automata.scripts.models.OrderChangeMember
import io.github.fate_grand_automata.scripts.models.ServantTarget
import io.github.fate_grand_automata.scripts.models.Skill
import io.github.fate_grand_automata.scripts.models.skill2
import io.github.fate_grand_automata.scripts.models.skill3
import io.github.lib_automata.Location
import io.github.lib_automata.Region
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class BattleScreenLocations @Inject constructor(
    scriptAreaTransforms: IScriptAreaTransforms,
    val master: MasterLocations
) : IScriptAreaTransforms by scriptAreaTransforms {
    fun locate(orderChangeMember: OrderChangeMember) = when (orderChangeMember) {
        OrderChangeMember.Starting.A -> -1000
        OrderChangeMember.Starting.B -> -600
        OrderChangeMember.Starting.C -> -200
        OrderChangeMember.Sub.A -> 200
        OrderChangeMember.Sub.B -> 600
        OrderChangeMember.Sub.C -> 1000
    }.let { x -> Location(x, 700) }.xFromCenter()

    fun locate(servantTarget: ServantTarget) = when (servantTarget) {
        ServantTarget.A -> -580
        ServantTarget.B -> 0
        ServantTarget.C -> 660
        ServantTarget.Left -> -290
        ServantTarget.Right -> 330
        ServantTarget.Option1 -> 0
        ServantTarget.Option2 -> 470
        ServantTarget.Melusine -> null
    }?.let { x -> Location(x, 880) }?.xFromCenter()

    fun locate(skill: Skill.Servant) = when (skill) {
        Skill.Servant.AS1 -> 148
        Skill.Servant.AS2 -> 324
        Skill.Servant.AS3 -> 500
        Skill.Servant.BS1 -> 784
        Skill.Servant.BS2 -> 960
        Skill.Servant.BS3 -> 1136
        Skill.Servant.CS1 -> 1418
        Skill.Servant.CS2 -> 1594
        Skill.Servant.CS3 -> 1770
    }.let { x -> Location(x + if (isWide) 108 else 0, if (isWide) 1117 else 1158) }


    private fun Location.threeEnemyFormation() = this + Location(x = if (isWide) 183 else 0, y = 0)

    private fun Location.sixEnemyFormation() = this + Location(x = if (isWide) 155 else 0, y = 0)

    fun locate(enemy: EnemyTarget) = when (enemy) {
        EnemyTarget.A3 -> Location(90, 80).threeEnemyFormation()
        EnemyTarget.B3 -> Location(570, 80).threeEnemyFormation()
        EnemyTarget.C3 -> Location(1050, 80).threeEnemyFormation()

        EnemyTarget.A6 -> Location(281, 64).sixEnemyFormation()
        EnemyTarget.B6 -> Location(681, 64).sixEnemyFormation()
        EnemyTarget.C6 -> Location(1081, 64).sixEnemyFormation()

        EnemyTarget.D6 -> Location(82, 261).sixEnemyFormation()
        EnemyTarget.E6 -> Location(482, 261).sixEnemyFormation()
        EnemyTarget.F6 -> Location(882, 261).sixEnemyFormation()
    }

    private fun Region.threeEnemyFormation() = this + Location(if (isWide) 150 else 0, 0)

    private fun Region.sixEnemyFormationUpper() = this + Location(x = if (isWide) 155 else 0, y = 0)

    private fun Region.sixEnemyFormationLower() = this + Location(x = if (isWide) 153 else 0, y = if (isWide) -1 else 0)

    fun dangerRegion(enemy: EnemyTarget) = when (enemy) {
        EnemyTarget.A3 -> Region(0, 0, 485, 220).threeEnemyFormation()
        EnemyTarget.B3 -> Region(485, 0, 482, 220).threeEnemyFormation()
        EnemyTarget.C3 -> Region(967, 0, 476, 220).threeEnemyFormation()

        EnemyTarget.A6 -> Region(220, 136, 112, 22).sixEnemyFormationUpper()
        EnemyTarget.B6 -> Region(620, 136, 112, 22).sixEnemyFormationUpper()
        EnemyTarget.C6 -> Region(1020, 136, 112, 22).sixEnemyFormationUpper()

        EnemyTarget.D6 -> Region(24, 334, 112, 22).sixEnemyFormationLower()
        EnemyTarget.E6 -> Region(424, 334, 112, 22).sixEnemyFormationLower()
        EnemyTarget.F6 -> Region(824, 334, 112, 22).sixEnemyFormationLower()
    }

    val screenCheckRegion =
        (if (isWide)
            Region(-660, -210, 400, 175)
        else Region(-455, -181, 336, 116))
            .xFromRight()
            .yFromBottom()

    fun servantPresentRegion(slot: FieldSlot) =
        slot.skill3().let {
            val skill3Location = locate(it)

            Region(
                skill3Location.x + 35,
                skill3Location.y + 67,
                120,
                120
            )
        }

    val attackClick =
        (if (isWide)
            Location(-460, -230)
        else Location(-260, -240))
            .xFromRight()
            .yFromBottom()

    val skillOkClick = Location(400, 850).xFromCenter()
    val orderChangeOkClick = Location(0, 1260).xFromCenter()
    val extraInfoWindowCloseClick = Location(-300, 940).xFromRight()
    val skillUseRegion = Region(-210, 320, 420, 85).xFromCenter()

    fun servantOpenDetailsClick(slot: FieldSlot) =
        Location(locate(slot.skill2()).x, 810)

    fun servantChangeCheckRegion(slot: FieldSlot) =
        slot.skill2().let {
            val x = locate(it).x

            Region(x + 20, 865, 40, 80)
        }

    fun servantChangeSupportCheckRegion(slot: FieldSlot) =
        slot.skill2().let {
            val x = locate(it).x

            Region(x + 25, 710, 300, 170)
        }

    fun imageRegion(skill: Skill.Servant) =
        Region(22, 28, 30, 30) + locate(skill)

    val servantDetailsInfoClick = Location(-660, 110).xFromCenter()
    val servantDetailsFaceCardRegion = Region(-685, 330, 110, 60).xFromCenter()

    val battleSafeMiddleOfScreenClick = Location(0, 550).xFromCenter()
}