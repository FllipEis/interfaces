package org.incendo.interfaces.minestom.element

import net.minestom.server.item.Material
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.drawable.Drawable

public interface Element {

    public companion object EMPTY : Element by StaticElement(Drawable.drawable(Material.AIR))

    public fun drawable(): Drawable

    public fun clickHandler(): ClickHandler
}
