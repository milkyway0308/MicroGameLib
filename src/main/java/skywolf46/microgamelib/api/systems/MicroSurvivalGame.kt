package skywolf46.microgamelib.api.systems

import org.bukkit.GameMode
import org.bukkit.event.EventPriority
import skywolf46.extrautility.events.combat.PlayerPreDeathEvent
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.api.abstraction.AbstractAttachableSystem
import skywolf46.microgamelib.api.abstraction.IGameResult
import skywolf46.microgamelib.api.data.results.MultipleWinnerResult
import skywolf46.microgamelib.api.data.results.SingleWinnerResult
import skywolf46.microgamelib.api.events.gameEvent.StageChangedEvent
import skywolf46.microgamelib.api.events.playerEvent.PlayerAfterSpectateEvent
import skywolf46.microgamelib.api.stages.MicroGame

abstract class MicroSurvivalGame : AbstractAttachableSystem() {

    @InGameListener
    fun PlayerPreDeathEvent.onGame() {
        isCancelled = true
        player.health = player.maxHealth
        party.setSpectate(player, true)
    }

    @InGameListener(priority = EventPriority.HIGHEST)
    fun PlayerAfterSpectateEvent.onSpectate() {
        if (isSpectating) {
            player.gameMode = GameMode.SPECTATOR
            if (party.getPlayers(false).size <= 1) {
                instance.nextStage()
            }
        } else if (player.gameMode == GameMode.SPECTATOR)
            player.gameMode = GameMode.SURVIVAL
    }

    @InGameListener(priority = EventPriority.HIGHEST)
    fun StageChangedEvent.onEvent() {
        winnerManager.addResult(checkWinner()!!)
    }

    override fun checkWinner(): IGameResult? {
        with(party.getPlayers(false)) {
            return when (size) {
                1 -> SingleWinnerResult(this[0])
                else -> MultipleWinnerResult(this.toMutableList())
            }
        }

    }

    override fun hasWinner(): Boolean {
        return true
    }
}