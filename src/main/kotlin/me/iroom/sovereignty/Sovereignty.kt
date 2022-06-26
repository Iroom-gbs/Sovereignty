package me.iroom.sovereignty

import me.ddayo.coroutine.Coroutine
import me.ddayo.coroutine.functions.WaitNextTick
import me.iroom.sovereignty.area.AreaManager.getLocationArea
import me.iroom.sovereignty.area.TeamManager.registerTeam
import me.iroom.sovereignty.commands.CommandStructure
import me.iroom.sovereignty.commands.SubCommand
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

//게시판//
//5 X 5로 나뉜 지역 기준 -500 500이 0번 500 -500이 24번 (좌표평면에서 왼쪽위부터 오른쪽아래)
//=>변경 : -500 -500이 1번 500 500이 25번
//TODO: 구역이 특정 플레이어 팀 땅인지 확인하는 함수 만들어줘 => 이거 이름 같은거 확인하면 되는거 맞지?
//구역 색깔 어덯게 구해?
////////

class Sovereignty: JavaPlugin() {
    override fun onEnable() {
        server.consoleSender.sendMessage("${ChatColor.GREEN}소버린티 플러그인이 활성화되었습니다.")
        server.pluginManager.registerEvents(EvListener(), this)

        CommandStructure().register(SubCommand("team")
            .register(SubCommand("join") // team join <TEAM> <PLAYER>
                .execute { _, args ->
                    server.getPlayer(args[1])!!.registerTeam(args[0])
                    true
                }))
            .register(SubCommand("op")
                .requireOp()
                .register(SubCommand("set_core")
                    .execute { sender, args ->
                        if(!sender.isOp) return@execute false

                        val loc = (sender as Player).location.subtract(0.0,1.0,0.0)
                        val block = loc.block
                        getLocationArea(block.location).coreLoc = block.location
                        block.type = Material.SEA_LANTERN

                        true
                    })).register("sovereignty")
    }

    override fun onDisable() {
        server.consoleSender.sendMessage("${ChatColor.RED}소버린티 플러그인이 비활성화되었습니다.")
    }
}
