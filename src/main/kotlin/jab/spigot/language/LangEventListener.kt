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
internal class LangEventListener(private val plugin: LangPlugin) : Listener {

    init {
        val mgr = plugin.server.pluginManager
        mgr.registerEvents(this, plugin)
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {
        if (LangCfg.joinMessages) {
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                val lang = plugin.lang!!
                val player = event.player
                lang.broadcast("enter_server", LangArg("player", player.displayName))
            }, 20L)
        }
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        if (LangCfg.leaveMessages) {
            val lang = plugin.lang!!
            val player = event.player
            lang.broadcast("leave_server", LangArg("player", player.displayName))
        }
    }
}