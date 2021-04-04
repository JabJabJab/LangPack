@file:Suppress("MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.textcomponent.objects.complex

import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangGroup
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.definition.LangDefinition
import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import net.md_5.bungee.api.chat.ClickEvent

/**
 * **CommandText** packages and processes text for [ClickEvent] for [ActionText].
 *
 * @author Jab
 *
 * @property command The command to execute when clicked.
 */
class CommandText(val command: String) : Complex<ClickEvent> {

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): ClickEvent {
        val processed = pack.processor.process(command, pack, lang, context, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    override fun walk(definition: LangDefinition<*>): CommandText = CommandText(definition.walk(command))

    override fun needsWalk(formatter: FieldFormatter): Boolean = formatter.needsWalk(command)

    override fun get(): ClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
}
