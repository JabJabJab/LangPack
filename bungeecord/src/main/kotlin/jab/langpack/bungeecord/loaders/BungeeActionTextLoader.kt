package jab.langpack.bungeecord.loaders

import jab.langpack.bungeecord.objects.BungeeActionText
import jab.langpack.commons.loader.ComplexLoader
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
class BungeeActionTextLoader : ComplexLoader<BungeeActionText> {

    override fun load(cfg: ConfigurationSection): BungeeActionText? {
        return BungeeActionText(cfg)
    }
}