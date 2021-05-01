package com.asledgehammer.langpack.bungeecord

import com.asledgehammer.langpack.core.LangPack
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * **LangPlugin** is the Bungeecord plugin container for [LangPack].
 *
 * @author Jab
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class LangPlugin : Plugin() {

    internal var testsEnabled = true
    internal val pack = BungeeLangPack(this::class.java.classLoader)
    private val configProvider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
    private var config: Configuration? = null

    override fun onEnable() {
        saveDefaultConfig()
        val config = getConfig()
        testsEnabled = if (config.contains("tests_enabled")) config.getBoolean("tests_enabled") else false
        pack.debug = if (config.contains("debug")) config.getBoolean("debug") else false

        pack.append("lang", save = true)
        if (testsEnabled) {
            pack.append("lang_test", save = true, force = true)
        }

        this.proxy.pluginManager.registerCommand(this, LangCommand(this))
    }

    private fun saveDefaultConfig() {
        if (!dataFolder.exists()) dataFolder.mkdir()
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            try {
                getResourceAsStream("config.yml").use { inputStream ->
                    Files.copy(inputStream, file.toPath())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getConfig(): Configuration {
        if (config == null) {
            config = configProvider.load(File(dataFolder, "config.yml"))
        }
        return config!!
    }
}
