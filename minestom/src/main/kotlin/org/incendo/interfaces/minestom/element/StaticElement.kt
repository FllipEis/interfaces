package org.incendo.interfaces.minestom.element

import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.drawable.Drawable

public class StaticElement public constructor(
    private val drawable: Drawable,
    private val clickHandler: ClickHandler = ClickHandler.EMPTY
) : Element {

    override fun drawable(): Drawable = drawable

    override fun clickHandler(): ClickHandler = clickHandler
}
