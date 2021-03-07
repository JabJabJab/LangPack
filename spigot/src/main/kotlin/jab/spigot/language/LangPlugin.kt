package jab.spigot.language

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import java.io.File

/**
 * TODO: Document.
 *
 * @author Jab
 */
internal class LangPlugin : JavaPlugin(), Listener {

    var lang: LangPackage? = null

    override fun onEnable() {
        instance = this
        LangCfg(this)
        loadLangPackages()
        LangEventListener(this)
        LangCommand(this)
    }

    override fun onDisable() {
        lang = null
    }

    private fun loadLangPackages() {
        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        lang = LangPackage("lang")
            .load(save = true, force = true)
            .append("test", save = true, force = true)
    }

    companion object {
        var instance: LangPlugin? = null
            private set
    }
}
