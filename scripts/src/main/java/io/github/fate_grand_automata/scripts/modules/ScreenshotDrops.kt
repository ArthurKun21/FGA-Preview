package io.github.fate_grand_automata.scripts.modules

import io.github.fate_grand_automata.IStorageProvider
import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.lib_automata.Pattern
import io.github.lib_automata.ScreenshotService
import io.github.lib_automata.dagger.ScriptScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class ScreenshotDrops @Inject constructor(
    api: IFgoAutomataApi,
    private val screenshotService: ScreenshotService,
    private val storageProvider: IStorageProvider
) : IFgoAutomataApi by api {
    fun screenshotDrops() {
        if (!prefs.screenshotDrops)
            return

        val drops = mutableListOf<Pattern>()

        for (i in 0..1) {
            useColor {
                if (prefs.screenshotDropsUnmodified) {
                    drops.add(screenshotService.takeScreenshot())
                } else {
                    drops.add(locations.scriptArea.getPattern())
                }
            }

            // check if we need to scroll to see more drops
            if (i == 0 && images[Images.DropScrollbar] in locations.resultDropScrollbarRegion) {
                // scroll to end
                locations.resultDropScrollEndClick.click()
            } else break
        }

        storageProvider.dropScreenshot(drops)
    }

    fun screenshotBond() {
        if (!prefs.screenshotBond) {
            return
        }
        prefs.hidePlayButtonForScreenshot = true
        1.seconds.wait()

        useColor {
            val pattern = screenshotService.takeScreenshot()

            storageProvider.dropBondScreenShot(pattern, server = prefs.gameServer)
        }

        prefs.hidePlayButtonForScreenshot = false
        1.seconds.wait()
    }

    fun screenshotDebug(logName: String = "debug") {
        if (!prefs.platformPrefs.debugMode) {
            return
        }

        useColor {
            val pattern = screenshotService.takeScreenshot()
            val sdf = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.getDefault())
            val timeString = sdf.format(Date())
            val screenRatio = if (locations.isWide) "wide" else "normal"

            val name = "${prefs.gameServer}_${screenRatio}_${logName}_${timeString}.png"

            storageProvider.dump(name, pattern)
        }
    }
}