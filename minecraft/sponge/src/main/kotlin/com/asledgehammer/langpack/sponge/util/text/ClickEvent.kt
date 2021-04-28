package com.asledgehammer.langpack.sponge.util.text

import com.asledgehammer.langpack.sponge.objects.complex.SpongeActionText
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.TextActions

/**
 * **ClickEvent** is a "dummy-wrapper" solution for maintaining a consistency with solutions for
 * cross-server-platform support for [SpongeActionText] objects.
 *
 * @author Jab
 */
class ClickEvent(var value: String) {

    /**
     * TODO: Document.
     */
    fun toAction(): ClickAction.RunCommand = TextActions.runCommand(value)
}
