package com.asledgehammer.langpack.sponge.util.text

import com.asledgehammer.langpack.minecraft.commons.util.text.HoverEvent
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.TextActions

/**
 * TODO: Document.
 *
 * @return
 */
fun HoverEvent.toAction(): HoverAction.ShowText {
    val builder = if (contents.isNotEmpty()) Text.builder(contents[0]) else Text.builder()
    if (contents.size > 1) {
        for (index in 1..contents.lastIndex) {
            builder.append(Text.NEW_LINE).append(Text.of(contents[index]))
        }
    }
    return TextActions.showText(builder.build())
}