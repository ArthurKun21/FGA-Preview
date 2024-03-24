package io.github.fate_grand_automata.scripts.modules

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.entrypoints.AutoBattle
import io.github.fate_grand_automata.scripts.models.NPUsage
import io.github.fate_grand_automata.scripts.models.ParsedCard
import io.github.fate_grand_automata.scripts.models.Skill
import io.github.fate_grand_automata.scripts.models.battle.BattleState
import io.github.fate_grand_automata.scripts.prefs.IBattleConfig
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class Battle @Inject constructor(
    api: IFgoAutomataApi,
    private val servantTracker: ServantTracker,
    private val state: BattleState,
    private val battleConfig: IBattleConfig,
    private val autoSkill: AutoSkill,
    private val caster: Caster,
    private val card: Card,
    private val skillSpam: SkillSpam,
    private val shuffleChecker: ShuffleChecker,
    private val stageTracker: StageTracker,
    private val autoChooseTarget: AutoChooseTarget
) : IFgoAutomataApi by api {
    init {
        prefs.stopAfterThisRun = false
        state.markStartTime()

        resetState()
    }

    var isReturningToMenu = false

    fun resetState(repeatQuest: Boolean = false) {
        // Don't increment no. of runs if we're just clicking on quest again and again
        // This can happen due to lags introduced during some events
        if (state.stage != -1 && !isReturningToMenu) {
            state.nextRun()

            servantTracker.nextRun()
        }
        val selectedServerConfigPref = prefs.selectedServerConfigPref

        if (prefs.stopAfterThisRun) {
            if (repeatQuest && !isReturningToMenu && selectedServerConfigPref.returnToMenu) {
                isReturningToMenu = true
                locations.cancelQuestRegion.click()
            } else {
                prefs.stopAfterThisRun = false
                isReturningToMenu = false
                throw AutoBattle.BattleExitException(AutoBattle.ExitReason.StopAfterThisRun)
            }
        }

        if (selectedServerConfigPref.shouldLimitRuns && state.runs >= selectedServerConfigPref.limitRuns) {
            if (repeatQuest && !isReturningToMenu && selectedServerConfigPref.returnToMenu) {
                isReturningToMenu = true
                locations.cancelQuestRegion.click()
            } else {
                isReturningToMenu = false
                throw AutoBattle.BattleExitException(AutoBattle.ExitReason.LimitRuns(state.runs))
            }
        }
    }

    fun isIdle() = images[Images.BattleScreen] in locations.battle.screenCheckRegion

    fun clickAttack(): List<ParsedCard> {
        locations.battle.attackClick.click()

        // Wait for Attack button to disappear
        locations.battle.screenCheckRegion.waitVanish(images[Images.BattleScreen], 5.seconds)

        prefs.waitBeforeCards.wait()

        return card.readCommandCards()
    }

    /**
     * Perform a battle for the specific turn/wave
     *
     * For Skills actions
     * @see AutoSkill.execute
     *
     * For Card reading
     * @see shuffleCards
     * @see clickAttack
     * @see Card.readCommandCards
     * @see CardParser.parse
     *
     * For Card and NP Clicking
     * @see Card.clickCommandCards
     */
    fun performBattle() {
        prefs.waitBeforeTurn.wait()

        if (battleConfig.addRaidTurnDelay){
            battleConfig.raidTurnDelaySeconds.seconds.wait()

            // snap another screenshot for the raid
            isIdle()
        }

        onTurnStarted()

        servantTracker.beginTurn()

        val npUsage = autoSkill.execute(state.stage, state.turn)

        // For Deprecation, going to spam skills once ran out of command list
        skillSpam.spamSkills()

        val cards = clickAttack()
            .takeUnless { shouldShuffle(it, npUsage) }
            ?: shuffleCards()

        val nps = card.readNpCards(npUsage = npUsage)

        card.clickCommandCards(cards, npUsage)

        0.5.seconds.wait()
    }

    private fun shouldShuffle(cards: List<ParsedCard>, npUsage: NPUsage): Boolean {
        // Not this wave
        if (state.stage != (battleConfig.shuffleCardsWave - 1)) {
            return false
        }

        // Already shuffled
        if (state.shuffled) {
            return false
        }

        return shuffleChecker.shouldShuffle(
            mode = battleConfig.shuffleCards,
            cards = cards,
            npUsage = npUsage
        )
    }

    private fun shuffleCards(): List<ParsedCard> {
        locations.attack.backClick.click()

        caster.castMasterSkill(Skill.Master.S3)
        state.shuffled = true

        return clickAttack()
    }

    private fun onTurnStarted() = useSameSnapIn {
        stageTracker.checkCurrentStage()

        state.nextTurn()

        if (battleConfig.autoChooseTarget) {
            autoChooseTarget.choose()
        }
    }
}
