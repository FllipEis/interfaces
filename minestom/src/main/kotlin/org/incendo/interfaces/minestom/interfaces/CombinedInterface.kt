package org.incendo.interfaces.minestom.interfaces

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.pane.CombinedPane
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.view.CombinedInterfaceView
import org.incendo.interfaces.minestom.view.InterfaceView

public class CombinedInterface internal constructor(
    override val rows: Int,
    override val initialTitle: Component?,
    override val closeHandlers: Collection<CloseHandler>,
    override val transforms: Collection<AppliedTransform<CombinedPane>>,
    override val clickPreprocessors: Collection<ClickHandler>,
    override val itemPostProcessor: ((ItemStack) -> Unit)?
) : Interface<CombinedPane>, TitledInterface {

    override fun totalRows(): Int = rows + 4

    override fun createPane(): CombinedPane = CombinedPane(rows)

    override suspend fun open(player: Player, parent: InterfaceView?): CombinedInterfaceView {
        val view = CombinedInterfaceView(player, this, parent)
        view.open()

        return view
    }
}
