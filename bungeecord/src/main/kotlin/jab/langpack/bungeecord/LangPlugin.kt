package jab.langpack.bungeecord

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangArg
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * The **LangPlugin** class is the Bungeecord-implementation for lang-pack. All initialization for the lang-pack library
 * occurs here.
 *
 * @author Jab
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class LangPlugin : Plugin(), Listener {

    /**
     * The default LangPack instance.
     */
    var pack: LangPack? = null

    /**
     * If set to true, connection events will be broadcast to all connections on the network.
     */
    private var broadcastEvents = false

    override fun onEnable() {

        saveResource("config.yml")
        val cfg = YamlConfiguration.loadConfiguration(File(dataFolder, "config.yml"))
        if (cfg.isBoolean("broadcast_connection_events")) {
            broadcastEvents = cfg.getBoolean("broadcast_connection_events")
        }

        pack = LangPack(this::class.java.classLoader)
        pack!!.append("lang", save = true, force = true)

        ProxyServer.getInstance().pluginManager.registerListener(this, this)
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val server = ProxyServer.getInstance()
        server.scheduler.schedule(this, {
            pack?.broadcast("event.connect", LangArg("player", event.player.name))
        }, 1L, TimeUnit.SECONDS)
    }

    @EventHandler
    fun on(event: PlayerDisconnectEvent) {
        val server = ProxyServer.getInstance()
        server.scheduler.schedule(this, {
            pack?.broadcast("event.disconnect", LangArg("player", event.player.name))
        }, 1L, TimeUnit.SECONDS)
    }

    /**
     * (Borrowed from Bukkit's JavaPlugin)
     *
     * @param resourcePath The path to the resource in the JAR file.
     * @param replace (Optional) Set to true to overwrite the file if it exists.
     */
    fun saveResource(resourcePath: String, replace: Boolean = false) {
        var resourcePath2 = resourcePath
        resourcePath2 = resourcePath2.replace('\\', '/')
        val `in` = getResource(resourcePath2)
            ?: throw IllegalArgumentException("The embedded resource '$resourcePath2' cannot be found in $file")
        val outFile = File(dataFolder, resourcePath2)
        val lastIndex = resourcePath2.lastIndexOf('/')
        val outDir = File(dataFolder, resourcePath2.substring(0, if (lastIndex >= 0) lastIndex else 0))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        try {
            if (!outFile.exists() || replace) {
                val out: OutputStream = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                `in`.close()
            } else {
                logger.log(Level.WARNING,
                    "Could not save " + outFile.name + " to " + outFile + " because " + outFile.name + " already exists.")
            }
        } catch (ex: IOException) {
            logger.log(Level.SEVERE, "Could not save " + outFile.name + " to " + outFile, ex)
        }
    }

    private fun getResource(filename: String): InputStream? {
        return try {
            val url: URL = getClassLoader().getResource(filename) ?: return null
            val connection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        } catch (ex: IOException) {
            null
        }
    }

    private fun getClassLoader(): ClassLoader = this.javaClass.classLoader
}
