package org.incendo.interfaces.minestom.transform

import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.properties.Trigger

public class AppliedTransform<P : Pane>(
    internal val priority: Int,
    internal val triggers: Set<Trigger>,
    transform: Transform<P>
) : Transform<P> by transform
