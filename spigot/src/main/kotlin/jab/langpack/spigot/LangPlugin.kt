package jab.langpack.spigot

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.commons.util.ResourceUtil
import jab.langpack.spigot.loaders.SpigotActionTextLoader
import jab.langpack.spigot.loaders.SpigotStringPoolLoader
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import java.io.File

/**
 * The **LangPlugin** class TODO: Document.
 *
 * @author Jab
 */
internal class LangPlugin : JavaPlugin(), Listener {

    var pack: SpigotLangPack? = null

    override fun onEnable() {

        instance = this

        setSpigotLoaders()
        LangCfg(this)
        loadLangPackages()
        LangEventListener(this)
        LangCommand(this)
    }

    override fun onDisable() {
        pack = null
    }

    private fun loadLangPackages() {

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        ResourceUtil.saveResource("lang/test_en.yml", true)
        ResourceUtil.saveResource("lang/test_jp.yml", true)

        pack = SpigotLangPack("lang")
        pack!!.load(save = true, force = true)
    }

    companion object {

        var instance: LangPlugin? = null
            private set

        fun setSpigotLoaders() {
            ComplexLoader.set("action", SpigotActionTextLoader())
            ComplexLoader.set("pool", SpigotStringPoolLoader())
        }
    }
}
