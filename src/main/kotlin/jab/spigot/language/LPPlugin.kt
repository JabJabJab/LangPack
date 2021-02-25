package jab.spigot.language

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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
        instance = this

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

//        if (!File(langDir, "test_en.yml").exists()) {
        saveResource("lang/test_en.yml", true)
//        }
//        if (!File(langDir, "test_jp.yml").exists()) {
        saveResource("lang/test_jp.yml", true)
//        }

        lang = LangPackage(langDir, "test")
        lang!!.load()

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        lang = null
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {

        val runnable = Runnable {
            println(event)
            val lang = lang!!
            val player = event.player
            lang.broadcastDynamic("enter_server", LangArg("player", player.displayName))
        }

        server.scheduler.runTaskLater(this, runnable, 20L)
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        println(event)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            return true
        }

        val player: Player = sender

        val lang = lang!!

        when {
            command.name.equals("hover", true) -> {

                var language = Language.getLanguage(player)
                if (language == null) {
                    language = lang.defaultLanguage
                }

                val hoverMsg = lang.get("hover_message", language)!!
                val hoverText = lang.getList("hover_message_text", language)!!.filterNotNull()
                val hoverComponent = LangPackage.createHoverComponent(hoverMsg, hoverText)

                player.spigot().sendMessage(hoverComponent)

            }
            command.name.equals("subcommand", true) -> {

            }
            command.name.equals("subsubcommand", true) -> {

            }
        }

        return true
    }

    companion object {
        var instance: LPPlugin? = null
    }
}