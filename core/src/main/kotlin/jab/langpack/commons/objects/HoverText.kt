package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * The **HoverText** class handles hover text that is displayed for ActionText instances. The
 *   HoverText supports dynamic text fields for processors.
 *
 * @author Jab
 *
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class HoverText: Complex<HoverEvent> {

    /**
     * The lines of text to display.
     */
    var lines: List<Text>

    /**
     * @param lines The lines of text to display.
     */
    constructor(lines: List<Text>) {
        this.lines = lines
    }

    /**
     * @param lines The lines of text to display.
     */
    constructor(vararg lines: Text) {
        val newLines = ArrayList<Text>()
        for (line in lines) {
            newLines.add(line)
        }
        this.lines = newLines
    }

    override fun process(pack: LangPack, lang: Language, vararg args: LangArg): HoverEvent {
        var array = emptyList<Text>()

        // Append all lines as one line with the [NEW_LINE] separator. The lang-pack will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            array = array.plus(Text(pack.processor.processString(line.value as String, pack, lang, *args)))
        }

        return HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
    }

    override fun get(): HoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, lines)
}
