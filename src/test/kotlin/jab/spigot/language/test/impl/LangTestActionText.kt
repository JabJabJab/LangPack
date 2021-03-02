package jab.spigot.language.test.impl

import jab.spigot.language.LangPackage
import jab.spigot.language.`object`.ActionText
import jab.spigot.language.`object`.HoverText
import jab.spigot.language.test.LangTest
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property player
 */
class LangTestActionText(private val player: Player) : LangTest("test_actiontext") {

    override fun run(pkg: LangPackage): Boolean {
        val actionText = ActionText("Hover me.", HoverText())
        actionText.send(player, pkg)
        return true
    }
}