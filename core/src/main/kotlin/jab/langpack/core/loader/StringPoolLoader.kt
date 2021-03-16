package jab.langpack.core.loader

import jab.langpack.core.objects.complex.StringPool
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **StringPoolLoader** class loads [StringPool] from YAML with the assigned type *"pool"*.
 *
 * @author Jab
 */
class StringPoolLoader : ComplexLoader<StringPool> {
    override fun load(cfg: ConfigurationSection): StringPool = StringPool(cfg)
}
