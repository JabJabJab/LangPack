package jab.langpack.spigot.loaders

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.spigot.objects.SpigotActionText
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
class SpigotActionTextLoader : ComplexLoader<SpigotActionText> {

    override fun load(cfg: ConfigurationSection): SpigotActionText? {
        return SpigotActionText(cfg)
    }

}