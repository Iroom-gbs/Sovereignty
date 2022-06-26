
package me.iroom.sovereignty.area

import me.ddayo.coroutine.Coroutine
import me.ddayo.coroutine.functions.WaitNextTick
import me.ddayo.coroutine.functions.WaitSeconds
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Color
import java.util.*
import kotlin.collections.HashMap


object AreaManager {
    var Areas: Array<Area> = Array(25) {
        Area(it+1, Location(Bukkit.getServer().getWorld("world"),0.0, 0.0, 0.0), "TEST")
    }


    var LevelPoint = HashMap<Color, Int>() //이거 머여?
    val coreProtectArea = 5;

    init {
        //Main update
        Coroutine.startCoroutine(sequence{
            while(true) {
                Bukkit.getOnlinePlayers().forEach {
                    getLocationArea(it.location).giveEffect(it)
                }

                Areas.forEach {
                    if(it.reinforced && Calendar.getInstance() >= it.reinforceEndTime) {
                        it.reinforced = false
                        it.vulnerable = true
                        val temp = Calendar.getInstance()
                        temp.add(Calendar.MINUTE, 30)
                        it.vulnerableEndTime = temp
                    }

                    if(it.vulnerable && Calendar.getInstance() >= it.vulnerableEndTime) {
                        it.vulnerable = false
                    }
                }
                yield(WaitSeconds(1.0))
            }
        })
    }

    fun getLocationArea(loc:Location): Area { //특정 지점의 구역 얻기
        var x = loc.x.toInt()
        var z = loc.z.toInt()
        x = (x + 500) / 200 //0 0 기준으로 내리기
        z = (z + 500) / 200
        return Areas[x * 5 + z]
    }

    fun isProtectedArea(loc: Location): Boolean { //코어 주변인지 확인
        val cLoc = getLocationArea(loc).coreLoc
        return if (loc.x > cLoc.x+ coreProtectArea || loc.x < cLoc.x - coreProtectArea) false
        else !(loc.z > cLoc.z + coreProtectArea || loc.z < cLoc.z - coreProtectArea)
    }

    fun isCoreBlock(loc: Location): Boolean { //코어 블럭인지 확인
        val cLoc = getLocationArea(loc).coreLoc
        return (cLoc == loc)
    }
}
