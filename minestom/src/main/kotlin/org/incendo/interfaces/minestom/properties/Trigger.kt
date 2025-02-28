package org.incendo.interfaces.minestom.properties

public interface Trigger {
    public fun trigger()

    public fun <T : Any> addListener(reference: T, listener: T.() -> Unit)
}
