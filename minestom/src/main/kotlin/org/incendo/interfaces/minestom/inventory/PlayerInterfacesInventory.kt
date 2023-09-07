package org.incendo.interfaces.minestom.inventory

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.utilities.gridPointToBukkitIndex

public class PlayerInterfacesInventory(
    private val player: Player
) : CachedInterfacesInventory() {

    private val playerInventory = player.inventory

    override fun get(row: Int, column: Int): ItemStack? {
        val index = gridPointToBukkitIndex(row, column)
        return playerInventory.getItemStack(index)
    }

    override fun setInternal(row: Int, column: Int, item: ItemStack?) {
        val index = gridPointToBukkitIndex(row, column)
        return playerInventory.setItemStack(index, item?: ItemStack.AIR)
    }
}
