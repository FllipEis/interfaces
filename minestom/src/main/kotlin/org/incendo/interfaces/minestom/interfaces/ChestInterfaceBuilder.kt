package org.incendo.interfaces.minestom.interfaces

import net.kyori.adventure.text.Component
import org.incendo.interfaces.minestom.pane.ChestPane

public class ChestInterfaceBuilder :
    AbstractInterfaceBuilder<ChestPane, ChestInterface>() {

    public var rows: Int = 0
    public var initialTitle: Component? = null

    override fun build(): ChestInterface = ChestInterface(
        rows,
        initialTitle,
        closeHandlers,
        transforms,
        clickPreprocessors,
        itemPostProcessor
    )
}
