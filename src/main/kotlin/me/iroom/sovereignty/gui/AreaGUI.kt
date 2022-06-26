package me.iroom.sovereignty.gui

import me.iroom.sovereignty.area.Area
import me.iroom.sovereignty.area.AreaManager.Areas
import me.iroom.sovereignty.area.TeamManager.getTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

object AreaGUI {
    fun createGuiItem(material: Material?, name: String?): ItemStack? {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        meta!!.setDisplayName(name)
        item.itemMeta = meta
        return item
    }
    fun createGuiItem(material: Material?, name: String?, vararg lore: String?): ItemStack? {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        meta!!.setDisplayName(name)
        meta.lore = Arrays.asList(*lore)
        item.itemMeta = meta
        return item
    }

    fun showAreaGUI(p: Player) {
        p.openInventory(setAreaGUI())
    }

    fun setAreaGUI(): Inventory {
        var inv = Bukkit.createInventory(null, 45, "소버린티")
        for(i in 0..4) {
            for(j in 0..4) {
                val a = Areas[i + 5 * j]
                var matName = "CONCRETE"
                var colorName = ""
                if(a.reinforced) matName = "GLAZED_TERRACOTTA"
                if(a.vulnerable) matName = "STAINED_GLASS"

                if(a.team.getTeam() != null) {
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
                }
                else {
                    colorName = "RED"
                }

                Bukkit.getConsoleSender().sendMessage(colorName.plus("_").plus(matName))
                val m = Material.getMaterial(colorName.plus("_").plus(matName))
                val num = i+5*j+1
                val itemS = createGuiItem(m, num.toString().plus("번 구역"))
                inv.setItem(9 * j + i + 2, itemS)
            }
        }

        return inv
    }
}