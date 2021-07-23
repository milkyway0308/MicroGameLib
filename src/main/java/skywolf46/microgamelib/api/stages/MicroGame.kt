package skywolf46.microgamelib.api.stages

import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.ifTrue
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.abstraction.AbstractSystemBase
import skywolf46.microgamelib.api.abstraction.IAttachableSystem
import skywolf46.microgamelib.api.abstraction.IGameResult
import skywolf46.microgamelib.api.abstraction.ISpectate
import skywolf46.microgamelib.api.data.StageWinnerManager
import skywolf46.microgamelib.api.events.gameEvent.StageChangedEvent
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.api.events.playerEvent.GameJoinEvent
import skywolf46.microgamelib.api.events.playerEvent.PlayerAfterSpectateEvent
import skywolf46.microgamelib.api.events.playerEvent.PlayerSpectateEvent
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.enums.QuitCause
import skywolf46.microgamelib.inject.impl.GameParty
import skywolf46.microgamelib.inject.util.ConditionScheduler
import skywolf46.microgamelib.inject.util.DynamicEventHandle
import skywolf46.microgamelib.storage.InjectReference
import skywolf46.microgamelib.storage.InjectorClassManager

abstract class MicroGame : AbstractSystemBase() {
    @Inject
    lateinit var party: GameParty

    @Inject
    lateinit var winnerManager: StageWinnerManager

    @Inject
    lateinit var instance: GameInstanceObject

    @Inject
    lateinit var injector: InjectReference

    @Inject
    lateinit var scheduler: ConditionScheduler


    @Inject
    lateinit var dynamicEventHandle: DynamicEventHandle

    private val systemList = mutableListOf<IAttachableSystem>()

    @InGameListener(priority = EventPriority.LOWEST)
    fun GameJoinEvent.filterJoin() {
        cancelFramework()
    }

    @InGameListener(priority = EventPriority.HIGHEST)
    fun PlayerQuitEvent.checkPlayerQuit() {
        party.removePlayer(player, QuitCause.PLAYER_QUIT_SERVER)
    }

    @InGameListener(priority = EventPriority.HIGHEST)
    fun StageChangedEvent.onEvent() {
        scheduler.endScope(InjectScope.STAGE)
        // Uninstall all
        uninstallSystem(IAttachableSystem::class.java)
    }

    open fun checkWinner(): IGameResult? {
        return null
    }

    fun installSystem(system: IAttachableSystem) {
        systemList += system
        injector.injectTo(system)
        dynamicEventHandle.bindListenerTo(InjectScope.STAGE, system)
        system.onAttach()
    }

    fun uninstallSystem(system: Class<out IAttachableSystem>) {
        systemList.removeIf {
            return@removeIf system.isAssignableFrom(it.javaClass).ifTrue {
                it.onDetach()
            }
        }
    }


}