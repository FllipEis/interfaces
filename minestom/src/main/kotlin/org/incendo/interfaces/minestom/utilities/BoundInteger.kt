package org.incendo.interfaces.minestom.utilities

import org.incendo.interfaces.minestom.properties.DelegateTrigger
import org.incendo.interfaces.minestom.properties.Trigger
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

// todo(josh): recalculate value when max/min changed?
public class BoundInteger(
    initial: Int,
    public var min: Int,
    public var max: Int
) : ObservableProperty<Int>(initial), Trigger {

    private val delegateTrigger = DelegateTrigger()

    override fun beforeChange(property: KProperty<*>, oldValue: Int, newValue: Int): Boolean {
        val acceptableRange = min..max

        if (newValue in acceptableRange) {
            return true
        }

        val coercedValue = newValue.coerceIn(acceptableRange)
        var value by this

        value = coercedValue

        return false
    }

    override fun afterChange(property: KProperty<*>, oldValue: Int, newValue: Int): Unit = trigger()

    override fun trigger() {
        delegateTrigger.trigger()
    }

    override fun <T : Any> addListener(reference: T, listener: T.() -> Unit) {
        delegateTrigger.addListener(reference, listener)
    }

    public fun hasSucceeding(): Boolean {
        val value by this
        return value < max
    }

    public fun hasPreceeding(): Boolean {
        val value by this
        return value > min
    }
}
