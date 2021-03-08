package jab.langpack.spigot.loaders

import jab.langpack.commons.loader.ComplexLoader
import jab.langpack.spigot.objects.SpigotActionText
import org.bukkit.configuration.ConfigurationSection

/**
 * The **SpigotActionTextLoader** class loads [SpigotActionText] from YAML, overriding the assigned type *"action"*
 * for the ***Spigot*** environment.
 *
 * @author Jab
 */
class SpigotActionTextLoader : ComplexLoader<SpigotActionText> {

    override fun load(cfg: ConfigurationSection): SpigotActionText? {
        return SpigotActionText(cfg)
    }

}