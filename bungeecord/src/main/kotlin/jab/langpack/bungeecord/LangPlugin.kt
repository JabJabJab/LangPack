package jab.langpack.bungeecord

import jab.langpack.bungeecord.loaders.BungeeActionTextLoader
import jab.langpack.bungeecord.loaders.BungeeStringPoolLoader
import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.commons.util.ResourceUtil
import net.md_5.bungee.api.plugin.Plugin
import java.io.*
import java.net.URL
import java.util.logging.Level

/**
 * The **LangPlugin** class TODO: Document.
 *
 * @author Jab
 */
internal class LangPlugin : Plugin() {

    /**
     * The default LangPack instance.
     */
    var pack: BungeeLangPack? = null

    override fun onEnable() {
        LangCfg(this)
        setBungeeLoaders()
        loadLangPackages()
        LangEventListener(this)
    }

    override fun onDisable() {
    }

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

    private fun getClassLoader(): ClassLoader {
        return this.javaClass.classLoader
    }

    private fun loadLangPackages() {

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        ResourceUtil.saveResource("lang/test_en.yml", true)
        ResourceUtil.saveResource("lang/test_jp.yml", true)

        pack = BungeeLangPack("lang")
        pack!!.load(save = true, force = true)
    }

    companion object {

        private fun setBungeeLoaders() {
            ComplexLoader.set("action", BungeeActionTextLoader())
            ComplexLoader.set("pool", BungeeStringPoolLoader())
        }
    }
}