package skywolf46.microgamelib.api.data

import org.bukkit.entity.Player
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.inject.impl.GameParty
import java.lang.IllegalStateException

class GameTeamManager {
    private val playerMap = mutableMapOf<Player, GameTeam>()
    private val teamList = mutableListOf<GameTeam>()

    @Inject
    private lateinit var party: GameParty

    val teams
        get() = teamList.toList()

    fun divide(label: List<String>) {
        // Cache for performance
        val players = party.getPlayers()
        if (players.size < label.size) {
            throw IllegalStateException("Cannot divide team : The minimum number of teams is greater than the current player.")
        }
        players.chunked(players.size / label.size).apply {
            forEach { players ->
                GameTeam(this@GameTeamManager).apply {
                    teamList += this
                    for (pl in players) {
                        playerMap[pl] = this
                        this.teamPlayers.add(pl)
                    }
                }
            }
        }
    }

    fun isBalanced(): Boolean {
        return teamList.associateBy { x -> x.teamPlayers.size }.size == 1
    }


    fun teamOf(player: Player): GameTeam? {
        return playerMap[player]
    }

}