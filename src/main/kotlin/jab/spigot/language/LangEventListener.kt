package jab.spigot.language

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property plugin
 */
class LangEventListener(private val plugin: LangPlugin) : Listener {

    init {
        val mgr = plugin.server.pluginManager
        mgr.registerEvents(this, plugin)
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {

        val runnable = Runnable {
            val lang = plugin.lang!!
            val player = event.player
            lang.broadcastField("enter_server", LangArg("player", player.displayName))
        }

        plugin.server.scheduler.runTaskLater(plugin, runnable, 20L)
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        val player = event.player
        plugin.lang?.broadcastField("leave_server", LangArg("player", player.displayName))
    }
}