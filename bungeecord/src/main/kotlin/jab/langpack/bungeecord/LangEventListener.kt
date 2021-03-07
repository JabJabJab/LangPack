package jab.langpack.bungeecord

import jab.langpack.commons.LangArg
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.concurrent.TimeUnit

internal class LangEventListener(private val plugin: LangPlugin) : Listener {

    init {
        ProxyServer.getInstance().pluginManager.registerListener(plugin, this)
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val server = ProxyServer.getInstance()
        server.scheduler.schedule(plugin, {
            plugin.pack?.broadcast("event.connect", LangArg("player", event.player.name))
        }, 1L, TimeUnit.SECONDS)
    }

    @EventHandler
    fun on(event: PlayerDisconnectEvent) {
        val server = ProxyServer.getInstance()
        server.scheduler.schedule(plugin, {
            plugin.pack?.broadcast("event.disconnect", LangArg("player", event.player.name))
        }, 1L, TimeUnit.SECONDS)
    }
}