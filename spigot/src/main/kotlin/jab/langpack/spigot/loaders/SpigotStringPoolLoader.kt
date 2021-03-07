package jab.langpack.spigot.loaders

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.spigot.objects.SpigotStringPool
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
class SpigotStringPoolLoader : ComplexLoader<SpigotStringPool> {

    override fun load(cfg: ConfigurationSection): SpigotStringPool? {
        return SpigotStringPool(cfg)
    }
}
