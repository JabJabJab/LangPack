@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.bukkit.objects.complex

import jab.sledgehammer.config.ConfigSection
import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangGroup
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.definition.LangDefinition
import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter

/**
 * **BukkitActionText** replaces the ActionText class to provide legacy support for the Bukkit API.
 *
 * @author Jab
 */
class BukkitActionText(val text: String) : Complex<String> {

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: ConfigSection) : this(cfg.getString("text"))

    override fun walk(definition: LangDefinition<*>): Complex<String> = BukkitActionText(definition.walk(text))

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): String =
        pack.processor.process(text, pack, lang, *args)


    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(text)

    override fun get(): String = text

    /**
     * **BukkitActionText.Loader** overrides ActionText with [BukkitActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BukkitActionText> {
        override fun load(cfg: ConfigSection): BukkitActionText = BukkitActionText(cfg)
    }
}
