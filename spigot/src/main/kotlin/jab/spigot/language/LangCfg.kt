package jab.spigot.language

import org.bukkit.configuration.ConfigurationSection

/**
 * The **LangCfg** class handles reading and storage of global flags set for the LangPackage plugin.
 *
 * @author Jab
 *
 * @param plugin The plugin instance to read the config.yml file.
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class LangCfg(plugin: LangPlugin) {

    init {
        plugin.saveDefaultConfig()
        read(plugin.config)
    }

    /**
     * Reads from a configuration for global flags for LangPackage.
     *
     * @param cfg The configuration to read.
     */
    fun read(cfg: ConfigurationSection) {

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

        /**
         * If set to true, the server will display join messages for players using %
         */
        var joinMessages: Boolean = false
            private set

        /**
         * TODO: Document.
         */
        var leaveMessages: Boolean = false
            private set

        /**
         * TODO: Document.
         */
        var testsEnabled: Boolean = false
            private set
    }
}
