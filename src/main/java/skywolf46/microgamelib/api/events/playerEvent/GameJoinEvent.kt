package skywolf46.microgamelib.api.events.playerEvent

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import skywolf46.microgamelib.api.abstraction.event.AbstractPlayerGameEvent
import skywolf46.microgamelib.data.GameInstanceObject

class GameJoinEvent(instance: GameInstanceObject, player: Player) : AbstractPlayerGameEvent(instance, player),
    Cancellable {

    var cancel = false
    var cancelledCause: CancelCause? = null
    override fun isCancelled(): Boolean {
        return isCancelledFromPlugin()
    }

    override fun setCancelled(p0: Boolean) {
        if (cancel)
            cancelledCause = CancelCause.CUSTOM
        cancel = p0
    }

    fun isCancelledFromPlugin(): Boolean {
        return cancel && cancelledCause == CancelCause.FRAMEWORk
    }

    fun isCancelledFromFramework(): Boolean {
        return cancel && cancelledCause == CancelCause.FRAMEWORk
    }

    internal fun cancelFramework() {
        cancel = true
        cancelledCause = CancelCause.FRAMEWORk
    }

    override fun getHandlers(): HandlerList {
        return _handle
    }

    companion object {
        internal val _handle = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return _handle
        }
    }

    enum class CancelCause {
        CUSTOM, FRAMEWORk
    }
}