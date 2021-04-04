package jab.sledgehammer.langpack.bungeecord

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangArg
import jab.sledgehammer.config.ConfigFile
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.io.*
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * **LangPlugin** is the Bungeecord plugin container for [LangPack].
 *
 * @author Jab
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class LangPlugin : Plugin(), Listener {

    /**
     * The default LangPack instance.
     */
    var pack: BungeeLangPack? = null

    /**
     * If set to true, connection events will be broadcast to all connections on the network.
     */
    private var broadcastEvents = false

    override fun onEnable() {
        saveResource("config.yml")
        val cfg = ConfigFile().load(File(dataFolder, "config.yml"))
        if (cfg.isBoolean("broadcast_connection_events")) {
            broadcastEvents = cfg.getBoolean("broadcast_connection_events")
        }
        pack = BungeeLangPack(this::class.java.classLoader)
        pack!!.append("lang", save = true, force = true)
        ProxyServer.getInstance().pluginManager.registerListener(this, this)
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        // !!NOTE: The server executes this event prior to the client sending the locale information. Slightly delay
        // any join event if using LangPack for the player. - Jab
        ProxyServer.getInstance().scheduler.schedule(this, {
            pack?.broadcast("event.connect", LangArg("player", event.player.name))
        }, 20L, TimeUnit.SECONDS)
    }

    @EventHandler
    fun on(event: PlayerDisconnectEvent) {
        pack?.broadcast("event.disconnect", LangArg("player", event.player.name))
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
        val inputStream = getResource(resourcePath2)
        require(inputStream != null) { "The embedded resource '$resourcePath2' cannot be found in $file" }
        val outFile = File(dataFolder, resourcePath2)
        val lastIndex = resourcePath2.lastIndexOf('/')
        val outDir = File(dataFolder, resourcePath2.substring(0, if (lastIndex >= 0) lastIndex else 0))
        if (!outDir.exists()) require(outDir.mkdirs()) { "Could not create directory: ${outDir.path}" }
        try {
            if (!outFile.exists() || replace) {
                val out: OutputStream = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                inputStream.close()
            } else {
                logger.log(
                    Level.WARNING,
                    "Could not save ${outFile.name} to $outFile because ${outFile.name} already exists."
                )
            }
        } catch (ex: IOException) {
            logger.log(Level.SEVERE, "Could not save ${outFile.name} to $outFile", ex)
        }
    }

    /**
     * (Borrowed from Bukkit's JavaPlugin)
     */
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

    /**
     * (Borrowed from Bukkit's JavaPlugin)
     */
    private fun getClassLoader(): ClassLoader = this.javaClass.classLoader
}
