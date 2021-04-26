package jab.sledgehammer.langpack.sponge.util.text

import jab.sledgehammer.langpack.sponge.objects.complex.SpongeActionText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.TextActions

/**
 * **HoverEvent** is a "dummy-wrapper" solution for maintaining a consistency with solutions for
 * cross-server-platform support for [SpongeActionText] objects.
 *
 * @author Jab
 */
class HoverEvent {

    /**
     * TODO: Document.
     */
    val contents = ArrayList<String>()

    /**
     * TODO: Document.
     *
     * @param lines
     */
    constructor(lines: Array<String>) {
        for (line in lines) this.contents.add(line)
    }

    /**
     * TODO: Document.
     *
     * @param lines
     */
    constructor(lines: Collection<String>) {
        for (line in lines) this.contents.add(line)
    }

    /**
     * TODO: Document.
     *
     * @return
     */
    fun toAction(): HoverAction.ShowText {
        val builder = if (contents.isNotEmpty()) Text.builder(contents[0]) else Text.builder()
        if (contents.size > 1) {
            for (index in 1..contents.lastIndex) {
                builder.append(Text.NEW_LINE).append(Text.of(contents[index]))
            }
        }
        return TextActions.showText(builder.build())
    }
}
