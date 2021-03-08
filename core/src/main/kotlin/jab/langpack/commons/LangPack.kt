package jab.langpack.commons

import jab.langpack.commons.objects.Complex
import jab.langpack.commons.objects.StringPool
import jab.langpack.commons.processor.LangProcessor
import jab.langpack.commons.processor.PercentProcessor
import jab.langpack.commons.util.ResourceUtil
import jab.langpack.commons.util.StringUtil
import net.md_5.bungee.api.chat.TextComponent
import java.io.File
import java.util.*

/**
 * The **LangPack** class is a utility that stores entries for dialog, separated by language. Files are loaded into the
 * lang-pack as a [LangFile], and queried based on language context when querying dialog.
 *
 * Text is processed using a [LangProcessor] implementation. By default, lang-packs use the [PercentProcessor].
 *
 * Text is processed dynamically as [TextComponent], allowing for dynamic text to be sent to players, enabling hover &
 * click events to be used throughout all entries in the lang-pack. If not desired, simply process the query as a
 * string. All text is processed when queried. If a result is queried more than once and is expected to be the same
 * result, use implementations of the [LangCache] utility to cache and recall the results of a query.
 *
 * LangPack works with contexts, referring to the [defaultLang] property in the lang-pack first, then the global context
 * last when attempting to resolve a query.
 *
 * Example query: ``command.dialog`` language: ``SPANISH_GENERIC``
 *   - specific langPack -> ``SPANISH_GENERIC``
 *   - specific langPack -> ``defaultLang``
 *   - global langPack -> ``SPANISH_GENERIC``
 *   - global langPack -> ``defaultLang``
 *
 * LangPacks can be created and modified during runtime using the [set] method.
 *
 * @author Jab
 *
 * @property name The String name of the LanguagePackage.
 * @property dir (Optional) The File Object for the directory where the LangFiles are stored. DEFAULT: 'lang/'
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class LangPack(val name: String, val dir: File = File("lang")) {

    /**
     * Set to true to print all debug information to the Java console.
     */
    var debug = false

    /**
     * Handles processing of texts for the LanguageFile.
     */
    var processor: LangProcessor = PercentProcessor()

    /**
     * The language file to default to if a raw string cannot be located with another language.
     */
    var defaultLang: Language = Language.ENGLISH_GENERIC

    /**
     * The Map for LanguageFiles, assigned with their Languages.
     */
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
     */
    fun load(save: Boolean = false, force: Boolean = false) {
        append(name, save, force)
    }

    /**
     * Appends a language package.
     *
     * @param name The name of the package to append.
     * @param save (Optional) Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force (Optional) Set to true to save resources, even if they are already present.
     */
    fun append(name: String, save: Boolean = false, force: Boolean = false) {
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
                    files[lang] = LangFile(this, file, lang).load()
                }
            }
        }
    }

    /**
     * Sets a entry for a language.
     *
     * @param lang The language to modify.
     * @param key The field to set.
     * @param value The value to set.
     */
    fun set(lang: Language, key: String, value: Any?) {
        val file: LangFile = files.computeIfAbsent(lang) { LangFile(this, lang, lang.abbreviation) }
        file.set(key, value)
    }

    /**
     * Sets entries for the language.
     *
     * @param lang The language to modify.
     * @param entries The entries to set.
     */
    fun set(lang: Language, vararg entries: LangArg) {

        // Make sure that we have fields to set.
        if (entries.isEmpty()) {
            return
        }

        val file: LangFile = files.computeIfAbsent(lang) { LangFile(this, lang, lang.abbreviation) }
        for (field in entries) {
            file.set(field.key, field.value)
        }
    }

    /**
     * Attempts to resolve a string-list with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved string-list. If nothing is located at the destination of the query, null is returned.
     */
    fun getList(query: String, lang: Language = defaultLang, vararg args: LangArg): List<String>? {

        val string = getString(query, lang, *args) ?: return null
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
     * Attempts to resolve a string with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun getString(query: String, lang: Language = defaultLang, vararg args: LangArg): String? {

        val raw = resolve(lang, query)
        return if (raw != null) {
            when (raw) {
                is Complex<*> -> {
                    val result = raw.process(this, lang, *args)
                    if (result is TextComponent) {
                        result.toPlainText()
                    } else {
                        result.toString()
                    }
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
     * Attempts to locate a stored value with a query.
     *
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun resolve(lang: Language, query: String): Any? {

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
            raw = langFile.resolve(query)
        }

        // Check global last.
        if (raw == null && this != global) {
            raw = global.resolve(lang, query)
        }

        return raw
    }

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves.
     */
    fun contains(lang: Language, query: String): Boolean {
        return files[lang]?.contains(query.toLowerCase()) ?: false
    }

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a [LangComplex] object.
     */
    fun isComplex(lang: Language, query: String): Boolean {
        return files[lang]?.isComplex(query) ?: false
    }

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a [StringPool].
     */
    fun isStringPool(lang: Language, query: String): Boolean {
        return files[lang]?.isStringPool(query) ?: false
    }

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a ActionText.
     */
    fun isActionText(lang: Language, query: String): Boolean {
        return files[lang]?.isActionText(query) ?: false
    }

    companion object {

        /**
         * The global context for all lang-packs to reference for unresolved queries.
         */
        val global: LangPack

        /**
         * The global directory for lang-packs to load from by default.
         */
        val GLOBAL_DIRECTORY: File = File("lang")

        /**
         * The standard 'line.separator' for most Java Strings.
         */
        const val NEW_LINE: String = "\n"

        /**
         * The default [Random] instance to use throughout lang-pack.
         */
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

            global = LangPack("global")
            global.load()
        }
    }
}
