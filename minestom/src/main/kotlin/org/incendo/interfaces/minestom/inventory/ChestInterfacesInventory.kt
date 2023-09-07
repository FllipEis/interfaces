package org.incendo.interfaces.minestom.inventory

import net.kyori.adventure.text.Component
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.utilities.createMinestomChestInventory
import org.incendo.interfaces.minestom.utilities.gridPointToBukkitIndex

public class ChestInterfacesInventory(
    title: Component?,
    rows: Int
) : CachedInterfacesInventory() {

    public val chestInventory: Inventory = createMinestomChestInventory(rows, title)

    override fun get(row: Int, column: Int): ItemStack? {
        val index = gridPointToBukkitIndex(row, column)
        return chestInventory.getItemStack(index)
    }

    override fun setInternal(row: Int, column: Int, item: ItemStack?) {
        val index = gridPointToBukkitIndex(row, column)
        chestInventory.setItemStack(index, item?: ItemStack.AIR)
    }
}
