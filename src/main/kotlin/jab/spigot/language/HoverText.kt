package jab.spigot.language

import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

/**
 * The <i>HoverText</i> class handles hover text that is displayed for ActionText instances. The
 *   HoverText supports dynamic text fields for LangPackages.
 *
 * @author Jab
 *
 * @param lines The lines of text to display.
 */
@Suppress("MemberVisibilityCanBePrivate")
class HoverText(var lines: Array<TextComponent>) {

    fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): HoverEvent {
        var array = emptyArray<TextComponent>()

        // Append all lines as one line with the [NEW_LINE] separator. The LangPackage will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            array = array.plus(TextComponent(pkg.processor.processString(line.text, pkg, lang, *args)))
        }

        return HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
    }

}