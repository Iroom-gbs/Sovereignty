package me.iroom.sovereignty.team

import me.iroom.sovereignty.area.AreaManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team


object TeamManager {

    fun Player.registerTeam(team: String) {
        val t = this.scoreboard.getTeam(team) ?: this.scoreboard.registerNewTeam(team)
        t.addEntry(this.uniqueId.toString())
    }

    fun String.getTeam() = Bukkit.getScoreboardManager()?.mainScoreboard?.getTeam(this)

    fun Player.getTeam() = this.scoreboard.getEntryTeam(uniqueId.toString())

    val Team.getTotalPoint
        get() = AreaManager.Areas.count { it.team == this.name } * 2

    val Team.getUsedPoint
        get() = AreaManager.Areas.sumOf { it.level }

    val Team.getLeftPoint
        get() = AreaManager.Areas.sumOf { 2 - it.level }
}


