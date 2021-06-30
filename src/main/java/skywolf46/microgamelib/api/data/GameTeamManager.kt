package skywolf46.microgamelib.api.data

import org.bukkit.entity.Player
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.inject.impl.GameParty
import java.lang.IllegalStateException

// TODO convert to internal inject
class GameTeamManager(val party: GameParty) {
    private val playerMap = mutableMapOf<Player, GameTeam>()
    private val teamList = mutableListOf<GameTeam>()
    private val labeledTeamMap = mutableMapOf<String, GameTeam>()


    val teams
        get() = teamList.toList()

    fun addTeam(team: GameTeam): GameTeam {
        teamList += team
        labeledTeamMap[team.teamName] = team
        return team
    }

    fun getOrAddTeam(name: String, team: () -> GameTeam): GameTeam {
        return labeledTeamMap[name] ?: addTeam(team())
    }

    fun divide(label: List<String>) {
        // Cache for performance
        val players = party.getPlayers()
        println("Players: ${players}")
        if (players.size < label.size) {
            throw IllegalStateException("Cannot divide team : The minimum number of teams is greater than the current player.")
        }
        val iterator = label.iterator()
        players.chunked(players.size / label.size).apply {
            forEach { players ->
                GameTeam(iterator.next(), this@GameTeamManager).apply {
                    teamList += this
                    for (pl in players) {
                        playerMap[pl] = this
                        this.teamPlayers.add(pl)
                    }
                }.apply {
                    labeledTeamMap[teamName] = this
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

    fun teamOf(name: String): GameTeam? {
        return labeledTeamMap[name]
    }

    fun cleanUp() {
        labeledTeamMap.clear()
        teamList.clear()
        playerMap.clear()
    }

}