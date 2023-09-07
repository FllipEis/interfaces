package org.incendo.interfaces.minestom.properties

public object EmptyTrigger : Trigger {

    override fun trigger() {
        // no implementation
    }

    override fun <T : Any> addListener(reference: T, listener: T.() -> Unit) {
        // no implementation
    }
}
