package jab.langpack.commons.loader

import jab.langpack.commons.objects.ActionText
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
class ActionTextLoader : ComplexLoader<ActionText> {

    override fun load(cfg: ConfigurationSection): ActionText? {
        return ActionText(cfg)
    }

}