package jab.langpack.spigot.loaders

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.spigot.objects.SpigotStringPool
import org.bukkit.configuration.ConfigurationSection

/**
 * The **SpigotStringPoolLoader** class loads [SpigotStringPool] from YAML, overriding the assigned type *"pool"*
 * for the ***Spigot*** environment.
 *
 * @author Jab
 */
class SpigotStringPoolLoader : ComplexLoader<SpigotStringPool> {
    override fun load(cfg: ConfigurationSection): SpigotStringPool = SpigotStringPool(cfg)
}
