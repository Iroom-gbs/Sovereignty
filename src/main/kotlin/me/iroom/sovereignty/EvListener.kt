package me.iroom.sovereignty

import me.iroom.sovereignty.area.AreaManager.getLocationArea
import me.iroom.sovereignty.area.AreaManager.isCoreBlock
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.world.StructureGrowEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.block.SpongeAbsorbEvent
import org.bukkit.event.block.BlockGrowEvent
import me.iroom.sovereignty.area.AreaManager.isProtectedArea
import me.iroom.sovereignty.area.TeamManager.getTeam
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import java.util.*

class EvListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) { //이벤트 명이랑 함수명이랑 맞추자
        val b = event.block
        val p = event.player
        val a = getLocationArea(b.location)
        //코어 블럭이면
        if(isCoreBlock(b.location)) {
            if(a.reinforced) {
                event.isCancelled = true
            }
            else {
                if(a.vulnerable) {
                    a.vulnerable = false
                    a.team = p.getTeam()!!.name
                }
                else {
                    a.reinforced = true
                    val cal = Calendar.getInstance()
                    //TODO: 강화 끝나는 시간 설정
                }
            }
        }
        //보호구역 내의 블럭이고 (코어 포함) 서바이벌 플레이어가 부쉈다면 취소
        if (isProtectedArea(b.location) && p.gameMode == GameMode.SURVIVAL) event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val b = event.blockPlaced
        val p = event.player
        //보호구역내에 서바이벌 플레이어가 설치했다면 취소
        if (isProtectedArea(b.location) && p.gameMode == GameMode.SURVIVAL) event.isCancelled = true
        //구역이 자기 팀이 아니면 취소
        if(!(p.getTeam() != null && getLocationArea(b.location).team == p.getTeam()!!.name)) event.isCancelled = true
    }

    @EventHandler
    fun onBlockGrow(event: BlockGrowEvent) {
        if(isProtectedArea(event.block.location)) event.isCancelled = true
    }

    @EventHandler
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) { //이번에는 피스톤도 막는다
        run {
            event.blocks.forEach {
                if (isProtectedArea(it.location.add(event.direction.direction.toLocation(it.location.world!!)))) {
                    event.isCancelled = true
                    return@run
                }
            }
        }
    }

    @EventHandler
    fun onSpongeAbsorb(event: SpongeAbsorbEvent) {
        event.blocks.forEach {
            if(isProtectedArea(it.location))
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockFromTo(event: BlockFromToEvent) {
        val loc = event.toBlock.location
        if (isProtectedArea(loc)) event.isCancelled = true
    }

    @EventHandler
    fun onBlockPistonRetract(event: BlockPistonRetractEvent) {
        run {
            event.blocks.forEach {
                if (isProtectedArea(it.location)) {
                    event.isCancelled = true
                    return@run
                }
            }
        }
    }

    @EventHandler
    fun onPlayerBucketFill(event: PlayerBucketFillEvent) {
        if(isProtectedArea(event.block.location) && event.player.gameMode == GameMode.SURVIVAL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        if(isProtectedArea(event.block.location) && event.player.gameMode == GameMode.SURVIVAL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.blockList().forEach {
            if (isProtectedArea(it.location)) {
                event.blockList().remove(it)
            }
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().forEach {
            if (isProtectedArea(it.location)) {
                event.blockList().remove(it)
            }
        }
    }


    @EventHandler
    fun onStructureGrow(event: StructureGrowEvent) {
        event.blocks.forEach {
            if (isProtectedArea(it.location)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerBedEnter(event: PlayerBedEnterEvent) {
        //TODO:다른팀 땅에서 못 자게 하기
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {

        val p: Player = event.player

        //200 (라인 높이) 보다 높이 올라가면 즉사
        if (p.location.y >= 200 && p.gameMode == GameMode.SURVIVAL)
            p.health = 0.0

        val prevArea = getLocationArea(event.from).areaID
        val afterArea = getLocationArea(event.to!!).areaID
        if (prevArea != afterArea)
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${afterArea}번 구역에 진입했습니다."))
    }
}