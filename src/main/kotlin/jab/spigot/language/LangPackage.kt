package jab.spigot.language

import jab.spigot.language.util.*
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.*
import java.net.URL
import java.util.*

/**
 * LangPackage is a utility that provides the ability to substitute sections of a string
 * recursively. This allows for Strings to be dynamically edited, and defined anywhere within the
 * String to be injected with EntryFields. Adding to this is the ability to select what Language to
 * choose from, falling back to English if not defined.
 *
 * TODO: Document.
 *
 * @author Jab
 *
 * @param dir The File Object for the directory where the LangFiles are stored.
 * @param name The String name of the LanguagePackage. This is noted in the LanguageFiles as
 *      "{{name}}_{{language_abbreviation}}.yml"
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 *
 * @property dir The directory of the folder storing the lang files.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LangPackage(val dir: File, val name: String) {

    /** Handles processing of texts for the LanguageFile. */
    var processor: StringProcessor = PercentStringProcessor()

    /** The language file to default to if a raw string cannot be located with another language. */
    var defaultLang: Language = Language.ENGLISH

    /** The Map for LanguageFiles, assigned with their Languages. */
    private val files: EnumMap<Language, LangFile> = EnumMap(Language::class.java)

    init {
        if (!dir.exists()) {
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
            if (file.nameWithoutExtension.startsWith(name, true)
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

                files[language] = langFile
            }
        }
    }

    /**
     * Sets a value for a language.
     *
     * @param lang The language to set.
     * @param field The field to set.
     * @param value The value to set.
     */
    fun set(lang: Language, field: String, value: Any?) {
        val file: LangFile = files.computeIfAbsent(lang) { LangFile(lang) }
        file.set(field, value)
    }

    /**
     * Sets a value for the language.
     *
     * @param lang The language to set.
     * @param fields The fields to set.
     */
    fun set(lang: Language, vararg fields: LangArg) {
        // Make sure that we have fields to set.
        if (fields.isEmpty()) {
            return
        }

        val file: LangFile = files.computeIfAbsent(lang) { LangFile(lang) }
        for (field in fields) {
            file.set(field.key, field.value)
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
    fun getList(field: String, lang: Language? = defaultLang, vararg args: LangArg): List<String>? {

        var llang = lang
        if (llang == null) {
            llang = defaultLang
        }

        val string = getString(field, lang, *args) ?: return null
        val rawList = toAList(string)
        val processedList = ArrayList<String>()
        for (raw in rawList) {
            if (raw != null) {
                processedList.add(processor.processString(raw, this, llang, *args))
            } else {
                processedList.add("")
            }
        }
        return processedList
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     *
     * @return
     */
    fun getString(field: String, lang: Language? = defaultLang, vararg args: LangArg): String? {

        var llang = lang
        if (llang == null) {
            llang = defaultLang
        }

        val raw = getRaw(field, llang)
        return if (raw != null) {
            when (raw) {
                is LangComponent -> {
                    raw.process(this, llang, *args).toPlainText()
                }
                is LangComplex -> {
                    raw.process(this, llang, *args)
                }
                else -> {
                    processor.processString(raw.toString(), this, llang, *args)
                }
            }
        } else {
            return null
        }
    }

    /**
     * TODO: Document.
     *
     * @param field
     * @param lang
     */
    fun getRaw(field: String, lang: Language): Any? {
        val langFile = files[lang]
        var raw: Any? = null

        if (langFile != null) {
            raw = langFile.get(field)
            if (raw == null) {
                // Check thew fallback language. (If set)
                val fallbackLang = lang.getFallback()
                if (fallbackLang != null) {
                    val fallbackLangFile = files[fallbackLang]
                    raw = fallbackLangFile?.get(field)
                }
            }
        } else {
            // Check the fallback language. (If set)
            val fallbackLang = lang.getFallback()
            if (fallbackLang != null) {
                raw = files[fallbackLang]?.get(field)
            }
        }

        // Check default language set by the package.
        if (raw == null && lang != defaultLang) {
            raw = files[defaultLang]?.get(field)
        }

        // Check global last.
        if (raw == null && this != global) {
            raw = global.getRaw(field, lang)
        }

        return raw
    }

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
     * @param field The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcastField(field: String, vararg args: LangArg) {

        val cache: EnumMap<Language, String> = EnumMap(Language::class.java)
        val cacheComponent: EnumMap<Language, TextComponent> = EnumMap(Language::class.java)

        for (player in Bukkit.getOnlinePlayers()) {

            // Grab the players language, else fallback to default.
            var lang = Language.getLanguageAbbrev(player.locale)
            if (lang == null) {
                lang = defaultLang
            }

            // If the dialog for the language has alredy been rendered, use the cache.
            if (cache.containsKey(lang)) {
                player.sendMessage(cache[lang]!!)
                continue
            } else if (cacheComponent.containsKey(lang)) {
                player.spigot().sendMessage(cacheComponent[lang]!!)
                continue
            }

            if (isLangComponent(lang, field)) {
                val message: LangComponent = getRaw(field, lang) as LangComponent
                player.spigot().sendMessage(message.process(this, lang, *args))

                // Set the language result in the cache to avoid wasted calculations.
                cacheComponent[lang] = message.process(this, lang, *args)
            } else {
                // Grab the message and send it through Bukkit.
                val message = getString(field, lang, *args)
                if (message != null) {
                    player.sendMessage(message)
                }

                // Set the language result in the cache to avoid wasted calculations.
                cache[lang] = message
            }
        }
    }

    /**
     * Messages a player with a given field and arguments. The language will be based on [Player.getLocale].
     *   If the language is not supported, [LangPackage.defaultLang] will be used.
     *
     * @param player The player to send the message.
     * @param field The field to send.
     * @param args Additional arguments to apply.
     */
    fun messageField(player: Player, field: String, vararg args: LangArg) {

        // Grab the players language, else fallback to default.
        val lang = Language.getLanguage(player, defaultLang)
        val value = getRaw(field, lang) ?: return


        val component: TextComponent = when (value) {
            is LangComponent -> {
                value.get()
            }
            is LangComplex -> {
                TextComponent(value.get())
            }
            is TextComponent -> {
                value
            }
            else -> {
                TextComponent(value.toString())
            }
        }

        val result = processor.processComponent(component, this, lang, *args)

        player.spigot().sendMessage(result)
    }

    /**
     * TODO: Document.
     *
     * @param lang
     * @param field
     *
     * @return
     */
    fun contains(lang: Language, field: String): Boolean {
        return files[lang]?.contains(field.toLowerCase()) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param field The field to test.
     *
     * @return Returns true if the field for the language stores a [LangComplex] object.
     */
    fun isComplex(lang: Language, field: String): Boolean {
        return files[lang]?.isComplex(field) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param field The field to test.
     *
     * @return Returns true if the field for the language stores a component-based value.
     */
    fun isLangComponent(lang: Language, field: String): Boolean {
        return files[lang]?.isLangComponent(field) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param field The field to test.
     *
     * @return Returns true if the field for the language stores a [StringPool].
     */
    fun isStringPool(lang: Language, field: String): Boolean {
        return files[lang]?.isStringPool(field) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param field The field to test.
     *
     * @return Returns true if the field for the language stores a [ActionText].
     */
    fun isActionText(lang: Language, field: String): Boolean {
        return files[lang]?.isActionText(field) ?: false
    }

    companion object {

        val global: LangPackage

        val GLOBAL_DIRECTORY: File = File("lang")

        /** The standard 'line.separator' for most Java Strings. */
        const val NEW_LINE: String = "\n"

        var DEFAULT_RANDOM: Random = Random()

        init {
            // The global 'lang' directory.
            if (!GLOBAL_DIRECTORY.exists()) {
                GLOBAL_DIRECTORY.mkdirs()
            }

            // Store all global lang files present in the jar.
            for (lang in Language.values()) {
                saveResource("lang${File.separator}global_${lang.abbreviation}.yml")
            }

            global = LangPackage(File("lang"), "global")
            global.load()
        }

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
         * Creates a component with a [ClickEvent] for firing a command.
         *
         * @param text The text to display.
         * @param command The command to execute when clicked.
         *
         * @return Returns a text component with a click event for executing the command.
         */
        fun createCommandComponent(text: String, command: String): TextComponent {
            val component = TextComponent(text)
            component.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
            return component
        }

        /**
         * Creates a component with a [HoverEvent] for displaying lines of text.
         *
         * @param text The text to display.
         * @param lines The lines of text to display when the text is hovered by a mouse.
         */
        fun createHoverComponent(text: String, lines: Array<String>): TextComponent {
            val component = TextComponent(text)

            var list: Array<TextComponent> = emptyArray()
            for (arg in lines) {
                list = list.plus(TextComponent(arg))
            }

            @Suppress("DEPRECATION")
            component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, list)
            return component
        }

        /**
         * Creates a component with a [HoverEvent] for displaying lines of text.
         *
         * @param text The text to display.
         * @param lines The lines of text to display when the text is hovered by a mouse.
         */
        @Suppress("DEPRECATION")
        fun createHoverComponent(text: String, lines: List<String>): TextComponent {
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
        fun toAList(value: Any): List<String?> {
            val string = value.toString()
            return if (string.contains(NEW_LINE)) {
                string.split(NEW_LINE)
            } else {
                listOf(string)
            }
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
         * Colors a list of strings to the Minecraft color-code specifications using an alternative color-code.
         *
         * @param strings The strings to color.
         * @param colorCode (Default: '&') The alternative color-code to process.
         */
        fun color(strings: List<String>, colorCode: Char = '&'): List<String> {
            val coloredList = ArrayList<String>()
            for (string in strings) {
                coloredList.add(color(string, colorCode))
            }
            return coloredList
        }

        /**
         * Colors a string to the Minecraft color-code specifications using an alternative color-code.
         *
         * @param string The string to color.
         * @param colorCode (Default: '&') The alternative color-code to process.
         */
        fun color(string: String, colorCode: Char = '&'): String {
            return ChatColor.translateAlternateColorCodes(colorCode, string)
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: CommandSender, lines: Array<String>) {
            if (lines.isEmpty()) {
                return
            }

            sender.sendMessage(lines)
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: CommandSender, lines: List<String>) {
            // Convert to an array to send all messages at once.
            var array = emptyArray<String>()
            for (line in lines) {
                array = array.plus(line)

            }

            sender.sendMessage(array)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: Array<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: List<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: Array<String?>) {
            for (line in lines) {
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: List<String?>) {
            for (line in lines) {
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
            }
        }

        private fun saveResource(resourcePath: String, replace: Boolean = false) {
            if (resourcePath.isEmpty()) {
                throw RuntimeException("ResourcePath cannot be empty.")
            }

            var resourcePath2 = resourcePath
            resourcePath2 = resourcePath2.replace('\\', '/')
            val `in`: InputStream = getResource(resourcePath2)
                ?: return
            val outFile = File(resourcePath2)
            val lastIndex = resourcePath2.lastIndexOf('/')
            val outDir = File(resourcePath2.substring(0, if (lastIndex >= 0) lastIndex else 0))
            if (!outDir.exists()) {
                outDir.mkdirs()
            }
            try {
                if (!outFile.exists() || replace) {
                    val out: OutputStream = FileOutputStream(outFile)
                    val buf = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                    out.close()
                    `in`.close()
                }
//                else {
//                    System.err.println(
//                        "Could not save ${outFile.name} to $outFile because ${outFile.name} already exists."
//                    )
//                }
            } catch (ex: IOException) {
                System.err.println("Could not save ${outFile.name} to $outFile")
            }
        }

        private fun getResource(filename: String): InputStream? {
            return try {
                val url: URL = this::class.java.classLoader.getResource(filename) ?: return null
                val connection = url.openConnection()
                connection.useCaches = false
                connection.getInputStream()
            } catch (ex: IOException) {
                null
            }
        }
    }
}