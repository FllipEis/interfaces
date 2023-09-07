package org.incendo.interfaces.minestom.interfaces

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.pane.ChestPane
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.view.ChestInterfaceView
import org.incendo.interfaces.minestom.view.InterfaceView

public class ChestInterface internal constructor(
    override val rows: Int,
    override val initialTitle: Component?,
    override val closeHandlers: Collection<CloseHandler>,
    override val transforms: Collection<AppliedTransform<ChestPane>>,
    override val clickPreprocessors: Collection<ClickHandler>,
    override val itemPostProcessor: ((ItemStack) -> Unit)?
) : Interface<ChestPane>, TitledInterface {

    public companion object {
        public const val NUMBER_OF_COLUMNS: Int = 9
    }

    override fun createPane(): ChestPane = ChestPane()

    override suspend fun open(player: Player, parent: InterfaceView?): ChestInterfaceView {
        val view = ChestInterfaceView(player, this, parent)
        view.open()

        return view
    }
}
