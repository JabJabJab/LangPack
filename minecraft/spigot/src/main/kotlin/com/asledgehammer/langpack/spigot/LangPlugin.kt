package com.asledgehammer.langpack.spigot

import com.asledgehammer.langpack.core.LangPack
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * **LangPlugin** is the Spigot plugin container for [LangPack].
 *
 * @author Jab
 */
internal class LangPlugin : JavaPlugin(), Listener {

    /**
     * The default lang-pack instance.
     */
    val pack: SpigotLangPack = SpigotLangPack(this::class.java.classLoader)

    /**
     * If set to true, tests for the spigot module will load and be accessible to authorized players.
     */
    var testsEnabled: Boolean = false

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        testsEnabled = if (config.isBoolean("tests_enabled")) config.getBoolean("tests_enabled") else false
        pack.debug = if (config.isBoolean("debug")) config.getBoolean("debug") else false

        pack.append("lang", save = true)
        if (testsEnabled) pack.append("lang_test", save = true, force = true)

        LangCommand(this)
        server.pluginManager.registerEvents(this, this)
    }

    companion object {
        var instance: LangPlugin? = null
            private set
    }
}
