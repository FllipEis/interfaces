package org.incendo.interfaces.minestom.interfaces

import org.incendo.interfaces.minestom.view.InterfaceView

public fun interface CloseHandler : suspend (InterfaceView) -> Unit
