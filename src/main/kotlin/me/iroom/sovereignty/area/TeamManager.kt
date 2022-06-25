package me.iroom.sovereignty.area

import org.bukkit.Bukkit
import org.bukkit.entity.Player


object TeamManager {
    val team = Bukkit.getScoreboardManager()?.mainScoreboard?.teams?.map { Team(it.name) }!!.toMutableSet()

    init {
        for(x in team.withIndex())
            x.value.id = x.index
    }

    fun Player.registerTeam(team: String) {
        val t = this.scoreboard.getTeam(team) ?: this.scoreboard.registerNewTeam(team)
        t.addEntry(this.uniqueId.toString())
        TeamManager.team.add(Team(team))
    }

    fun Player.getTeam() = this.scoreboard.getEntryTeam(uniqueId.toString())

    data class Team(public val name: String) {
        var id: Int = -1
            set(value) {
                if(field != -1) throw IllegalAccessException("Not allowed")
                field = value
            }

        public val team get() = Bukkit.getScoreboardManager()?.mainScoreboard!!.getTeam(name)!!
        val color get() = team.color
    }
}


