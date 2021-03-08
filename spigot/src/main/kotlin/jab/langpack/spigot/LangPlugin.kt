package jab.langpack.spigot

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.spigot.loaders.SpigotActionTextLoader
import jab.langpack.spigot.loaders.SpigotStringPoolLoader
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
    var pack: SpigotLangPack? = null

    override fun onEnable() {

        instance = this

        setSpigotLoaders()
        LangCfg(this)
        loadLangPacks()
        LangEventListener(this)
        LangCommand(this)
    }

    override fun onDisable() {
        pack = null
    }

    private fun loadLangPacks() {

        val langDir = File(dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        pack = SpigotLangPack("lang")
        pack!!.load(save = true)
        pack!!.append("test", save = true)
    }

    companion object {

        var instance: LangPlugin? = null
            private set

        /**
         * Overrides the generic loaders with spigot-wrappers to add functionality specific to the Spigot environment.
         */
        fun setSpigotLoaders() {
            ComplexLoader.set("action", SpigotActionTextLoader())
            ComplexLoader.set("pool", SpigotStringPoolLoader())
        }
    }
}
