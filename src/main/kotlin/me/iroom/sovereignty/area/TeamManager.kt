package me.iroom.sovereignty.area

import org.bukkit.Bukkit
import org.bukkit.entity.Player


object TeamManager {

    fun Player.registerTeam(team: String) {
        val t = this.scoreboard.getTeam(team) ?: this.scoreboard.registerNewTeam(team)
        t.addEntry(this.uniqueId.toString())
    }

    fun String.getTeam() = Bukkit.getScoreboardManager()?.mainScoreboard?.getTeam(this)

    fun Player.getTeam() = this.scoreboard.getEntryTeam(uniqueId.toString())
}


