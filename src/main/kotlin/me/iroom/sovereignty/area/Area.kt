package me.iroom.sovereignty.area

import me.iroom.sovereignty.area.TeamManager.getTeam
import org.bukkit.*
import java.util.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class Area(areaID : Int, coreLoc : Location, team: String){
    val areaID : Int //구역 ID <- 내부적으로는 0부터 시작되는 배열로 처리하고, 이거는 표시되는 ID

    var coreLoc : Location //코어의 좌표
    var team : String //Team ID
    var reinforced : Boolean = false //강화(무적 시간) 여부
    var reinforceEndTime = Calendar.getInstance() //강화 풀리는 시간
    var vulnerable : Boolean = false //강화 풀린 후 취약 여부
    var vulnerableEndTime = Calendar.getInstance() //취약 풀리는 시간

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

    fun giveEffect(p: Player) {
        if(p.getTeam() != null && p.getTeam()?.name  == team) {
            if(1<=level)
                p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 1, 1))
            if(2<=level) {
                p.addPotionEffect(PotionEffect(PotionEffectType.LUCK, 1, 1))
                p.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 1, 1))
                p.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1, 1))
            }
            if(3<=level) {
                p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1, 1))
                p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 1, 1))
            }
            if(4<=level)
                p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1, 2))
        }
        else {
            p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 1, 2))
            if(4<=level)
                p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 1, 1))
            if(5<=level) {
                p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1, 1))
                p.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 1, 1))
            }
            if(6<=level) {
                p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 1, 3))
            }
        }
    }
}