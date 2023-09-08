package org.incendo.interfaces.minestom

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.launch
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.inventory.click.ClickType
import org.incendo.interfaces.minestom.Constants.SCOPE
import org.incendo.interfaces.minestom.click.ClickContext
import org.incendo.interfaces.minestom.click.ClickHandler
import org.incendo.interfaces.minestom.click.CompletableClickHandler
import org.incendo.interfaces.minestom.grid.GridPoint
import org.incendo.interfaces.minestom.view.AbstractInterfaceView
import org.incendo.interfaces.minestom.view.PlayerInterfaceView
import java.util.*
import java.util.concurrent.TimeUnit

public class InterfacesListeners private constructor() {

    public companion object {
        /** The current instance for interface listeners class. */
        public lateinit var INSTANCE: InterfacesListeners
            private set

        /** Installs interfaces into this plugin. */
        public fun install() {
            require(!::INSTANCE.isInitialized) { "Already installed!" }
            INSTANCE = InterfacesListeners()
            MinecraftServer.getGlobalEventHandler()
                .addListener(InventoryCloseEvent::class.java) { INSTANCE.onClose(it) }
                .addListener(InventoryPreClickEvent::class.java) { INSTANCE.onClick(it) }
                .addListener(PlayerDisconnectEvent::class.java) { INSTANCE.onPlayerQuit(it) }
                .addListener(PlayerUseItemEvent::class.java) {
                    INSTANCE.onInteract(
                        it.player,
                        ClickType.RIGHT_CLICK,
                        it
                    )
                }
                .addListener(PlayerUseItemOnBlockEvent::class.java) {
                    INSTANCE.onInteract(
                        it.player,
                        ClickType.RIGHT_CLICK,
                        null
                    )
                }
//                .addListener(EntityAttackEvent::class.java) { INSTANCE.onInteract(it.player, ClickType.RIGHT_CLICK, null) }
            //TODO: add left click
        }

        private val PLAYER_INVENTORY_RANGE = 0..40
        private const val OUTSIDE_CHEST_INDEX = -999
    }

    private val spamPrevention: Cache<UUID, Unit> = Caffeine.newBuilder()
        .expireAfterWrite(200.toLong(), TimeUnit.MILLISECONDS)
        .build()

    /** A cache of open player interface views, with weak values. */
    private val openPlayerInterfaceViews: Cache<UUID, PlayerInterfaceView> = Caffeine.newBuilder()
        .weakValues()
        .build()

    /** A cache of open player interface views, with weak values. */
    private val openInterfaceViews: Cache<UUID, AbstractInterfaceView<*, *>> = Caffeine.newBuilder()
        .weakValues()
        .build()


    /** Returns the currently open interface for [playerId]. */
    public fun getOpenPlayerInterface(playerId: UUID): PlayerInterfaceView? =
        openPlayerInterfaceViews.getIfPresent(playerId)

    /** Updates the currently open interface for [playerId] to [view]. */
    public fun setOpenPlayerInterfaceInterface(playerId: UUID, view: PlayerInterfaceView?) {
        if (view == null) {
            openPlayerInterfaceViews.invalidate(playerId)
        } else {
            openPlayerInterfaceViews.put(playerId, view)
        }
    }


    /** Returns the currently open interface for [playerId]. */
    public fun getOpenInterface(playerId: UUID): AbstractInterfaceView<*, *>? =
        openInterfaceViews.getIfPresent(playerId)

    /** Updates the currently open interface for [playerId] to [view]. */
    public fun setOpenInterface(playerId: UUID, view: AbstractInterfaceView<*, *>?) {
        if (view == null) {
            openInterfaceViews.invalidate(playerId)
        } else {
            openInterfaceViews.put(playerId, view)
        }
    }

    public fun onClose(event: InventoryCloseEvent) {
        SCOPE.launch {
            val view = getOpenInterface(event.player.uuid) ?: return@launch
            if (view != null) {
                view.backing.closeHandlers.forEach {
                    it.invoke(view)
                }
            }

//            if (reason !in VALID_REASON) {
//                return@launch
//            }

            getOpenPlayerInterface(event.player.uuid)?.open()
        }
    }

    public fun onClick(event: InventoryPreClickEvent) {
        val view = getOpenInterface(event.player.uuid) ?: return

        val clickedPoint = clickedPoint(view, event) ?: return

        handleClick(view, clickedPoint, event.clickType, event)
    }

    public fun onPlayerQuit(event: PlayerDisconnectEvent) {
        setOpenPlayerInterfaceInterface(event.player.uuid, null)
        setOpenInterface(event.player.uuid, null)
    }

    private fun clickedPoint(view: AbstractInterfaceView<*, *>, event: InventoryPreClickEvent): GridPoint? {
        // not really sure why this special handling is required,
        // the ordered pane system should solve this but this is the only
        // place where it's become an issue.
        if (event.inventory == event.player.inventory || event.inventory == null) {
            val index = event.slot

            if (index !in PLAYER_INVENTORY_RANGE) {
                return null
            }

            val x = view.backing.rows - 1 + index / 9
            return GridPoint(x, index % 9)
        }

        val index = event.slot

        if (index == OUTSIDE_CHEST_INDEX) {
            return null
        }

        return GridPoint.at(index / 9, index % 9)
    }

    public fun onInteract(
        player: Player,
        clickType: ClickType,
        event: CancellableEvent?
    ) {
        val view = getOpenPlayerInterface(player.uuid) as? AbstractInterfaceView<*, *> ?: return

        val slot = player.heldSlot.toInt()
        val clickedPoint = GridPoint.at(3, slot)

        //TODO: sneak is not implemented
        handleClick(view, clickedPoint, clickType, event)
    }

    private fun handleClick(
        view: AbstractInterfaceView<*, *>,
        clickedPoint: GridPoint,
        click: ClickType,
        event: CancellableEvent?
    ) {
        if (view.isProcessingClick || shouldThrottle(view.player)) {
            event?.isCancelled = true
            return
        }

        view.isProcessingClick = true

        val clickContext = ClickContext(view.player, view, click)

        view.backing.clickPreprocessors
            .forEach { handler -> ClickHandler.process(handler, clickContext) }

        val raw = view.pane.getRaw(clickedPoint)

        val clickHandler = raw
            ?.clickHandler ?: ClickHandler.ALLOW

        val completedClickHandler = clickHandler
            .run { CompletableClickHandler().apply { handle(clickContext) } }
            .onComplete { view.isProcessingClick = false }

        if (!completedClickHandler.completingLater) {
            completedClickHandler.complete()
        }

        event?.isCancelled = completedClickHandler.cancelled
    }

    private fun shouldThrottle(player: Player): Boolean =
        if (spamPrevention.getIfPresent(player.uuid) == null) {
            spamPrevention.put(player.uuid, Unit)
            false
        } else {
            true
        }
}
