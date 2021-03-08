package jab.langpack.bungeecord.loaders

import jab.langpack.bungeecord.objects.BungeeActionText
import jab.langpack.commons.loader.ComplexLoader
import org.bukkit.configuration.ConfigurationSection

/**
 * The **BungeeStringPoolLoader** class loads [BungeeActionText] from YAML, overriding the assigned type *"action"*
 * for the ***BungeeCord*** environment.
 *
 * @author Jab
 */
class BungeeActionTextLoader : ComplexLoader<BungeeActionText> {
    override fun load(cfg: ConfigurationSection): BungeeActionText = BungeeActionText(cfg)
}