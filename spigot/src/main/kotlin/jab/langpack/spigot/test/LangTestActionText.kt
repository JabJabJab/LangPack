package jab.langpack.spigot.test

import jab.langpack.commons.objects.HoverText
import jab.langpack.spigot.SpigotLangPack
import jab.langpack.spigot.objects.SpigotActionText
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 */
class LangTestActionText : LangTest("actiontext") {

    override fun run(pkg: SpigotLangPack, player: Player): TestResult {
        val actionText = SpigotActionText("Hover me.", HoverText(Text("This is hover text!")))
        actionText.send(player, pkg)
        return TestResult(true)
    }
}
