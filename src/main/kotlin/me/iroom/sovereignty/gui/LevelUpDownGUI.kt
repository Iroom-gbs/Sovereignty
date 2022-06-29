package me.iroom.sovereignty.gui

import me.iroom.sovereignty.area.Area
import me.iroom.sovereignty.area.AreaManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class LevelUpDownGUI(private val areaID: Int): InventoryHolder {
    private val area
        get() = AreaManager.Areas[areaID - 1]

    override fun getInventory() = Bukkit.createInventory(this, 9, "${areaID}번 구역").apply {
        if(area.level != 0)
            this.setItem(3, ItemStack(Material.RED_WOOL).apply {
                val meta = this.itemMeta!!
                meta.setDisplayName("레벨 다운")
                this.itemMeta = meta
            })
        this.setItem(4, ItemStack(Material.NETHER_STAR, AreaManager.Areas[areaID].level + 1).apply {
            val meta = this.itemMeta!!
            meta.setDisplayName("현재 레벨: ${AreaManager.Areas[areaID].level + 1}레벨")
            this.itemMeta = meta
        })
        if(area.level < Area.maxLevel)
            this.setItem(5, ItemStack(Material.GREEN_WOOL).apply {
                val meta = this.itemMeta!!
                meta.setDisplayName("레벨 업")
                this.itemMeta = meta
            })
        this.setItem(8, ItemStack(Material.BARRIER).apply {
            val meta = this.itemMeta!!
            meta.setDisplayName("나가기")
            this.itemMeta = meta
        })
    }

    fun onClick(event: InventoryClickEvent) {
        when(event.slot) {
            3 -> area.levelDown()
            5 -> area.levelUp()
            8 -> event.whoClicked.closeInventory()
        }
    }

    fun onInteract(event: InventoryInteractEvent) {
        event.isCancelled = true
    }
}