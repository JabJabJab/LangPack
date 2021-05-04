@file:Suppress("MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.sponge.objects.complex

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.minecraft.commons.util.text.ClickEvent

/**
 * **CommandText** packages and processes text for [ClickEvent] for [SpongeActionText].
 *
 * @author Jab
 *
 * @property command The command to execute when clicked.
 */
class SpongeCommandText(val command: String) : Complex<ClickEvent> {

    override var definition: ComplexDefinition? = null

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): ClickEvent {
        val processed = pack.processor.process(command, pack, lang, context, *args)
        return ClickEvent(processed)
    }

    override fun walk(definition: LangDefinition<*>): SpongeCommandText = SpongeCommandText(definition.walk(command))

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(command)

    override fun get(): ClickEvent = ClickEvent(command)
}
