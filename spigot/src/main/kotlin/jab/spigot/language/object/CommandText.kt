package jab.spigot.language.`object`

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.Language
import net.md_5.bungee.api.chat.ClickEvent

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property command
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandText(val command: String) {

    fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): ClickEvent {
        val processed = pkg.processor.processString(command, pkg, lang, *args)
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, processed)
    }

    fun get(): ClickEvent {
        return ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
    }
}
