@file:Suppress("unused")

package jab.sledgehammer.langpack.spigot.example.kotlin

import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLocaleChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ExamplePlugin : JavaPlugin(), Listener {

    private val greetList = HashMap<UUID, Boolean>()
    private val pack = SpigotLangPack(classLoader)
    private var joinMsg = false
    private var leaveMsg = false

    override fun onEnable() {

        saveDefaultConfig()

        val cfg = config
        if (cfg.contains("join_messages") && cfg.isBoolean("join_messages")) {
            joinMsg = cfg.getBoolean("join_messages")
        }
        if (cfg.contains("leave_messages") && cfg.isBoolean("leave_messages")) {
            leaveMsg = cfg.getBoolean("leave_messages")
        }

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
        if (!joinMsg) return
        // !!NOTE: The server executes this event prior to the client sending the locale information.
        //         Log the information to be processed only when the client settings are sent. -Jab
        greetList[event.player.uniqueId] = true
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        if (!leaveMsg) return

        with(event) {
            pack.broadcast("event.leave_server", LangArg("player", player.displayName))
        }
    }
}
