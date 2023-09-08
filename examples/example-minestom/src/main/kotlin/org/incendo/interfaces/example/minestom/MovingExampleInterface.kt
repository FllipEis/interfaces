package org.incendo.interfaces.example.minestom

import net.minestom.server.item.Material
import org.incendo.interfaces.minestom.drawable.Drawable.Companion.drawable
import org.incendo.interfaces.minestom.element.StaticElement
import org.incendo.interfaces.minestom.interfaces.Interface
import org.incendo.interfaces.minestom.interfaces.buildCombinedInterface
import org.incendo.interfaces.minestom.utilities.BoundInteger

public class MovingExampleInterface {

    public fun create(): Interface<*> = buildCombinedInterface {
        val countProperty = BoundInteger(4, 1, 7)
        var count by countProperty

        rows = 1

        withTransform(countProperty) { pane, _ ->
            pane[0, 0] = StaticElement(drawable(Material.RED_CONCRETE)) { count-- }
            pane[0, 8] = StaticElement(drawable(Material.GREEN_CONCRETE)) { count++ }

            pane[0, count] = StaticElement(drawable(Material.STICK))
        }
    }
}
