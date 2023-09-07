package org.incendo.interfaces.minestom.inventory

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.utilities.createMinestomChestInventory
import org.incendo.interfaces.minestom.utilities.gridPointToBukkitIndex
import org.incendo.interfaces.minestom.view.AbstractInterfaceView.Companion.COLUMNS_IN_CHEST

public class CombinedInterfacesInventory(
    player: Player,
    title: Component?,
    private val rows: Int
) : CachedInterfacesInventory() {

    private val chestSlots = rows * COLUMNS_IN_CHEST

    private val playerInventory = player.inventory
    public val chestInventory: Inventory = createMinestomChestInventory(rows, title)

    override fun get(row: Int, column: Int): ItemStack? {
        val bukkitIndex = gridPointToBukkitIndex(row, column)

        if (row >= rows) {
            val adjustedIndex = bukkitIndex - chestSlots
            return playerInventory.getItemStack(adjustedIndex)
        }

        return chestInventory.getItemStack(bukkitIndex)
    }

    override fun setInternal(row: Int, column: Int, item: ItemStack?) {
        val bukkitIndex = gridPointToBukkitIndex(row, column)

        if (row >= rows) {
            val adjustedIndex = bukkitIndex - chestSlots
            playerInventory.setItemStack(adjustedIndex, item?: ItemStack.AIR)
            return
        }

        chestInventory.setItemStack(bukkitIndex, item?: ItemStack.AIR)
    }
}
