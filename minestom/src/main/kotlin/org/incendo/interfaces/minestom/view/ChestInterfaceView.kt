package org.incendo.interfaces.minestom.view

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import org.incendo.interfaces.minestom.InterfacesListeners
import org.incendo.interfaces.minestom.interfaces.ChestInterface
import org.incendo.interfaces.minestom.inventory.ChestInterfacesInventory
import org.incendo.interfaces.minestom.pane.ChestPane
import org.incendo.interfaces.minestom.utilities.TitleState
import org.incendo.interfaces.minestom.utilities.runSync

public class ChestInterfaceView internal constructor(
    player: Player,
    backing: ChestInterface,
    parent: InterfaceView?
) : AbstractInterfaceView<ChestInterfacesInventory, ChestPane>(
    player,
    backing,
    parent
) {
    private val titleState = TitleState(backing.initialTitle)

    override fun title(value: Component) {
        titleState.current = value
    }

    override fun createInventory(): ChestInterfacesInventory = ChestInterfacesInventory(
        titleState.current,
        backing.rows
    )

    override fun openInventory() {
        // Close whatever inventory the player has open so they can look at their normal inventory!
        // This will only continue if the menu hasn't been closed yet.
        if (!isOpen(player)) {
            player.openInventory(this.currentInventory.chestInventory)
            InterfacesListeners.INSTANCE.setOpenInterface(player.uuid, this)
        }
    }

    override fun close() {
        // Ensure we update the interface state in the main thread!
        // Even if the menu is not currently on the screen.
        runSync {
            InterfacesListeners.INSTANCE.setOpenInterface(player.uuid, null)
        }
    }

    override fun requiresPlayerUpdate(): Boolean = false

    override fun requiresNewInventory(): Boolean = titleState.hasChanged

    override fun isOpen(player: Player): Boolean {
        return player.openInventory?.viewers?.contains(player)?: false
    }
}
