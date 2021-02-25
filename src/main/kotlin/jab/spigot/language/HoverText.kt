package jab.spigot.language

import jab.spigot.language.util.LangProcessable

/**
 * The <i>HoverText</i> class handles hover text that is displayed for ActionText instances. The
 *   HoverText supports dynamic text fields for LangPackages.
 *
 * @author Jab
 *
 * @param lines The lines of text to display.
 */
@Suppress("MemberVisibilityCanBePrivate")
class HoverText(var lines: Array<String>) : LangProcessable {

    override fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): String {
        val builder = StringBuilder()

        // Append all lines as one line with the [NEW_LINE] separator. The LangPackage will
        //   interpret the separator and handle this when displayed to the player.
        for (line in lines) {
            val processedLine = pkg.processor.process(line, pkg, lang, *args)
            if (builder.isEmpty()) {
                builder.append(processedLine)
            } else {
                builder.append(LangPackage.NEW_LINE).append(processedLine)
            }
        }

        return builder.toString()
    }

}