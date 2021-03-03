package jab.spigot.language

import org.bukkit.configuration.file.FileConfiguration

class LangCfg(private var plugin: LangPlugin) {

    private val cfg: FileConfiguration

    init {
        plugin.saveDefaultConfig()
        cfg = plugin.config

        // Test flag for testing the internals of the plugin.
        testsEnabled = if (cfg.isBoolean("tests_enabled")) {
            cfg.getBoolean("tests_enabled")
        } else {
            false
        }
    }

    companion object {
        var testsEnabled: Boolean = false
            private set
    }
}