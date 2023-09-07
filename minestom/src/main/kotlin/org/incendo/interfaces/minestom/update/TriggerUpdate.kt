package org.incendo.interfaces.minestom.update

import kotlinx.coroutines.awaitAll
import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.properties.Trigger
import org.incendo.interfaces.minestom.view.AbstractInterfaceView

public class TriggerUpdate(
    private val trigger: Trigger
) : Update {

    override suspend fun <P : Pane> apply(target: AbstractInterfaceView<*, P>) {
        // Re-run all transforms waiting on this trigger and open the menu
        // again when they have updated
        val filteredTransforms = target.backing.transforms
            .filter { transform -> transform.triggers.contains(trigger) }
        target.applyTransforms(filteredTransforms).awaitAll()
        target.renderAndOpen(openIfClosed = false)
    }
}
