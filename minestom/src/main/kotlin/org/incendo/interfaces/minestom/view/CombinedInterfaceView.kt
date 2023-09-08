package org.incendo.interfaces.minestom.view

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import org.incendo.interfaces.minestom.InterfacesListeners
import org.incendo.interfaces.minestom.interfaces.CombinedInterface
import org.incendo.interfaces.minestom.inventory.CombinedInterfacesInventory
import org.incendo.interfaces.minestom.pane.CombinedPane
import org.incendo.interfaces.minestom.utilities.TitleState
import org.incendo.interfaces.minestom.utilities.runSync

public class CombinedInterfaceView internal constructor(
    player: Player,
    backing: CombinedInterface,
    parent: InterfaceView?
) : AbstractInterfaceView<CombinedInterfacesInventory, CombinedPane>(
    player,
    backing,
    parent
) {
    private val titleState = TitleState(backing.initialTitle)

    override fun title(value: Component) {
        titleState.current = value
    }

    override fun createInventory(): CombinedInterfacesInventory = CombinedInterfacesInventory(
        player,
        titleState.current,
        backing.rows
    )

    override fun openInventory() {
        player.openInventory(this.currentInventory.chestInventory)
        InterfacesListeners.INSTANCE.setOpenInterface(player.uuid, this)
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

    override fun overlapsPlayerInventory(): Boolean = true

    override fun isOpen(player: Player): Boolean {
        return player.openInventory?.viewers?.contains(player) ?: false
    }
}
