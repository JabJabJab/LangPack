package jab.sledgehammer.langpack.bukkit

import jab.sledgehammer.langpack.core.LangPack
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * **LangPlugin** is the Bukkit plugin container for [LangPack].
 *
 * @author Jab
 */
internal class LangPlugin : JavaPlugin(), Listener {

    /**
     * The default lang-pack instance.
     */
    internal val pack: BukkitLangPack = BukkitLangPack(this::class.java.classLoader)

    /**
     * If set to true, tests for the spigot module will load and be accessible to authorized players.
     */
    var testsEnabled: Boolean = false

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        testsEnabled = if (config.isBoolean("tests_enabled")) config.getBoolean("tests_enabled") else false
        pack.append("lang", save = true)
        if (testsEnabled) pack.append("lang_test", save = true)
        LangCommand(this)
        server.pluginManager.registerEvents(this, this)
    }

    companion object {
        var instance: LangPlugin? = null
            private set
    }
}
