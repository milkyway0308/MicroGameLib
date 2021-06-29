package skywolf46.microgamelib.api.abstraction.event

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import skywolf46.microgamelib.data.GameInstanceObject

abstract class AbstractPlayerGameEvent(val game: GameInstanceObject, player: Player) : PlayerEvent(player)
