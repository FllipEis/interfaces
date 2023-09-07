package org.incendo.interfaces.minestom.transform

import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.view.InterfaceView

public fun interface Transform<P : Pane> : suspend (P, InterfaceView) -> Unit
