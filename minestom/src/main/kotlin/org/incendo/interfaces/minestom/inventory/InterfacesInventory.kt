package org.incendo.interfaces.minestom.inventory

import net.minestom.server.item.ItemStack

public interface InterfacesInventory {

    public fun set(row: Int, column: Int, item: ItemStack?): Boolean

    public fun get(row: Int, column: Int): ItemStack?
}
