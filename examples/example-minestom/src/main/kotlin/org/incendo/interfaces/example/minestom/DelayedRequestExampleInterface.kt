package org.incendo.interfaces.example.minestom

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component.text
import net.minestom.server.item.Material
import org.incendo.interfaces.minestom.drawable.Drawable
import org.incendo.interfaces.minestom.element.StaticElement
import org.incendo.interfaces.minestom.interfaces.Interface
import org.incendo.interfaces.minestom.interfaces.buildCombinedInterface
import kotlin.time.Duration.Companion.seconds

public class DelayedRequestExampleInterface {

    private companion object {
        private val BACKING_ELEMENT = StaticElement(Drawable.drawable(Material.GRAY_CONCRETE))
    }

    @OptIn(DelicateCoroutinesApi::class)
    public fun create(): Interface<*> = buildCombinedInterface {
        initialTitle = text("Moin")
        rows = 2

        withTransform { pane, _ ->
            suspendingData().forEachIndexed { index, material ->
                pane[0, index] = StaticElement(Drawable.drawable(material)) {
                    cancelled = true
                }
            }
        }

        withTransform { pane, _ ->
            for (index in 0..8) {
                pane[1, index] = BACKING_ELEMENT
            }

            pane[0, 8] = StaticElement(Drawable.drawable(Material.ENDER_PEARL)) {
                // This is very unsafe, it's up to you to set up a way to reliably
                // launch coroutines per player in a click handler.
                GlobalScope.launch {
                    it.view.back()
                }
            }
        }
    }

    private suspend fun suspendingData(): List<Material> {
        delay(3.seconds)
        return listOf(Material.GREEN_CONCRETE, Material.YELLOW_CONCRETE, Material.RED_CONCRETE)
    }
}
