@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.textcomponent.objects.complex

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.util.StringUtil
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * **HoverText** handles dynamic text, processing into [HoverEvent].
 * Dynamic text fields are supported.
 *
 * @author Jab
 */
class TextComponentHoverText : Complex<HoverEvent> {

    override var definition: ComplexDefinition? = null

    /**
     * The lines of text to display.
     */
    var lines: List<Text>

    /**
     * Collection constructor.
     *
     * @param lines The lines of text to display.
     */
    constructor(lines: Collection<*>) {
        val packagedLines = ArrayList<Text>()
        for (line in lines) {
            if (line == null) {
                packagedLines.add(Text(" "))
            } else {
                if (line is Text) {
                    packagedLines.add(line)
                } else {
                    packagedLines.add(Text(StringUtil.toAString(line)))
                }
            }
        }
        this.lines = packagedLines
    }

    /**
     * Vararg constructor.
     *
     * @param lines The lines of text to display.
     */
    constructor(vararg lines: Text) {
        val newLines = ArrayList<Text>()
        for (line in lines) newLines.add(line)
        this.lines = newLines
    }

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): HoverEvent {
        var array = emptyList<Text>()
        // Append all lines as one line with the [NEW_LINE] separator. The lang-pack will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            array = array.plus(Text(pack.processor.process(line.value as String, pack, lang, context, *args)))
        }
        return HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
    }

    override fun walk(definition: LangDefinition<*>): TextComponentHoverText {
        val walkedLines = ArrayList<Text>()
        for (text in lines) walkedLines.add(Text(definition.walk(text.value.toString())))
        return TextComponentHoverText(walkedLines)
    }

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(lines)

    override fun get(): HoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, lines)
}
