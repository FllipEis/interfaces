package org.incendo.interfaces.minestom.interfaces

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.InterfacesListeners
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.view.InterfaceView

public interface Interface<P : Pane> {

    public val rows: Int

    public val closeHandlers: Collection<CloseHandler>

    public val transforms: Collection<AppliedTransform<P>>

    public val clickPreprocessors: Collection<ClickHandler>

    public val itemPostProcessor: ((ItemStack) -> Unit)?

    public fun totalRows(): Int = rows

    public fun createPane(): P

    /**
     * Opens an [InterfaceView] from this [Interface]. The parent defaults to whatever menu the player
     * is currently viewing.
     *
     * @param player the player to show the view
     * @param parent the parent view that is opening the interface
     * @return the view
     */
    public suspend fun open(
        player: Player,
        parent: InterfaceView? =
            InterfacesListeners.INSTANCE.getOpenPlayerInterface(player.uuid)
    ): InterfaceView
}
