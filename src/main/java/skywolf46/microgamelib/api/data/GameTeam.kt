package skywolf46.microgamelib.api.data

import org.bukkit.entity.Player

class GameTeam(val teamName: String, private val manager: GameTeamManager) {
    internal val teamPlayers = mutableListOf<Player>()

    val players
        get() = ArrayList(teamPlayers)

}