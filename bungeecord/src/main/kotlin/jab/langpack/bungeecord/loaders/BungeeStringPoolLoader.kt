package jab.langpack.bungeecord.loaders

import jab.langpack.bungeecord.objects.BungeeStringPool
import jab.langpack.commons.loader.ComplexLoader
import org.bukkit.configuration.ConfigurationSection

/**
 * The **BungeeStringPoolLoader** class loads [BungeeStringPool] from YAML, overriding the assigned type *"pool"*
 * for the ***BungeeCord*** environment.
 *
 * @author Jab
 */
class BungeeStringPoolLoader : ComplexLoader<BungeeStringPool> {

    override fun load(cfg: ConfigurationSection): BungeeStringPool {
        return BungeeStringPool(cfg)
    }
}