package org.incendo.interfaces.minestom.update

import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.view.AbstractInterfaceView

public sealed interface Update {

    public suspend fun <P : Pane> apply(target: AbstractInterfaceView<*, P>)
}
