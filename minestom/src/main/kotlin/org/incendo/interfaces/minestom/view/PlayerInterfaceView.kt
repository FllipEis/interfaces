package org.incendo.interfaces.minestom.view

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import org.incendo.interfaces.minestom.InterfacesListeners
import org.incendo.interfaces.minestom.interfaces.PlayerInterface
import org.incendo.interfaces.minestom.inventory.PlayerInterfacesInventory
import org.incendo.interfaces.minestom.pane.PlayerPane
import org.incendo.interfaces.minestom.utilities.runSync

public class PlayerInterfaceView internal constructor(
    player: Player,
    backing: PlayerInterface
) : AbstractInterfaceView<PlayerInterfacesInventory, PlayerPane>(
    player,
    backing,
    // todo(josh): should player interface views hold a parent?
    null
) {

    override fun title(value: Component) {
        error("PlayerInventoryView's cannot have a title")
    }

    override fun overlapsPlayerInventory(): Boolean = true

    override fun requiresNewInventory(): Boolean = false

    override fun createInventory(): PlayerInterfacesInventory = PlayerInterfacesInventory(player)

    override fun openInventory() {
        // Close whatever inventory the player has open so they can look at their normal inventory!
        // This will only continue if the menu hasn't been closed yet.
        if (!isOpen(player)) {
            // First we close then we set the interface so we don't double open!
            InterfacesListeners.INSTANCE.setOpenPlayerInterfaceInterface(player.uuid, null)
            player.closeInventory()
            InterfacesListeners.INSTANCE.setOpenPlayerInterfaceInterface(player.uuid, this)
        }
    }

    override fun close() {
        // Ensure we update the interface state in the main thread!
        // Even if the menu is not currently on the screen.
        runSync {
            InterfacesListeners.INSTANCE.setOpenPlayerInterfaceInterface(player.uuid, null)
        }
    }

    override fun isOpen(player: Player): Boolean =
        player.openInventory?.inventoryType == InventoryType.CRAFTING &&
            InterfacesListeners.INSTANCE.getOpenPlayerInterface(player.uuid) == this
}
