@file:Suppress("MemberVisibilityCanBePrivate")

package jab.langpack.core.objects.complex

import jab.langpack.core.objects.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.definition.Definition
import jab.langpack.core.processor.FieldFormatter
import net.md_5.bungee.api.chat.ClickEvent

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **CommandText** class packages and processes text for [ClickEvent] in processed [ActionText].
 *
 * @author Jab
 *
 * @property command The command to execute when clicked.
 */
class CommandText(val command: String) : Complex<ClickEvent> {

    override fun process(pack: LangPack, lang: Language, vararg args: LangArg): ClickEvent {
        val processed = pack.processor.process(command, pack, lang, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    override fun walk(definition: Definition<*>): CommandText = CommandText(definition.walk(command))

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(command)

    override fun get(): ClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)

}
