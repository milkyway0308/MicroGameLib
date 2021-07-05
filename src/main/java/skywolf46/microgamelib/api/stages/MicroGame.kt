package skywolf46.microgamelib.api.stages

import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent
import skywolf46.extrautility.util.callEvent
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.abstraction.IGameResult
import skywolf46.microgamelib.api.abstraction.ISpectate
import skywolf46.microgamelib.api.data.StageWinnerManager
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.api.events.playerEvent.GameJoinEvent
import skywolf46.microgamelib.api.events.playerEvent.PlayerAfterSpectateEvent
import skywolf46.microgamelib.api.events.playerEvent.PlayerSpectateEvent
import skywolf46.microgamelib.enums.QuitCause
import skywolf46.microgamelib.inject.impl.GameParty

abstract class MicroGame {
    @Inject
    lateinit var party: GameParty

    @Inject
    lateinit var winnerManager: StageWinnerManager

    @Inject
    lateinit var instance: GameInstanceObject


    @InGameListener(priority = EventPriority.LOWEST)
    fun GameJoinEvent.filterJoin() {
        cancelFramework()
    }

    @InGameListener(priority = EventPriority.HIGHEST)
    fun PlayerQuitEvent.checkPlayerQuit() {
        party.removePlayer(player, QuitCause.PLAYER_QUIT_SERVER)
    }

    open fun checkWinner(): IGameResult? {
        return null
    }


}