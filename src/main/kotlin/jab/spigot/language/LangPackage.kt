package jab.spigot.language

import jab.spigot.language.util.IStringProcessor
import jab.spigot.language.util.StringProcessor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.io.File
import java.util.*

/**
 * LangPackage is a utility that provides the ability to substitute sections of a string
 * recursively. This allows for Strings to be dynamically edited, and defined anywhere within the
 * String to be injected with EntryFields. Adding to this is the ability to select what Language to
 * choose from, falling back to English if not defined.
 *
 * TODO: Cache system.
 * TODO: Document.
 *
 * @author Jab
 *
 * @param dir The File Object for the directory where the LangFiles are stored.
 * @param name The String name of the LanguagePackage. This is noted in the LanguageFiles as
 *      "{{name}}_{{language_abbreviation}}.yml"
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LangPackage(val dir: File, val name: String) {

    /** Handles processing of texts for the LanguageFile. */
    var processor: IStringProcessor = StringProcessor()

    /** The language file to default to if a raw string cannot be located with another language. */
    private var defaultLanguage: Language = Language.ENGLISH

    /** The Map for LanguageFiles, assigned with their Languages. */
    private val mapLanguageFiles: EnumMap<Language, LangFile> = EnumMap(Language::class.java)

    init {
        if (dir.exists()) {
            throw IllegalArgumentException("""The directory "$dir" doesn't exist.""")
        } else if (!dir.isDirectory) {
            throw IllegalArgumentException("""The path "$dir" is not a valid directory.""")
        }
        if (name.isEmpty()) {
            throw IllegalArgumentException("""The name "$name" is empty.""")
        }
    }

    /**
     * Reads and loads the LangPackage.
     */
    fun load() {
        append(name)
    }

    /**
     * Appends a language package.
     *
     * @param name The name of the package to append.
     */
    fun append(name: String) {
        for (file in dir.listFiles()!!) {
            if (file.nameWithoutExtension.equals(name, true)
                && file.extension.equals("yml", true)
            ) {
                val langAbbrev = file.nameWithoutExtension.split("_")[1]
                val language = Language.getLanguageAbbrev(langAbbrev)
                if (language == null) {
                    System.err.println("""The file "${file.name}" does not have a valid language abbreviation.""")
                    continue
                }
                val langFile = LangFile(file, language)
                langFile.load()
                mapLanguageFiles[language] = langFile
            }
        }
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     *
     * @return
     */
    fun getList(field: String, lang: Language? = defaultLanguage, vararg fields: LangField): ArrayList<String?>? {
        var llang = lang
        if (llang == null) {
            llang = defaultLanguage
        }
        val rawList = getRawList(field, llang)
        if (rawList != null) {
            val processedList = ArrayList<String>()
            for (raw in rawList) {
                if (raw != null) {
                    processedList.add(processor.process(raw, this, llang, *fields))
                } else {
                    processedList.add(null.toString())
                }
            }
        }
        return null
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     *
     * @return
     */
    fun get(field: String, lang: Language? = defaultLanguage, vararg fields: LangField): String? {
        var llang = lang
        if (llang == null) {
            llang = defaultLanguage
        }
        val rawText = getRaw(field, llang)
        return if (rawText != null) {
            processor.process(rawText, this, llang, *fields)
        } else {
            null
        }
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     *
     * @return
     */
    fun getRawList(field: String, lang: Language): ArrayList<String?>? {
        val rawText = getRaw(field, lang)
        if (rawText != null) {
            return toAList(rawText)
        } else {
            return null
        }
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     *
     * @return
     */
    fun getRaw(field: String, lang: Language): String? {
        val langFile = mapLanguageFiles[lang]
        var raw: String? = null

        if (langFile != null) {
            raw = langFile.get(field)
            if (raw == null) {
                // Check thew fallback language. (If set)
                val fallbackLang = lang.getFallback()
                if (fallbackLang != null) {
                    val fallbackLangFile = mapLanguageFiles[fallbackLang]
                    raw = fallbackLangFile?.get(field)
                }
            }
        } else {
            // Check the fallback language. (If set)
            val fallbackLang = lang.getFallback()
            if (fallbackLang != null) {
                raw = mapLanguageFiles[fallbackLang]?.get(field)
            }
        }

        // Check default language set by the package.
        if (raw == null && lang != defaultLanguage) {
            raw = mapLanguageFiles[defaultLanguage]?.get(field)
        }

        return raw
    }

    companion object {

        /** The standard 'line.separator' for most Java Strings. */
        private const val NEW_LINE: String = "\n"

        var DEFAULT_RANDOM: Random = Random()

        /**
         * Converts any object given to a string. Lists are compacted into one String using [NEW_LINE] as a separator.
         *
         * @param value The value to process to a String.
         *
         * @return Returns the result String.
         */
        fun toAString(value: Any): String {
            return if (value is List<*>) {
                val builder: StringBuilder = StringBuilder()
                for (next in value) {
                    val line = value.toString()
                    if (builder.isEmpty()) {
                        builder.append(line)
                    } else {
                        builder.append(NEW_LINE).append(line)
                    }
                }
                builder.toString()
            } else {
                value.toString()
            }
        }

        /**
         * TODO: Document.
         *
         * @param text
         * @param command
         */
        fun createCommandComponent(text: String, command: String): TextComponent {
            val component = TextComponent(text)
            component.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
            return component
        }

        /**
         * TODO: Document.
         *
         * @param text
         * @param lines
         */
        @Suppress("DEPRECATION")
        fun createHoverComponent(text: String, lines: Array<String>): TextComponent {
            val component = TextComponent(text)

            var list: Array<TextComponent> = emptyArray()
            for (arg in lines) {
                list = list.plus(TextComponent(arg))
            }

            component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, list)
            return component
        }

        /**
         * @param value The value to partition as a string with the [NEW_LINE] operator.
         *
         * @return Returns a List of Strings, partitioned by the [NEW_LINE] operator.
         */
        fun toAList(value: Any): ArrayList<String?> {
            return ArrayList(value.toString().split(NEW_LINE))
        }

        /**
         * Converts a List of Strings to a String Array.
         *
         * @param list The List to convert.
         * @return Returns a String Array of the String Lines in the List provided.
         */
        fun toAStringArray(list: List<String>): Array<String> {
            var array: Array<String> = emptyArray()
            for (next in list) {
                array = array.plus(next)
            }
            return array
        }

        /**
         * TODO: Document.
         *
         * @param strings
         */
        fun color(strings: List<String>): List<String> {
            val coloredList = ArrayList<String>()
            for (string in strings) {
                coloredList.add(color(string))
            }
            return coloredList
        }

        /**
         * TODO: Document.
         *
         * @param string
         */
        fun color(string: String): String {
            return ChatColor.translateAlternateColorCodes('&', string)
        }

        /**
         * TODO: Document.
         *
         * @param sender
         * @param lines
         */
        fun message(sender: CommandSender, lines: Array<String?>) {
            if (lines.isEmpty()) {
                return
            }
            sender.sendMessage(lines)
        }

        /**
         * TODO: Document.
         *
         * @param sender
         * @param lines
         */
        fun message(sender: CommandSender, lines: List<String?>) {
            for (line in lines) {
                if (line != null) {
                    sender.sendMessage(line)
                }
            }
        }

        /**
         * TODO: Document.
         *
         * @param lines
         */
        fun broadcast(lines: Array<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * TODO: Document.
         *
         * @param lines
         */
        fun broadcast(lines: List<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * TODO: Document.
         *
         * @param lines
         */
        fun broadcastNulls(lines: Array<String?>) {
            for (line in lines) {
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
            }
        }

        /**
         * TODO: Document.
         *
         * @param lines
         */
        fun broadcastNulls(lines: List<String?>) {
            for (line in lines) {
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
            }
        }
    }
}