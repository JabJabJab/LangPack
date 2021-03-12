package jab.langpack.spigot.test

import jab.langpack.core.LangPack
import jab.langpack.core.objects.ActionText
import jab.langpack.core.objects.HoverText
import jab.langpack.spigot.send
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

/**
 * The **LangTestActionText** class Tests the basic operations for [ActionText].
 *
 * @author Jab
 */
class LangTestActionText : LangTest<Player>("actiontext") {

    override fun run(pack: LangPack, player: Player): TestResult {
        val actionText = ActionText("Hover me.", HoverText(Text("This is hover text!")))
        actionText.send(player, pack)
        return TestResult(true)
    }
}
