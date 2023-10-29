package io.github.fate_grand_automata.scripts.modules

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.enums.CEDisplayChangeAreaEnum
import io.github.lib_automata.Location
import io.github.lib_automata.Region
import io.github.lib_automata.Scale
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class AutoSetup @Inject constructor(
    api: IFgoAutomataApi,
    private val scale: Scale
) : IFgoAutomataApi by api {


    fun Region.detectNumberInText(): Int? {
        val text = this
            .detectText(false) // replace common OCR mistakes
            .replace("%", "x")
            .replace("S", "5")
            .replace("O", "0")
            .lowercase()
        val regex = Regex("""(\d+)""")
        return regex.find(text)?.groupValues?.getOrNull(1)?.toInt()
    }

    fun getMinimumSkillLevel() {
        val skill1Text = locations.skillUpgrade.skill1TextRegion.detectNumberInText()
        prefs.skillUpgrade.minSkill1 = skill1Text ?: 1
        val skill2Text = locations.skillUpgrade.skill2TextRegion.detectNumberInText()

        prefs.skillUpgrade.minSkill2 = skill2Text ?: 1
        prefs.skillUpgrade.skill2Available = skill2Text != null

        val skill3Text = locations.skillUpgrade.skill3TextRegion.detectNumberInText()

        prefs.skillUpgrade.minSkill3 = skill3Text ?: 1
        prefs.skillUpgrade.skill3Available = skill3Text != null
    }

    fun checkIfEmptyEnhance() {
        val emptyEnhance = images[Images.EmptyEnhance] in locations.emptyEnhanceRegion

        prefs.craftEssence.emptyEnhance = emptyEnhance
    }

    fun checkIfCanAutomaticDisplayChangeInCE() {
        val scaledPlayButton = prefs.playButtonRegion * (1 / scale.scriptToScreen)

        val displayRegion = locations.ceBomb.displayChangeRegion

        val topLeft = Location(displayRegion.x, displayRegion.y)
        val topRight = Location(displayRegion.right, displayRegion.y)

        val bottomLeft = Location(displayRegion.x, displayRegion.bottom)
        val bottomRight = Location(displayRegion.right, displayRegion.bottom)

        val xRange = scaledPlayButton.x..scaledPlayButton.right
        val yRange = scaledPlayButton.y..scaledPlayButton.bottom

        val displayChangeArea = when {
            topRight.x !in xRange &&
                    topRight.y !in yRange -> CEDisplayChangeAreaEnum.TOP_RIGHT

            topLeft.x !in xRange &&
                    topLeft.y !in yRange -> CEDisplayChangeAreaEnum.TOP_LEFT

            bottomLeft.x !in xRange &&
                    bottomLeft.y !in yRange -> CEDisplayChangeAreaEnum.BOTTOM_LEFT

            bottomRight.x !in xRange &&
                    bottomRight.y !in yRange -> CEDisplayChangeAreaEnum.BOTTOM_RIGHT

            else -> CEDisplayChangeAreaEnum.NONE
        }

        prefs.craftEssence.updateCeDisplayChangeArea(displayChangeArea)

        prefs.craftEssence.canShowAutomaticDisplayChange = displayChangeArea != CEDisplayChangeAreaEnum.NONE
    }

    val playButton = prefs.playButtonRegion

    val isPlayButtonInGoodXLocation = playButton.location.x in
            0..locations.scriptAreaRaw.width / 2

    val isPlayButtonInGoodYLocation = playButton.location.y in
            locations.scriptAreaRaw.height * 5 / 8..locations.scriptAreaRaw.height

}