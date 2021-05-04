package com.asledgehammer.langpack.sponge.util.text

import com.asledgehammer.langpack.minecraft.commons.util.text.ClickEvent
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.TextActions

/**
 * @return Returns the ClickEvent as a Sponge object.
 */
fun ClickEvent.toAction(): ClickAction.RunCommand = TextActions.runCommand(value)
