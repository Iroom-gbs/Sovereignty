package me.iroom.sovereignty.gui

import me.iroom.sovereignty.area.Area
import me.iroom.sovereignty.area.AreaManager.Areas
import me.iroom.sovereignty.area.TeamManager.getTeam
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

object AreaGUI {
    fun createGuiItem(material: Material?, name: String?, vararg lore: String?): ItemStack? {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        meta!!.setDisplayName(name)
        meta.lore = Arrays.asList(*lore)
        item.itemMeta = meta
        return item
    }

    fun showAreaGui(p: Player) {
        p.openInventory(setAreaGUI())
    }

    fun setAreaGUI(): Inventory {
        var inv = Bukkit.createInventory(null, 45, "소버린티")
        for(i in 0..4) {
            for(j in 0..4) {
                val a = Areas[i + 5 * j]
                var matName = ""
                var colorName = ""
                if(a.reinforced) matName = "SHULKER_BOX"
                if(a.vulnerable) matName = "GRASS_BOX"
                if(a.team.getTeam() != null) {
                    colorName = a.team.getTeam()!!.color.name
                }
                else {
                    colorName = "BLACK"
                }

                val m = Material.getMaterial(colorName.plus("_").plus(matName))
                val num = i+5*j+1
                val itemS = createGuiItem(m, num.toString().plus("번 구역"))
                inv.setItem(9 * j + i + 2, itemS)
            }
        }

        return inv
    }
}