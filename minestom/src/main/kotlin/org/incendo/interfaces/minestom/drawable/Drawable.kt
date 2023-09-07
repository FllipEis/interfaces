package org.incendo.interfaces.minestom.drawable

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

public fun interface Drawable {

    public companion object {
        public fun drawable(item: ItemStack): Drawable = Drawable { item }

        public fun drawable(material: Material): Drawable = Drawable { ItemStack.of(material) }
    }

    public suspend fun draw(player: Player): ItemStack
}
