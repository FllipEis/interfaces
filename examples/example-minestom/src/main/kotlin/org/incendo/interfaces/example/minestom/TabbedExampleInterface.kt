package org.incendo.interfaces.example.minestom

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.minestom.server.item.Material
import org.incendo.interfaces.minestom.drawable.Drawable.Companion.drawable
import org.incendo.interfaces.minestom.element.StaticElement
import org.incendo.interfaces.minestom.interfaces.CombinedInterfaceBuilder
import org.incendo.interfaces.minestom.interfaces.Interface
import org.incendo.interfaces.minestom.interfaces.buildCombinedInterface
import kotlin.time.Duration.Companion.milliseconds

public class TabbedExampleInterface  {

    private companion object {
        private val ELEMENT = StaticElement(
            drawable(Material.NETHER_STAR)
        )
    }
    public fun create(): Interface<*> = first

    private fun CombinedInterfaceBuilder.defaults() {
        rows = 6

        withTransform { pane, _ ->
            pane[8, 2] = StaticElement(drawable(Material.IRON_INGOT)) { (player) ->
                completingLater = true

                runBlocking {
                    first.open(player)
                    complete()
                }
            }

            pane[8, 4] = StaticElement(drawable(Material.GOLD_INGOT)) { (player) ->
                completingLater = true

                runBlocking {
                    second.open(player)
                    complete()
                }
            }
        }
    }

    private val first = buildCombinedInterface {
        defaults()

        withTransform { pane, _ ->
            pane[0, 1] = ELEMENT
        }

        withTransform { pane, _ ->
            delay(100.milliseconds)
            pane[2, 4] = ELEMENT
        }
    }

    private val second = buildCombinedInterface {
        defaults()

        withTransform { pane, _ ->
            pane[1, 2] = ELEMENT
        }

        withTransform { pane, _ ->
            delay(100.milliseconds)
            pane[3, 5] = ELEMENT
        }
    }
}
