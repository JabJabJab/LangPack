package jab.langpack.spigot.test

import jab.langpack.commons.objects.ActionText
import jab.langpack.commons.objects.HoverText
import jab.langpack.spigot.SpigotLangPack
import jab.langpack.spigot.objects.send
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

/**
 * The **LangTestActionText** class Tests the basic operations for [ActionText].
 *
 * @author Jab
 */
class LangTestActionText : LangTest("actiontext") {

    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        val actionText = ActionText("Hover me.", HoverText(Text("This is hover text!")))
        actionText.send(player, pack)
        return TestResult(true)
    }
}
