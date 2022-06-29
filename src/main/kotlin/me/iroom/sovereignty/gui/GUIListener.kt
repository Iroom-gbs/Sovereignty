package me.iroom.sovereignty.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent

class GUIListener : Listener {
    @EventHandler
    fun guiEvent(event: InventoryClickEvent) {
        if(event.inventory.holder is LevelUpDownGUI)
            (event.inventory.holder as LevelUpDownGUI).onClick(event)
    }

    @EventHandler
    fun guiInteractEvent(event: InventoryInteractEvent) {
        if(event.inventory.holder is LevelUpDownGUI)
            (event.inventory.holder as LevelUpDownGUI).onInteract(event)
    }
}