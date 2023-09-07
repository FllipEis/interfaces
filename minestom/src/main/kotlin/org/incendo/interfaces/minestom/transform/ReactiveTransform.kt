package org.incendo.interfaces.minestom.transform

import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.properties.Trigger

public interface ReactiveTransform<P : Pane> : Transform<P> {

    public val triggers: Array<Trigger>
}
