package org.incendo.interfaces.minestom.utilities

import net.minestom.server.MinecraftServer

internal fun runSync(function: () -> Unit) {
    MinecraftServer.getSchedulerManager().buildTask { function() }.schedule()
}
