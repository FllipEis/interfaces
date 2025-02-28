package org.incendo.interfaces.minestom.view

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withTimeout
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import org.incendo.interfaces.minestom.Constants.SCOPE
import org.incendo.interfaces.minestom.event.DrawPaneEvent
import org.incendo.interfaces.minestom.interfaces.Interface
import org.incendo.interfaces.minestom.inventory.InterfacesInventory
import org.incendo.interfaces.minestom.pane.CompletedPane
import org.incendo.interfaces.minestom.pane.Pane
import org.incendo.interfaces.minestom.pane.complete
import org.incendo.interfaces.minestom.transform.AppliedTransform
import org.incendo.interfaces.minestom.update.CompleteUpdate
import org.incendo.interfaces.minestom.update.TriggerUpdate
import org.incendo.interfaces.minestom.utilities.CollapsablePaneMap
import org.incendo.interfaces.minestom.utilities.runSync
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Exception
import kotlin.time.Duration.Companion.seconds

public abstract class AbstractInterfaceView<I : InterfacesInventory, P : Pane>(
    public val player: Player,
    public val backing: Interface<P>,
    private val parent: InterfaceView?
) : InterfaceView {

    public companion object {
        public const val COLUMNS_IN_CHEST: Int = 9
    }

    private val logger = LoggerFactory.getLogger(AbstractInterfaceView::class.java)
    private val semaphore = Semaphore(1)
    private val queue = AtomicInteger(0)

    protected var firstPaint: Boolean = true

    internal var isProcessingClick = false

    private val panes = CollapsablePaneMap.create(backing.totalRows(), backing.createPane())
    internal lateinit var pane: CompletedPane

    protected lateinit var currentInventory: I

    private suspend fun setup() {
        backing.transforms
            .flatMap(AppliedTransform<P>::triggers)
            .forEach { trigger ->
                trigger.addListener(this) {
                    // Handle all trigger updates asynchronously
                    SCOPE.launch {
                        // If the first paint has not completed we do not perform any updates
                        if (firstPaint) return@launch
                        TriggerUpdate(trigger).apply(this@addListener)
                    }
                }
            }

        // Run a complete update which draws all transforms
        // and then opens the menu again
        CompleteUpdate.apply(this)
    }

    override suspend fun open() {
        // If this menu overlaps the player inventory we always
        // need to do a brand new first paint every time!
        if (firstPaint || overlapsPlayerInventory()) {
            firstPaint = true
            setup()
            firstPaint = false
        } else {
            renderAndOpen(openIfClosed = true)
        }
    }

    override fun close() {
        if (isOpen(player)) {
            // Ensure we always close on the main thread!
            runSync {
                player.closeInventory()
            }
        }
    }

    override fun parent(): InterfaceView? {
        return parent
    }

    override suspend fun back() {
        if (parent == null) {
            close()
        } else {
            parent.open()
        }
    }

    public abstract fun createInventory(): I

    public abstract fun openInventory()

    public abstract fun isOpen(player: Player): Boolean

    internal suspend fun renderAndOpen(openIfClosed: Boolean) {
        // Don't update if closed
        if (!openIfClosed && !isOpen(player)) return

        // If there is already queue of 2 renders we don't bother!
        if (queue.get() >= 2) return

        // Await to acquire a semaphore before starting the render
        queue.incrementAndGet()
        semaphore.acquire()
        try {
            withTimeout(6.seconds) {
                pane = panes.collapse()
                renderToInventory(openIfClosed) { createdNewInventory ->
                    // send an update packet if necessary
                    if (!createdNewInventory && requiresPlayerUpdate()) {
                        player.openInventory?.update(player)
                        player.inventory.update()
                    }
                }
            }
        } finally {
            semaphore.release()
            queue.decrementAndGet()
        }
    }

    internal fun applyTransforms(transforms: Collection<AppliedTransform<P>>): List<Deferred<Unit>> {
        if (MinecraftServer.isStopping() || !player.isOnline) {
            return listOf()
        }

        return transforms.map { transform ->
            SCOPE.async {
                try {
                    // Don't run transforms for an offline player!
                    if (MinecraftServer.isStopping() || !player.isOnline) return@async

                    withTimeout(6.seconds) {
                        runTransformAndApplyToPanes(transform)
                    }
                } catch (exception: Exception) {
                    logger.error("Failed to run and apply transform: $transform", exception)
                    return@async
                }
                return@async
            }
        }
    }

    private suspend fun runTransformAndApplyToPanes(transform: AppliedTransform<P>) {
        val pane = backing.createPane()
        transform(pane, this@AbstractInterfaceView)
        val completedPane = pane.complete(player)

        // Access to the pane has to be shared through a semaphore
        semaphore.acquire()
        panes[transform.priority] = completedPane
        semaphore.release()
    }

    protected open fun drawPaneToInventory(opened: Boolean) {
        // NEVER draw to the player's inventory if it's not allowed!
        if (overlapsPlayerInventory() && !opened) return

        pane.forEach { row, column, element ->
            currentInventory.set(row, column, element.itemStack.apply { this?.let { backing.itemPostProcessor?.invoke(it) } })
        }
        MinecraftServer.getGlobalEventHandler().call(DrawPaneEvent(player))
    }

    protected open fun requiresNewInventory(): Boolean = firstPaint

    protected open fun requiresPlayerUpdate(): Boolean = false

    protected open fun overlapsPlayerInventory(): Boolean = false

    protected open suspend fun renderToInventory(openIfClosed: Boolean, callback: (Boolean) -> Unit) {
        // If a new inventory is required we create one
        // and mark that the current one is not to be used!
        val createdInventory = if (firstPaint || requiresNewInventory()) {
            currentInventory = createInventory()
            true
        } else {
            false
        }

        // Draw the contents of the inventory synchronously because
        // we don't want it to happen in between ticks and show
        // a half-finished inventory.
        runSync {
            // Determine if the inventory is currently open or being opened immediately,
            // otherwise we never draw to player inventories. This ensures lingering
            // updates on menus that have closed do not affect future menus that actually
            // ended up being opened.
            val isOpen = isOpen(player)
            val openingSoon = (openIfClosed && !isOpen) || createdInventory
            drawPaneToInventory(openingSoon || isOpen)
            callback(createdInventory)

            if (openingSoon) {
                openInventory()
            }
        }
    }
}
