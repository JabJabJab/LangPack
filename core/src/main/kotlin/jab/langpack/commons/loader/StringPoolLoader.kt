package jab.langpack.commons.loader

import jab.langpack.commons.objects.StringPool
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
class StringPoolLoader : ComplexLoader<StringPool> {

    override fun load(cfg: ConfigurationSection): StringPool? {
        return StringPool(cfg)
    }
}