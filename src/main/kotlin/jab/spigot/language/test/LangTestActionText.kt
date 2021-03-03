package jab.spigot.language.test

import jab.spigot.language.LangPackage
import jab.spigot.language.`object`.ActionText
import jab.spigot.language.`object`.HoverText
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 */
class LangTestActionText : LangTest("actiontext") {

    override fun run(pkg: LangPackage, player: Player): TestResult {
        val actionText = ActionText("Hover me.", HoverText(Text("This is hover text!")))
        actionText.send(player, pkg)
        return TestResult(true)
    }
}