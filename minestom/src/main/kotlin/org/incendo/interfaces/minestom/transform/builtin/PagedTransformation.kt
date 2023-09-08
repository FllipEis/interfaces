package org.incendo.interfaces.minestom.transform.builtin

import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import org.incendo.interfaces.minestom.drawable.Drawable
import org.incendo.interfaces.minestom.element.StaticElement
import org.incendo.interfaces.minestom.grid.GridPoint
import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.properties.Trigger
import org.incendo.interfaces.minestom.transform.ReactiveTransform
import org.incendo.interfaces.minestom.utilities.BoundInteger
import org.incendo.interfaces.minestom.view.InterfaceView

public abstract class PagedTransformation<P : Pane>(
    private val back: PaginationButton,
    private val forward: PaginationButton,
    extraTriggers: Array<Trigger> = emptyArray()
) : ReactiveTransform<P> {

    protected val boundPage: BoundInteger = BoundInteger(0, 0, Integer.MAX_VALUE)
    protected var page: Int by boundPage

    override suspend fun invoke(pane: P, view: InterfaceView) {
        applyButton(pane, back, !boundPage.hasPreceeding())
        applyButton(pane, forward, !boundPage.hasSucceeding())
    }

    protected open fun applyButton(pane: Pane, button: PaginationButton, displayFallback: Boolean) {
        val (point, drawable, increments) = button

        if (displayFallback) {
            button.fallbackDrawable?.let {
                pane[point] = StaticElement(it) { (player, _, _) -> button.fallbackClickHandler.invoke(player) }
            }
            return
        }

        pane[point] = StaticElement(drawable) { (player, _, click) ->
            increments[click]?.let { increment -> page += increment }
            button.clickHandler(player)
        }
    }

    override val triggers: Array<Trigger> = arrayOf<Trigger>(boundPage).plus(extraTriggers)
}

public data class PaginationButton(
    public val position: GridPoint,
    public val drawable: Drawable,
    public val increments: Map<ClickType, Int>,
    public val clickHandler: (Player) -> Unit = {},
    public val fallbackDrawable: Drawable? = null,
    public val fallbackClickHandler: (Player) -> Unit = {}
)
