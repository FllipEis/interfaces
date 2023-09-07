package org.incendo.interfaces.minestom.interfaces

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.pane.PlayerPane
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.view.InterfaceView
import org.incendo.interfaces.minestom.view.PlayerInterfaceView

public class PlayerInterface internal constructor(
    override val closeHandlers: Collection<CloseHandler>,
    override val transforms: Collection<AppliedTransform<PlayerPane>>,
    override val clickPreprocessors: Collection<ClickHandler>,
    override val itemPostProcessor: ((ItemStack) -> Unit)?
) : Interface<PlayerPane> {

    public companion object {
        public const val NUMBER_OF_COLUMNS: Int = 9
    }

    override val rows: Int = 4

    override fun createPane(): PlayerPane = PlayerPane()

    override suspend fun open(player: Player, parent: InterfaceView?): PlayerInterfaceView {
        val view = PlayerInterfaceView(player, this)
        view.open()

        return view
    }
}
