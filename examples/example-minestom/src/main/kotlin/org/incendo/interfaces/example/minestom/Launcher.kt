package org.incendo.interfaces.example.minestom

import kotlinx.coroutines.runBlocking
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.MojangAuth
import org.incendo.interfaces.minestom.InterfacesListeners

public fun main() {
    val server = MinecraftServer.init()

    MojangAuth.init()
    val instance = MinecraftServer.getInstanceManager().createInstanceContainer()

    InterfacesListeners.install()

    MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent::class.java) {
        it.setSpawningInstance(instance)
        it.player.isFlying = true
        it.player.gameMode = GameMode.CREATIVE
    }

    MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent::class.java)   {
        if (it.message.startsWith("open")) {
            runBlocking {
                println("Opening interface for ${it.player.username}")
                val ex = TabbedExampleInterface()
                ex.create().open(it.player)
                println("Opened interface for ${it.player.username}")
            }
        }
    }

    server.start("127.0.0.1", 25565)
}
