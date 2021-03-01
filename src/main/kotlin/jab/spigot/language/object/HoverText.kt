package jab.spigot.language.`object`

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.Language
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * The <i>HoverText</i> class handles hover text that is displayed for ActionText instances. The
 *   HoverText supports dynamic text fields for LangPackages.
 *
 * @author Jab
 *
 * @property lines The lines of text to display.
 */
@Suppress("MemberVisibilityCanBePrivate")
class HoverText(var lines: List<Text>) {

    /**
     * TODO: Document.
     *
     * @param pkg
     * @param lang
     * @param args
     *
     * @return
     */
    fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): HoverEvent {
        var array = emptyList<Text>()

        // Append all lines as one line with the [NEW_LINE] separator. The LangPackage will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            array = array.plus(Text(pkg.processor.processString(line.value as String, pkg, lang, *args)))
        }

        return HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
    }

    /**
     * TODO: Document.
     *
     * @return
     */
    fun get(): HoverEvent {
        return HoverEvent(HoverEvent.Action.SHOW_TEXT, lines)
    }

}