package org.incendo.interfaces.minestom.element

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.click.ClickHandler

internal data class CompletedElement(
    public val itemStack: ItemStack?,
    public val clickHandler: ClickHandler
)

internal suspend fun Element.complete(player: Player) = CompletedElement(
    drawable().draw(player),
    clickHandler()
)
