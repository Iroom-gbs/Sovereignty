package me.iroom.sovereignty.area

import org.bukkit.*
import java.util.*
import org.bukkit.entity.Player


class Area(areaID : Int, coreLoc : Location, team: String){
    val areaID : Int //구역 ID <- 내부적으로는 0부터 시작되는 배열로 처리하고, 이거는 표시되는 ID

    var coreLoc : Location //코어의 좌표
    var team : String //Team ID
    var reinforced : Boolean = false //강화(무적 시간) 여부
    var reinforceEndTime = Calendar.getInstance() // 일단 현재시간
    var vulnerable : Boolean = false //강화 풀린 후 취약 여부

    var level = 2 //현재레벨
        get private set

    companion object {
        const val maxLevel = 6
    }

    init {
        this.areaID = areaID
        this.coreLoc = coreLoc
        this.team = team
    }

    fun levelUp() {
        if(level == maxLevel) return
        if(reinforced) return
        level++
    }

    fun levelDown() {
        if(level ==0) return
        if(reinforced) return
        level--
    }

    fun giveEffect(player: Player) {

    }
}