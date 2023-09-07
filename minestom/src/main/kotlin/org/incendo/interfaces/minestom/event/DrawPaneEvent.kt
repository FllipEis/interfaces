package org.incendo.interfaces.minestom.event

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

/** An event emitted when the inventory of [player] is drawn. */
public class DrawPaneEvent(private val player: Player) : PlayerEvent {
    override fun getPlayer(): Player {
        return this.player
    }

}
