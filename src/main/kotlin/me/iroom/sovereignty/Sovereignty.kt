package me.iroom.sovereignty

import me.iroom.sovereignty.area.AreaManager
import me.iroom.sovereignty.area.AreaManager.getLocationArea
import me.iroom.sovereignty.team.TeamManager.registerTeam
import me.iroom.sovereignty.commands.CommandStructure
import me.iroom.sovereignty.commands.SubCommand
import me.iroom.sovereignty.gui.GUIListener
import me.iroom.sovereignty.util.Option
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

//게시판//
//5 X 5로 나뉜 지역 기준 -500 500이 0번 500 -500이 24번 (좌표평면에서 왼쪽위부터 오른쪽아래)
//=>변경 : -500 -500이 1번 500 500이 25번
//TODO: 구역이 특정 플레이어 팀 땅인지 확인하는 함수 만들어줘 => 이거 이름 같은거 확인하면 되는거 맞지? yes
//구역 색깔 어덯게 구해? => getTeam().color
////////

class Sovereignty: JavaPlugin() {
    override fun onEnable() {
        server.consoleSender.sendMessage("${ChatColor.GREEN}소버린티 플러그인이 활성화되었습니다.")
        server.pluginManager.registerEvents(EvListener(), this)
        server.pluginManager.registerEvents(GUIListener(), this)

        val scaf =  ItemStack(Material.SCAFFOLDING,2)
        val key = NamespacedKey(this, "scaf")
        val newScaf = ShapedRecipe(key, scaf)
        newScaf.shape("BAB", "B B", "B B")
        newScaf.setIngredient('A', Material.STRING)
        newScaf.setIngredient('B', Material.STICK)
        this.server.addRecipe(newScaf)

        CommandStructure().register(SubCommand("team")
            .requireOp()
            .register(SubCommand("join") // team join <TEAM> <PLAYER>
                .execute { _, args ->
                    server.getPlayer(args[1])!!.registerTeam(args[0])
                    true
                }))
            .register(SubCommand("op")
                .requireOp()
                .register(SubCommand("set_core")
                    .execute { sender, _ ->
                        if(!sender.isOp) return@execute false

                        val loc = (sender as Player).location.subtract(0.0,1.0,0.0)
                        val block = loc.block
                        getLocationArea(block.location).coreLoc = block.location
                        block.type = Material.SEA_LANTERN

                        true
                    })
                .register(SubCommand("save")
                    .execute { _, _ ->
                        AreaManager.save()
                        true
                    })
                .register(SubCommand("load")
                    .execute { _, args ->
                        AreaManager.load(Option.readOption(File(args[0]).readText()))
                        true
                    })).register("sovereignty")

        if(File("areas.dat").exists())
            AreaManager.load(Option.readOption(File("areas.dat").readText()))
    }

    override fun onDisable() {
        server.consoleSender.sendMessage("${ChatColor.RED}소버린티 플러그인이 비활성화되었습니다.")
    }
}
