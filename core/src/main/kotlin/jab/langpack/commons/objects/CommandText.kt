package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import net.md_5.bungee.api.chat.ClickEvent

/**
 * The **CommandText** class packages and processes text for [ClickEvent] in processed [ActionText].
 *
 * @author Jab
 *
 * @property command The command to execute when clicked.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandText(val command: String): Complex<ClickEvent> {

    override fun process(pack: LangPack, lang: Language, vararg args: LangArg): ClickEvent {
        val processed = pack.processor.processString(command, pack, lang, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    override fun get(): ClickEvent {
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
    }
}
