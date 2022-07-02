package me.iroom.sovereignty.gui

import me.iroom.sovereignty.area.AreaManager.Areas
import me.iroom.sovereignty.team.TeamManager.getTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

object AreaGUI {
    fun showAreaGUI(p: Player) {
        p.openInventory(setAreaGUI())
    }

    fun setAreaGUI(): Inventory {
        val inv = Bukkit.createInventory(null, 45, "소버린티")
        for (i in 0..4) {
            for (j in 0..4) {
                val a = Areas[i + 5 * j]
                var matName = "CONCRETE"
                var colorName = ""
                if (a.reinforced) matName = "GLAZED_TERRACOTTA"
                if (a.vulnerable) matName = "STAINED_GLASS"

                if (a.team.getTeam() != null) {
                    when (a.team.getTeam()!!.color) {
                        ChatColor.AQUA -> colorName = "AQUA"
                        ChatColor.BLACK -> colorName = "BLACK"
                        ChatColor.RED -> colorName = "RED"
                        ChatColor.GREEN -> colorName = "GREEN"
                        ChatColor.BLUE -> colorName = "BLUE"
                        ChatColor.DARK_AQUA -> colorName = "DARK_AQUA"
                        ChatColor.DARK_BLUE -> colorName = "DARK_BLUE"
                        ChatColor.DARK_GRAY -> colorName = "DARK_GRAY"
                        ChatColor.DARK_PURPLE -> colorName = "DARK_PURPLE"
                        ChatColor.DARK_RED -> colorName = "DARK_RED"
                        ChatColor.DARK_GREEN -> colorName = "DARK_GREEN"
                        ChatColor.GOLD -> colorName = "GOLD"
                        ChatColor.GRAY -> colorName = "GRAY"
                        ChatColor.LIGHT_PURPLE -> colorName = "LIGHT_PURPLE"
                        ChatColor.WHITE -> colorName = "WHITE"
                        ChatColor.YELLOW -> colorName = "YELLOW"
                        else -> colorName = "RED"
                    }
                } else {
                    colorName = "RED"
                }

                val m = Material.getMaterial(colorName.plus("_").plus(matName))
                val num = i + 5 * j + 1
                val item = ItemStack(m!!, a.level + 1)
                val meta = item.itemMeta
                meta!!.setDisplayName(num.toString().plus("번 구역"))
                if (a.reinforced) {
                    val lore = ArrayList<String>()
                    lore.add("강화 상태")
                    var milis =
                        a.reinforceEndTime.time.time - Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).time.time
                    milis = milis / 1000 / 60
                    lore.add(milis.toString().plus("분 남음"))
                    meta.lore = lore
                }
                if (a.vulnerable) {
                    val lore = ArrayList<String>()
                    lore.add("강화 상태")
                    lore.add("취약 상태")
                    var milis =
                        a.vulnerableEndTime.time.time - Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).time.time
                    milis = milis / 1000 / 60
                    lore.add(milis.toString().plus("분 남음"))
                    meta.lore = lore
                }
                item.itemMeta = meta

                inv.setItem(9 * j + i + 2, item)
            }
        }

        return inv
    }

    fun Player.openLevelUpDownGUI(areaID: Int) = this.openInventory(LevelUpDownGUI(areaID).inventory)
}
