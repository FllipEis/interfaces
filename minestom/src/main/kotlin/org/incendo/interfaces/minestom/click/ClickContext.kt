package org.incendo.interfaces.minestom.click

import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import org.incendo.interfaces.minestom.view.InterfaceView

public data class ClickContext(
    public val player: Player,
    public val view: InterfaceView,
    public val type: ClickType
)
