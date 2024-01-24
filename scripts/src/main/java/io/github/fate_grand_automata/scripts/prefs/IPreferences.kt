package io.github.fate_grand_automata.scripts.prefs

import io.github.fate_grand_automata.scripts.enums.BattleConfigListSortEnum
import io.github.fate_grand_automata.scripts.enums.GameServer
import io.github.fate_grand_automata.scripts.enums.ScriptModeEnum
import io.github.lib_automata.Location
import io.github.lib_automata.PlatformPrefs
import io.github.lib_automata.Region
import kotlin.time.Duration

interface IPreferences {
    var scriptMode: ScriptModeEnum
    var gameServer: GameServer
    val battleConfigs: List<IBattleConfig>
    var showGameServers: List<GameServer>
    var selectedServerConfigPref: IPerServerConfigPrefs
    var selectedBattleConfig: IBattleConfig
    val storySkip: Boolean
    val withdrawEnabled: Boolean
    val stopOnCEGet: Boolean
    val stopOnFirstClearRewards: Boolean
    val boostItemSelectionMode: Int

    val useRootForScreenshots: Boolean
    val recordScreen: Boolean
    val skillDelay: Duration
    val screenshotDrops: Boolean
    val screenshotDropsUnmodified: Boolean
    val screenshotBond : Boolean
    var hidePlayButtonForScreenshot: Boolean
    var maxGoldEmberStackSize: Int
    var maxGoldEmberTotalCount: Int
    var stopAfterThisRun: Boolean
    val skipServantFaceCardCheck: Boolean
    val treatSupportLikeOwnServant: Boolean

    var receiveEmbersWhenGiftBoxFull: Boolean
    var loopIntoLotteryAfterPresentBox: Boolean
    var isPresentBoxFull: Boolean

    val lottoSpin: Int

    val stageCounterSimilarity: Double
    val stageCounterNew: Boolean
    val waitBeforeTurn: Duration
    val waitBeforeCards: Duration

    val support: ISupportPreferencesCommon
    val platformPrefs: PlatformPrefs
    val gestures: IGesturesPreferences

    fun getPerServerConfigPref(server: GameServer): IPerServerConfigPrefs

    fun addPerServerConfigPref(server: GameServer): IPerServerConfigPrefs

    fun forBattleConfig(id: String): IBattleConfig
    fun addBattleConfig(id: String): IBattleConfig
    fun removeBattleConfig(id: String)
    fun isOnboardingRequired(): Boolean
    fun completedOnboarding()

    val skill: ISkillPreferences

    val append: IAppendPreferences

    val servant: IServantEnhancementPreferences

    val craftEssence: ICraftEssencePreferences

    val friendGacha: IFriendGachaPreferences

    val playButtonLocation: Location

    val playButtonRegion: Region

    val ignorePlayButtonDetectionWarning: Boolean

    var notifyErrorWarning: String

    var configListSort: BattleConfigListSortEnum

    var emptyEnhance: Boolean
}

val IPreferences.wantsMediaProjectionToken get() = !useRootForScreenshots