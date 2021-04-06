package jab.sledgehammer.langpack.spigot

import jab.sledgehammer.langpack.core.LangPack
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * **LangPlugin** is the Spigot plugin container for [LangPack].
 *
 * @author Jab
 */
internal class LangPlugin : JavaPlugin(), Listener {

    /**
     * The default lang-pack instance.
     */
    var pack: SpigotLangPack? = null

    /**
     * If set to true, tests for the spigot module will load and be accessible to authorized players.
     */
    var testsEnabled: Boolean = false

    override fun onEnable() {

        instance = this

        saveDefaultConfig()

        testsEnabled = if (config.isBoolean("tests_enabled")) {
            config.getBoolean("tests_enabled")
        } else {
            false
        }

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) langDir.mkdirs()
        pack = SpigotLangPack(this::class.java.classLoader)
//        pack!!.debug = true
        pack!!.append("lang", save = true, force = true)
        pack!!.append("lang_test", save = true, force = true)

        println(pack!!.print())

        LangCommand(this)

        server.pluginManager.registerEvents(this, this)
    }

    companion object {
        var instance: LangPlugin? = null
            private set
    }
}
