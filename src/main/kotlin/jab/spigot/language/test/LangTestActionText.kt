package jab.spigot.language.test

import jab.spigot.language.LangPackage
import jab.spigot.language.`object`.ActionText
import jab.spigot.language.`object`.HoverText
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property player
 */
class LangTestActionText : LangTest("test_actiontext") {

    override fun run(pkg: LangPackage, player: Player): Boolean {
        val actionText = ActionText("Hover me.", HoverText())
        actionText.send(player, pkg)
        return true
    }
}