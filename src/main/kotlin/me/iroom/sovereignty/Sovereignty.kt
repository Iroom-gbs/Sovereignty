package me.iroom.sovereignty

import me.iroom.sovereignty.area.TeamManager.registerTeam
import me.iroom.sovereignty.commands.CommandStructure
import me.iroom.sovereignty.commands.SubCommand
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

//게시판//
//5 X 5로 나뉜 지역 기준 -500 500이 0번 500 -500이 24번 (좌표평면에서 왼쪽위부터 오른쪽아래)
//TODO: 구역이 특정 플레이어 팀 땅인지 확인하는 함수 만들어줘 => 현재 플레이어 팀 구하는거 만듬
//TODO: 자기가 밟고 있는 블럭을 해당 구역의 코어블럭으로 만드는 명령어 만들워줘 (크리 전용) => 하단 TODO
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
                        //TODO
                        true
                    })).register("sovereignty")
    }

    override fun onDisable() {
        server.consoleSender.sendMessage("${ChatColor.RED}소버린티 플러그인이 비활성화되었습니다.")
    }
}
