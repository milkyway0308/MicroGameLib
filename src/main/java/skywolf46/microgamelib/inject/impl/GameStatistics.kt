package skywolf46.microgamelib.inject.impl

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChatEvent
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope

@InjectTarget(InjectScope.GLOBAL)
class GameStatistics : HashMap<String, Int>() {

}