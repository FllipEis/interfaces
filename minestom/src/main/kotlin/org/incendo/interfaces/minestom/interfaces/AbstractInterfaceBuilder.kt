package org.incendo.interfaces.minestom.interfaces

import net.minestom.server.item.ItemStack
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.properties.Trigger
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.transform.ReactiveTransform
import org.incendo.interfaces.minestom.transform.Transform
import org.incendo.interfaces.minestom.utilities.IncrementingInteger

public abstract class AbstractInterfaceBuilder<P : Pane, I : Interface<P>> internal constructor() :
    InterfaceBuilder<P, I>() {

    private val transformCounter by IncrementingInteger()

    protected val closeHandlers: MutableCollection<CloseHandler> = mutableListOf()
    protected val transforms: MutableCollection<AppliedTransform<P>> = mutableListOf()
    protected val clickPreprocessors: MutableCollection<ClickHandler> = mutableListOf()

    public var itemPostProcessor: ((ItemStack) -> Unit)? = null

    public fun withTransform(vararg triggers: Trigger, transform: Transform<P>) {
        transforms.add(AppliedTransform(transformCounter, triggers.toSet(), transform))
    }

    public fun addTransform(reactiveTransform: ReactiveTransform<P>) {
        transforms.add(AppliedTransform(transformCounter, reactiveTransform.triggers.toSet(), reactiveTransform))
    }

    public fun withCloseHandler(
        closeHandler: CloseHandler
    ) {
        closeHandlers.add(closeHandler)
    }

    public fun withPreprocessor(handler: ClickHandler) {
        clickPreprocessors += handler
    }
}
