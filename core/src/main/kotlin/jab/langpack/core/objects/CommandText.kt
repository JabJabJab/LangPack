package jab.langpack.core.objects

import jab.langpack.core.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import net.md_5.bungee.api.chat.ClickEvent

/**
 * The **CommandText** class packages and processes text for [ClickEvent] in processed [ActionText].
 *
 * @author Jab
 *
 * @property command The command to execute when clicked.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandText(val command: String) : Complex<ClickEvent> {

    override fun walk(definition: Definition<*>): CommandText {
        // TODO: Implement.
        return this
    }

    override fun process(pack: LangPack, lang: Language, vararg args: LangArg): ClickEvent {
        val processed = pack.processor.process(command, pack, lang, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    override fun get(): ClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)

    override fun needsWalk(): Boolean {
        // TODO: Implement.
        return false
    }
}
