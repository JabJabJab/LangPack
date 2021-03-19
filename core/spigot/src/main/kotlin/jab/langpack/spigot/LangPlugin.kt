package jab.langpack.spigot

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * The **LangPlugin** class is the Spigot-implementation for lang-pack. All initialization for the lang-pack library
 * occurs here.
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
        pack!!.append("lang", save = true, force = true)
        pack!!.append("lang_test", save = true, force = true)
        // pack!!.debug = true

        LangCommand(this)

        server.pluginManager.registerEvents(this, this)
    }

    companion object {
        var instance: LangPlugin? = null
            private set
    }
}
