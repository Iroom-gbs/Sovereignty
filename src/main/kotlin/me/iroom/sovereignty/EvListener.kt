package me.iroom.sovereignty

import me.iroom.sovereignty.area.AreaManager
import me.iroom.sovereignty.area.AreaManager.getLocationArea
import me.iroom.sovereignty.area.AreaManager.isCoreBlock
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.world.StructureGrowEvent
import me.iroom.sovereignty.area.AreaManager.isProtectedArea
import me.iroom.sovereignty.team.TeamManager.getTeam
import me.iroom.sovereignty.gui.AreaGUI.showAreaGUI
import me.iroom.sovereignty.gui.LevelUpDownGUI
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Scaffolding
import org.bukkit.event.block.*
import org.bukkit.event.player.*

class EvListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) { //이벤트 명이랑 함수명이랑 맞추자
        val b = event.block
        val p = event.player
        val a = getLocationArea(b.location)

        if(isCoreBlock(b.location)) {
            if(a.coreHp >= 0) {
                if(p.getTeam() != null) {
                    if(p.getTeam()!!.name != a.team) {
                        Bukkit.getOnlinePlayers().forEach {
                            if(it.getTeam()!=null) {
                                if(it.getTeam()!!.name == a.team) {
                                    it.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}${a.areaID}번 구역이 공격받고 있습니다!"))
                                }
                            }
                        }
                        a.coreHp -= 1
                    }
                }
                else {
                    a.coreHp -= 1
                    if(p.getTeam()!!.name != a.team) {
                        Bukkit.getOnlinePlayers().forEach {
                            if (it.getTeam() != null) {
                                if (it.getTeam()!!.name == a.team) {
                                    it.spigot().sendMessage(
                                        ChatMessageType.ACTION_BAR,
                                        TextComponent("${ChatColor.RED}${a.areaID}번 구역이 공격받고 있습니다!")
                                    )
                                }
                            }
                        }
                        a.coreHp -= 1
                    }
                }
            }

            if(a.coreHp <= 0) a.coreBreak(event.player)


            event.isCancelled = true
        }

        if (isProtectedArea(b.location) && p.gameMode == GameMode.SURVIVAL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteractItem(event: PlayerInteractEvent) {
        if(event.action == Action.RIGHT_CLICK_BLOCK)
            AreaManager.Areas.firstOrNull { it.coreLoc == event.clickedBlock!!.location }?.let {
                event.player.openInventory(LevelUpDownGUI(it.areaID).inventory)
                event.isCancelled = true
            }

        if(event.player.inventory.itemInMainHand.type == Material.COMPASS) {
            showAreaGUI(event.player)
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val b = event.blockPlaced
        val p = event.player
        //보호구역내에 서바이벌 플레이어가 설치했다면 취소
        if (isProtectedArea(b.location) && p.gameMode == GameMode.SURVIVAL) event.isCancelled = true
        //구역이 자기 팀이 아니고 비계가 아니면 취소
        if(!(p.getTeam() != null && getLocationArea(b.location).team == p.getTeam()!!.name) && (event.block.type != Material.SCAFFOLDING)) event.isCancelled = true
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
    fun onBlockSpread(event: BlockSpreadEvent) {
        if (isProtectedArea(event.block.location)) event.isCancelled = true
    }

    @EventHandler
    fun onPlayerBucketFill(event: PlayerBucketFillEvent) {
        if(isProtectedArea(event.block.location) && event.player.gameMode == GameMode.SURVIVAL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val p = event.player
        val b = event.block
        if(p.getTeam() != null) {
            if(getLocationArea(b.location).team != p.getTeam()!!.name && p.location.world == Bukkit.getWorld("world"))
                event.isCancelled = true
        }
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
        val p = event.player
        if(p.getTeam() != null) {
            if(getLocationArea(event.bed.location).team != p.getTeam()!!.name)
                event.isCancelled = true
        }
        else event.isCancelled = true
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {

        val p: Player = event.player

        //200 (라인 높이) 보다 높이 올라가면 즉사
        if (p.location.y >= 200 && p.gameMode == GameMode.SURVIVAL)
            p.health = 0.0

        val prevArea = getLocationArea(event.from).areaID
        val afterArea = getLocationArea(event.to!!).areaID
        if (event.to!!.world == Bukkit.getWorld("world") && prevArea != afterArea)
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${afterArea}번 구역에 진입했습니다."))
        if (isProtectedArea(event.to!!)) {
            getLocationArea(p.location).bar.addPlayer(p)
        }
        else if (p.location.world == Bukkit.getWorld("world")){
            getLocationArea(p.location).bar.removePlayer(p)
        }
    }
}