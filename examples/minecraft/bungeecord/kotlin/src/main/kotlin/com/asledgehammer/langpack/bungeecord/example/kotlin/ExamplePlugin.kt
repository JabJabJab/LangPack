@file:Suppress("unused")

package com.asledgehammer.langpack.bungeecord.example.kotlin

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.SettingsChangedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * **ExamplePlugin** TODO: Document.
 *
 * @author Jab
 */
class ExamplePlugin : Plugin(), Listener {

    private val greetList = HashMap<UUID, Boolean>()
    private val pack = BungeeLangPack(this::class.java.classLoader)

    override fun onEnable() {
        pack.append("lang_example_kotlin", save = true, force = true)
        ProxyServer.getInstance().pluginManager.registerListener(this, this)
    }

    @EventHandler
    fun on(event: SettingsChangedEvent) {
        val player = event.player
        val playerId = player.uniqueId
        if (greetList.containsKey(playerId)) {
            greetList.remove(playerId)
            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            ProxyServer.getInstance().scheduler.schedule(this, {
                pack.broadcast("event.connect", LangArg("player", event.player.name))
            }, 1L, TimeUnit.SECONDS)
        }
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        // The server executes this event prior to the client sending the locale information. Log the information to be
        // processed only when the client settings are sent. -Jab
        greetList[event.player.uniqueId] = true
    }

    @EventHandler
    fun on(event: PlayerDisconnectEvent) {
        greetList.remove(event.player.uniqueId)
        pack.broadcast("event.disconnect", LangArg("player", event.player.name))
    }
}
