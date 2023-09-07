package org.incendo.interfaces.minestom.utilities

import net.kyori.adventure.text.Component
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import org.incendo.interfaces.minestom.grid.GridPoint

public fun gridPointToBukkitIndex(row: Int, column: Int): Int {
    return row * 9 + column
}

public fun gridPointToBukkitIndex(gridPoint: GridPoint): Int = gridPointToBukkitIndex(gridPoint.x, gridPoint.y)

public fun forEachInGrid(rows: Int, columns: Int, function: (row: Int, column: Int) -> Unit) {
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            function(row, column)
        }
    }
}

public fun createMinestomChestInventory(
    rows: Int,
    title: Component?
): Inventory {
    return Inventory(getChestInventoryTypeByRows(rows), title?: Component.empty())
}

public fun createMinestomInventory(
    inventoryType: InventoryType,
    title: Component?
): Inventory {
    return Inventory(inventoryType, title?: Component.empty())
}

private fun getChestInventoryTypeByRows(rows: Int): InventoryType {
    return when (rows) {
        1 -> InventoryType.CHEST_1_ROW
        2 -> InventoryType.CHEST_2_ROW
        3 -> InventoryType.CHEST_3_ROW
        4 -> InventoryType.CHEST_4_ROW
        5 -> InventoryType.CHEST_5_ROW
        6 -> InventoryType.CHEST_6_ROW
        else -> throw IllegalArgumentException("Invalid number of rows: $rows")
    }
}
