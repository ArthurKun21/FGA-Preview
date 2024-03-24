package io.github.fate_grand_automata.scripts.locations

import io.github.fate_grand_automata.scripts.enums.GameServer
import io.github.fate_grand_automata.scripts.models.CommandCard
import io.github.lib_automata.Location
import io.github.lib_automata.Region
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class AttackScreenLocations @Inject constructor(
    scriptAreaTransforms: IScriptAreaTransforms
) : IScriptAreaTransforms by scriptAreaTransforms {
    private fun clickLocation(card: CommandCard.Face) = when (card) {
        CommandCard.Face.A -> -980
        CommandCard.Face.B -> -530
        CommandCard.Face.C -> 20
        CommandCard.Face.D -> 520
        CommandCard.Face.E -> 1070
    }.let { x -> Location(x, 1000) }

    fun clickLocation(card: CommandCard) = when (card) {
        is CommandCard.Face -> clickLocation(card)
        CommandCard.NP.A -> Location(-280, 220)
        CommandCard.NP.B -> Location(20, 400)
        CommandCard.NP.C -> Location(460, 400)
    }.xFromCenter()

    private val faceCardDeltaY =
        Location(0, if (gameServer == GameServer.Cn && isWide) -42 else 0)

    fun affinityRegion(card: CommandCard.Face) = when (card) {
        CommandCard.Face.A -> -985
        CommandCard.Face.B -> -470
        CommandCard.Face.C -> 41
        CommandCard.Face.D -> 554
        CommandCard.Face.E -> 1068
    }.let { x -> Region(x, 590, 250, 260) + faceCardDeltaY }.xFromCenter()

    fun typeRegion(card: CommandCard.Face) = when (card) {
        CommandCard.Face.A -> -1280
        CommandCard.Face.B -> -768
        CommandCard.Face.C -> -256
        CommandCard.Face.D -> 256
        CommandCard.Face.E -> 768
    }.let { x -> Region(x, 1060, 512, 200) + faceCardDeltaY }.xFromCenter()

    fun typeNPRegion(np: CommandCard.NP) = when (np) {
        CommandCard.NP.A -> -768
        CommandCard.NP.B -> -256
        CommandCard.NP.C -> 256
    }.let { x -> Region(x, 475, 512, 200) + faceCardDeltaY }.xFromCenter()

    fun servantMatchRegion(card: CommandCard.Face) = when (card) {
        CommandCard.Face.A -> -1174
        CommandCard.Face.B -> -660
        CommandCard.Face.C -> -150
        CommandCard.Face.D -> 364
        CommandCard.Face.E -> 880
    }.let { x -> Region(x - 100, 700, 500, 400) + faceCardDeltaY }.xFromCenter()

    fun servantNPMatchRegion(np: CommandCard.NP) = when (np) {
        CommandCard.NP.A -> -660
        CommandCard.NP.B -> -150
        CommandCard.NP.C -> 364
    }.let { x -> Region(x - 100, 115, 500, 400) + faceCardDeltaY }.xFromCenter()

    fun supportNPCheckRegion(np: CommandCard.NP) = when (np) {
        CommandCard.NP.A -> -470
        CommandCard.NP.B -> 41
        CommandCard.NP.C -> 554
    }.let { x ->
        Region(x, 5, 250, 260) + faceCardDeltaY
    }.xFromCenter() +
            Location(-50, 100)

    fun supportCheckRegion(card: CommandCard.Face) =
        affinityRegion(card) + Location(-50, 100)


    val backClick = when (isWide) {
        true -> Location(-325, 1310)
        false -> Location(-160, 1370)
    }.xFromRight()
}