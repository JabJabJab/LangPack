@file:Suppress("unused")

package jab.sledgehammer.langpack.spigot.example.kotlin

import jab.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin(), Listener {

    private var pack: SpigotLangPack? = null
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

        pack = SpigotLangPack(classLoader)
        pack!!.append("lang_example_kotlin", save = true, force = true)

        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {

        if (!joinMsg) return

        // !!NOTE: The server executes this event prior to the client sending the locale information. Slightly delay
        // any join event if using LangPack for the player. - Jab
        server.scheduler.runTaskLater(this, Runnable {
            with(event) {
                pack?.broadcast("event.enter_server", LangArg("player", player.displayName))
            }
        }, 20L)
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {

        if (!leaveMsg) return

        with(event) {
            pack?.broadcast("event.leave_server", LangArg("player", player.displayName))
        }
    }
}
