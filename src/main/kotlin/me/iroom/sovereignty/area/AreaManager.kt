
package me.iroom.sovereignty.area

import me.ddayo.coroutine.Coroutine
import me.ddayo.coroutine.functions.WaitSeconds
import me.iroom.sovereignty.area.Area.Companion.getArea
import me.iroom.sovereignty.util.Option
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File
import java.util.*
import kotlin.collections.HashMap


object AreaManager {
    val Areas = emptyList<Area>().toMutableList()
    init {
        for(x in 1..25)
            Areas.add(Area(x, Location(Bukkit.getServer().getWorld("world"),0.0, 0.0, 0.0), "TEST"))
    }

    var LevelPoint = HashMap<String, Int>() //이거 머여? 팀별 총 포인트로 쓰려던거
    val coreProtectArea = 5; //코어 보호 범위

    init {
        //Main update
        Coroutine.startCoroutine(sequence{
            while(true) {
                Bukkit.getOnlinePlayers().forEach {
                    if(it.location.world == Bukkit.getWorld("world")) {
                        getLocationArea(it.location).giveEffect(it)
                    }
                }

                Areas.forEach { //시간 보고 강화/취약 풀기
                    it.updateHpBar()
                    if(it.reinforced && Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")) >= it.reinforceEndTime) {
                        it.vulnerateArea()
                        val temp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                        temp.add(Calendar.MINUTE, 20)
                        it.vulnerableEndTime = temp
                    }

                    if(it.vulnerable && Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")) >= it.vulnerableEndTime) {
                        it.normalizeArea()
                    }
                }

                yield(WaitSeconds(1.0))
            }
        })

        Coroutine.startCoroutine(sequence {
            while(true) {
                //30초당 한칸씩 코어 회복, 없는 상태에서 풀회복까지 취약상태에서 10분, 통상 상태에서 25분 소요
                Areas.forEach {
                    if(it.coreHp < it.maxCoreHp)
                        it.coreHp += 1
                }
                yield(WaitSeconds(30.0))
            }
        })
    }

    fun getLocationArea(loc:Location): Area { //특정 지점의 구역 얻기
        var x = loc.x.toInt()
        var z = loc.z.toInt()

        if (x >= 500) x = 499
        if (x <= -500) x = -499
        if (z >= 500) z = 499
        if (z <= -500) z = -499

        x = (x + 500) / 200 //0 0 기준으로 내리기
        z = (z + 500) / 200
        return Areas[x + z * 5]
    }

    fun isProtectedArea(loc: Location): Boolean { //코어 주변인지 확인
        val cLoc = getLocationArea(loc).coreLoc
        if (loc.world != Bukkit.getWorld("world")) return false

        return if (loc.x > cLoc.x+ coreProtectArea || loc.x < cLoc.x - coreProtectArea) false
        else !(loc.z > cLoc.z + coreProtectArea || loc.z < cLoc.z - coreProtectArea)
    }

    fun isCoreBlock(loc: Location): Boolean { //코어 블럭인지 확인
        val cLoc = getLocationArea(loc).coreLoc
        if (loc.world != Bukkit.getWorld("world")) return false

        return (cLoc == loc)
    }

    val serialize get() = Option("Areas", "").apply {
            for(x in Areas)
                append(x.serialize)
        }

    fun load(opt: Option) {
        Areas.clear()
        Areas.addAll(opt.subOptions.first().subOptions.sortedBy { it["areaID"].first().value.toInt() }.map { it.getArea() })
    }

    fun save() {
        File("areas.dat").writeText(serialize.str())
    }

    init {
        Coroutine.startCoroutine(sequence {
            while(true) {
                save()
                yield(WaitSeconds(300.0))
            }
        })
    }
}
