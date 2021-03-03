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

        joinMessages = if (cfg.isBoolean("join_messages")) {
            cfg.getBoolean("join_messages")
        } else {
            false
        }

        leaveMessages = if (cfg.isBoolean("leave_messages")) {
            cfg.getBoolean("leave_messages")
        } else {
            false
        }
    }

    companion object {
        var joinMessages: Boolean = false
            private set
        var leaveMessages: Boolean = false
            private set
        var testsEnabled: Boolean = false
            private set
    }
}