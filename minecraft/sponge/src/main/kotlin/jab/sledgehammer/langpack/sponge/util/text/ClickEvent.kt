package jab.sledgehammer.langpack.sponge.util.text

import jab.sledgehammer.langpack.sponge.objects.complex.ActionText
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.TextActions

/**
 * **ClickEvent** is a "dummy-wrapper" solution for maintaining a consistency with solutions for
 * cross-server-platform support for [ActionText] objects.
 *
 * @author Jab
 */
class ClickEvent(var value: String) {
    fun toAction(): ClickAction.RunCommand = TextActions.runCommand(value)
}
