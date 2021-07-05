package skywolf46.microgamelib.api.abstraction

import org.bukkit.entity.Player

interface ISpectate {
    fun setSpectate(player: Player, isSpectate: Boolean)

    fun isSpectated(player: Player): Boolean

    fun getSpectators(): List<Player>

    fun getPlayers(includeSpectator: Boolean = false): List<Player>
}