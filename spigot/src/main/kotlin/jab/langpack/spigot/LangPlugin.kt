package jab.langpack.spigot

import jab.langpack.core.LangPack
import org.bukkit.configuration.ConfigurationSection
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
    var pack: LangPack? = null

    override fun onEnable() {

        instance = this

        CFG(this)

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) langDir.mkdirs()
        pack = LangPack(this::class.java.classLoader)
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

    /**
     * The **LangCfg** class handles reading and storage of global flags set for the lang-pack plugin.
     *
     * @author Jab
     *
     * @param plugin The plugin instance to read the config.yml file.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    internal class CFG(plugin: LangPlugin) {

        init {
            plugin.saveDefaultConfig()
            read(plugin.config)
        }

        /**
         * Reads from a configuration for global flags for lang-pack.
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
             * If set to true, the server will display join messages for players.
             */
            var joinMessages: Boolean = false
                private set

            /**
             * If set to true, the server will display leave messages for players.
             */
            var leaveMessages: Boolean = false
                private set

            /**
             * If set to true, tests for the spigot module will load and be accessible to authorized players.
             */
            var testsEnabled: Boolean = false
                private set
        }
    }
}
