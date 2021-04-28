@file:Suppress("unused")

package com.asledgehammer.langpack.spigot.example.kotlin

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLocaleChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * **ExamplePlugin** TODO: Document.
 *
 * @author Jab
 */
class ExamplePlugin : JavaPlugin(), Listener {

    private val greetList = HashMap<UUID, Boolean>()
    private val pack = SpigotLangPack(classLoader)

    override fun onEnable() {
        pack.append("lang_example_kotlin", save = true, force = true)
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun on(event: PlayerLocaleChangeEvent) {
        val playerId = event.player.uniqueId
        if (greetList.containsKey(playerId)) {
            val playerName = event.player.displayName
            greetList.remove(playerId)
            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            server.scheduler.runTaskLater(this, Runnable {
                pack.broadcast("event.enter_server", LangArg("player", playerName))
            }, 1L)
        }
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {
        event.joinMessage = null
        // The server executes this event prior to the client sending the locale information. Log the information to be
        // processed only when the client settings are sent. -Jab
        greetList[event.player.uniqueId] = true
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        event.quitMessage = null
        val player = event.player
        val playerId = player.uniqueId
        if (greetList.containsKey(playerId)) {
            greetList.remove(playerId)
            return
        }
        pack.broadcast("event.leave_server", LangArg("player", player.displayName))
    }
}
