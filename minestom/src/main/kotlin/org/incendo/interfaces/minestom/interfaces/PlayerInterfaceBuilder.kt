package org.incendo.interfaces.minestom.interfaces

import org.incendo.interfaces.minestom.pane.PlayerPane

public class PlayerInterfaceBuilder :
    AbstractInterfaceBuilder<PlayerPane, PlayerInterface>() {

    override fun build(): PlayerInterface = PlayerInterface(
        closeHandlers,
        transforms,
        clickPreprocessors,
        itemPostProcessor
    )
}
