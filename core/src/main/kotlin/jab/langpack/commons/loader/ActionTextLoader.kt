package jab.langpack.commons.loader

import jab.langpack.commons.objects.ActionText
import org.bukkit.configuration.ConfigurationSection

/**
 * The **ActionTextLoader** class loads [ActionText] from YAML with the assigned type *"action"*.
 *
 * @author Jab
 */
class ActionTextLoader : ComplexLoader<ActionText> {

    override fun load(cfg: ConfigurationSection): ActionText {
        return ActionText(cfg)
    }
}
