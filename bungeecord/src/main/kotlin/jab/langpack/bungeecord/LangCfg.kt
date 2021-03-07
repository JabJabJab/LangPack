package jab.langpack.bungeecord

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


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
        var broadcastConnectionEvents = false
    }
}