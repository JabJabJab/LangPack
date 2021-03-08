package jab.langpack.bungeecord

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * The **LangCfg** class handles the config.yml for the Bungeecord lang-pack environment.
 *
 * @author Jab
 *
 * @param plugin The plugin instance.
 */
@Suppress("unused")
internal class LangCfg(plugin: LangPlugin) {

    init {

        plugin.saveResource("config.yml")
        val cfg = YamlConfiguration.loadConfiguration(
            File(plugin.dataFolder, "config.yml")
        )

        if (cfg.isBoolean("broadcast_connection_events")) {
            broadcastConnectionEvents = cfg.getBoolean("broadcast_connection_events")
        }
    }

    companion object {

        /**
         * If set to true, connection events will be broadcast to all connections on the network.
         */
        var broadcastConnectionEvents = false
    }
}
