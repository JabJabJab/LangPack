package jab.spigot.language

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import java.io.File

/**
 * Dummy plug-in class to allow for independent loading of library as a plug-in for multi-plugin use.
 *
 * TODO: Document.
 *
 * @author Jab
 */
class LangPlugin : JavaPlugin(), Listener {

    var lang: LangPackage? = null

    override fun onEnable() {
        instance = this

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        saveResource("lang/test_en.yml", true)
        saveResource("lang/test_jp.yml", true)

        lang = LangPackage(langDir, "test")
        lang!!.load()

        LangEventListener(this)
        LangCommand(this)
    }

    override fun onDisable() {
        lang = null
    }

    companion object {
        var instance: LangPlugin? = null
    }
}