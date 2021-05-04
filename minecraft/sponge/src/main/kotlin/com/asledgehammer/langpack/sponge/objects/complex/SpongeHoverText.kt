@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.sponge.objects.complex

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.util.StringUtil
import com.asledgehammer.langpack.minecraft.commons.util.text.HoverEvent

/**
 * **HoverText** handles dynamic text, processing into [HoverEvent].
 * Dynamic text fields are supported.
 *
 * @author Jab
 */
class SpongeHoverText : Complex<HoverEvent> {

    override var definition: ComplexDefinition? = null

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

    override fun walk(definition: LangDefinition<*>): SpongeHoverText {
        val walkedLines = ArrayList<String>()
        for (text in lines) {
            walkedLines.add(definition.walk(text))
        }
        return SpongeHoverText(walkedLines)
    }

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(lines)

    override fun get(): HoverEvent = HoverEvent(lines)
}
