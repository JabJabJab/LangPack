package jab.spigot.language

import jab.spigot.language.`object`.LangComplex
import jab.spigot.language.`object`.LangComponent
import jab.spigot.language.processor.LangProcessor
import jab.spigot.language.processor.PercentProcessor
import jab.spigot.language.util.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.*
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
 * @property name The String name of the LanguagePackage. This is noted in the LanguageFiles as
 *      "{{name}}_{{language_abbreviation}}.yml"
 * @property dir (Optional) The File Object for the directory where the LangFiles are stored. DEFAULT: 'lang/'
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LangPackage(val name: String, val dir: File = File("lang")) {

    /** Handles processing of texts for the LanguageFile. */
    var processor: LangProcessor = PercentProcessor()

    /** The language file to default to if a raw string cannot be located with another language. */
    var defaultLang: Language = Language.ENGLISH_GENERIC

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
     *
     * @param save (Optional) Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force (Optional) Set to true to save resources, even if they are already present.
     *
     * @return Returns the instance. (For one-line executions)
     */
    fun load(save: Boolean = false, force: Boolean = false): LangPackage {
        append(name, save, force)
        return this
    }

    /**
     * Appends a language package.
     *
     * @param name The name of the package to append.
     * @param save (Optional) Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force (Optional) Set to true to save resources, even if they are already present.
     *
     * @return Returns the instance. (For one-line executions)
     */
    fun append(name: String, save: Boolean = false, force: Boolean = false): LangPackage {

        // Save any resources detected.
        if (save) {

            for (lang in Language.values()) {

                val resourcePath = "${dir.path}${File.separator}${name}_${lang.abbreviation}.yml"

                try {
                    ResourceUtil.saveResource(resourcePath, force)
                } catch (e: Exception) {
                    System.err.println("Failed to save resource: $resourcePath")
                    e.printStackTrace(System.err)
                }
            }
        }

        // Search for and load LangFiles for the package.
        for (lang in Language.values()) {

            val file = File(dir, "${name}_${lang.abbreviation}.yml")
            if (file.exists()) {

                val langFile = files[lang]
                if (langFile != null) {
                    langFile.append(file)
                } else {
                    files[lang] = LangFile(file, lang).load()
                }
            }
        }

        return this
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
     * @param args
     *
     * @return
     */
    fun getList(field: String, lang: Language = defaultLang, vararg args: LangArg): List<String>? {

        val string = getString(field, lang, *args) ?: return null
        val rawList = StringUtil.toAList(string)
        val processedList = ArrayList<String>()
        for (raw in rawList) {
            if (raw != null) {
                processedList.add(processor.processString(raw, this, lang, *args))
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
    fun getString(field: String, lang: Language = defaultLang, vararg args: LangArg): String? {

        val raw = getRaw(field, lang)
        return if (raw != null) {
            when (raw) {
                is LangComponent -> {
                    raw.process(this, lang, *args).toPlainText()
                }
                is LangComplex -> {
                    raw.process(this, lang, *args)
                }
                else -> {
                    processor.processString(raw.toString(), this, lang, *args)
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
     *
     * @return
     */
    fun getRaw(field: String, lang: Language): Any? {

        // Attempt to grab the most relevant LangFile.
        var langFile = files[lang]
        if (langFile == null) {

            // Check language fallbacks if the file is not defined.
            val fallBack = lang.getFallback()
            if (fallBack != null) {
                langFile = files[fallBack]
            }
        }

        var raw: Any? = null
        if (langFile != null) {
            raw = langFile.get(field)
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
     *
     * @param field The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcast(field: String, vararg args: LangArg) {

        val cache: EnumMap<Language, TextComponent> = EnumMap<Language, TextComponent>(Language::class.java)

        for (player in Bukkit.getOnlinePlayers()) {
            // Grab the players language, else fallback to default.
            val langPlayer = Language.getLanguage(player, defaultLang)
            var lang = langPlayer

            if (cache[lang] != null) {
                player.spigot().sendMessage(cache[lang])
                continue
            }

            var value = getRaw(field, lang)
            if (value == null) {
                lang = defaultLang
                value = getRaw(field, lang)
            }

            val component: TextComponent
            if (value != null) {
                component = when (value) {
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
            } else {
                component = TextComponent(field)
            }

            val result = processor.processComponent(component, this, langPlayer, *args)
            cache[lang] = result
            cache[langPlayer] = result

            player.spigot().sendMessage(result)
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
    fun message(player: Player, field: String, vararg args: LangArg) {

        val langPlayer = Language.getLanguage(player, defaultLang)
        var lang = langPlayer

        var value = getRaw(field, lang)
        if (value == null) {
            lang = defaultLang
            value = getRaw(field, lang)
        }

        val component: TextComponent
        if (value != null) {
            component = when (value) {
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
        } else {
            component = TextComponent(field)
        }

        player.spigot().sendMessage(
            processor.processComponent(component, this, langPlayer, *args)
        )
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
     * @return Returns true if the field for the language stores a ActionText.
     */
    fun isActionText(lang: Language, field: String): Boolean {
        return files[lang]?.isActionText(field) ?: false
    }

    companion object {

        /** TODO: Document. */
        val global: LangPackage

        /** TODO: Document. */
        val GLOBAL_DIRECTORY: File = File("lang")

        /** The standard 'line.separator' for most Java Strings. */
        const val NEW_LINE: String = "\n"

        /** TODO: Document. */
        var DEFAULT_RANDOM: Random = Random()

        init {

            // The global 'lang' directory.
            if (!GLOBAL_DIRECTORY.exists()) {
                GLOBAL_DIRECTORY.mkdirs()
            }

            // Store all global lang files present in the jar.
            for (lang in Language.values()) {
                ResourceUtil.saveResource("lang${File.separator}global_${lang.abbreviation}.yml")
            }

            global = LangPackage("global").load()
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
    }
}
