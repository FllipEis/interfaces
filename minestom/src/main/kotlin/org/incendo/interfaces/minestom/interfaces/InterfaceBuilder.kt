package org.incendo.interfaces.minestom.interfaces

import org.incendo.interfaces.minestom.pane.Pane

public abstract class InterfaceBuilder<P : Pane, T : Interface<P>> {

    public abstract fun build(): T
}
