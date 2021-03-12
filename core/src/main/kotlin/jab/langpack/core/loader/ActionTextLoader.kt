package jab.langpack.core.loader

import jab.langpack.core.objects.ActionText
import org.bukkit.configuration.ConfigurationSection

/**
 * The **ActionTextLoader** class loads [ActionText] from YAML with the assigned type *"action"*.
 *
 * @author Jab
 */
class ActionTextLoader : ComplexLoader<ActionText> {
    override fun load(cfg: ConfigurationSection): ActionText = ActionText(cfg)
}
