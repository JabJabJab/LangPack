@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.sponge.objects.complex

import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangGroup
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.definition.LangDefinition
import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.util.StringUtil
import jab.sledgehammer.langpack.sponge.util.text.HoverEvent

/**
 * **HoverText** handles dynamic text, processing into [HoverEvent].
 * Dynamic text fields are supported.
 *
 * @author Jab
 */
class HoverText : Complex<HoverEvent> {

    /**
     * The lines of text to display.
     */
    var lines: List<String>

    /**
     * Collection constructor.
     *
     * @param lines The lines of text to display.
     */
    constructor(lines: Collection<*>) {
        val packagedLines = ArrayList<String>()
        for (line in lines) {
            if (line == null) {
                packagedLines.add(" ")
            } else {
                packagedLines.add(StringUtil.toAString(line))
            }
        }
        this.lines = packagedLines
    }

    /**
     * Vararg constructor.
     *
     * @param lines The lines of text to display.
     */
    constructor(vararg lines: String) {
        val newLines = ArrayList<String>()
        for (line in lines) {
            newLines.add(line)
        }
        this.lines = newLines
    }

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): HoverEvent {
        var array = emptyList<String>()
        // Append all lines as one line with the [NEW_LINE] separator. The lang-pack will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            array = array.plus(pack.processor.process(line, pack, lang, context, *args))
        }
        return HoverEvent(array)
    }

    override fun walk(definition: LangDefinition<*>): HoverText {
        val walkedLines = ArrayList<String>()
        for (text in lines) {
            walkedLines.add(definition.walk(text))
        }
        return HoverText(walkedLines)
    }

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(lines)

    override fun get(): HoverEvent = HoverEvent(lines)
}
