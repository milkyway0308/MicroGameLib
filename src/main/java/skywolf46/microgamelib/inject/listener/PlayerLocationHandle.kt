package skywolf46.microgamelib.inject.listener

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import skywolf46.extrautility.events.interaction.PlayerLeftClickAtBlockEvent
import skywolf46.extrautility.events.interaction.PlayerRightClickAtBlockEvent
import skywolf46.extrautility.events.interaction.PlayerRightClickEvent
import skywolf46.extrautility.util.getOrSetValue
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope

@InjectTarget(InjectScope.GLOBAL)
class PlayerLocationHandle {
    @InGameListener
    fun PlayerLeftClickAtBlockEvent.onListen() {
        if (player.isOp || player.hasPermission("mglib.admin")) {
            player.itemInHand?.apply {
                if (type == Material.GOLD_AXE) {
                    isCancelled = true
                    player.sendMessage("§6MicroGameLib §7| §e지점 1 설정됨!")
                    player.getOrSetValue("[MGLib] Locs") {
                        Array<Location?>(2) { null }
                    }[0] = targetBlock.location
                }
            }
        }
    }

    @InGameListener
    fun PlayerRightClickAtBlockEvent.onListen() {
        if (isOffHanded)
            return
        if (player.isOp || player.hasPermission("mglib.admin")) {
            player.itemInHand?.apply {
                if (type == Material.GOLD_AXE) {
                    isCancelled = true
                    player.sendMessage("§6MicroGameLib §7| §e지점 2 설정됨!")
                    player.getOrSetValue("[MGLib] Locs") {
                        Array<Location?>(2) { null }
                    }[1] = targetBlock.location
                }
            }
        }
    }
}