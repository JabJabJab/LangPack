package jab.spigot.language

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Dummy plug-in class to allow for independent loading of library as a plug-in for multi-plugin use.
 *
 * @author Jab
 */
class LPPlugin : JavaPlugin(), Listener {

    var lang: LangPackage? = null

    override fun onEnable() {
        saveResource("lang/test_en.yml", false)
        saveResource("lang/test_jp.yml", false)

        lang = LangPackage(File(dataFolder, "lang"), "test")
        lang!!.load()
    }

    override fun onDisable() {
        lang = null
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {
        val language = Language.getLanguage(event.player)
        LangPackage.broadcast(lang.get("enter_server", language))
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {

    }
}