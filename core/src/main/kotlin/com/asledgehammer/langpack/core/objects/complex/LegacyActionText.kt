@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.core.objects.complex

import com.asledgehammer.config.ConfigSection
import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter

/**
 * **ActionText** provides legacy support for situations that does not have the
 * ability to utilize the CommandText and HoverText components.
 *
 * @author Jab
 *
 * @param text
 */
open class LegacyActionText(val text: String) : Complex<String> {

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: ConfigSection) : this(cfg.getString("text"))

    override fun walk(definition: LangDefinition<*>): Complex<String> = LegacyActionText(definition.walk(text))

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): String =
        pack.processor.process(text, pack, lang, *args)

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(text)

    override fun get(): String = text

    /**
     * **LegacyActionText.Loader** provides a loader for [LegacyActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<LegacyActionText> {
        override fun load(cfg: ConfigSection): LegacyActionText = LegacyActionText(cfg)
    }
}
