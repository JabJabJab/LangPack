package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import net.md_5.bungee.api.chat.ClickEvent

/**
 * The **CommandText** class TODO: Document.
 *
 * @author Jab
 *
 * @property command
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandText(val command: String) {

    fun process(pkg: LangPack, lang: Language, vararg args: LangArg): ClickEvent {
        val processed = pkg.processor.processString(command, pkg, lang, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    fun get(): ClickEvent {
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
    }
}
