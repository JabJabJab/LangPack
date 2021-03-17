@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.langpack.core

import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.LangFile
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.objects.complex.StringPool
import jab.langpack.core.objects.definition.ComplexDefinition
import jab.langpack.core.objects.definition.LangDefinition
import jab.langpack.core.objects.definition.StringDefinition
import jab.langpack.core.objects.formatter.FieldFormatter
import jab.langpack.core.objects.formatter.PercentFormatter
import jab.langpack.core.processor.DefaultProcessor
import jab.langpack.core.processor.LangProcessor
import jab.langpack.core.util.ResourceUtil
import jab.langpack.core.util.StringUtil
import net.md_5.bungee.api.chat.TextComponent
import java.io.File
import java.util.*

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **LangPack** class is a utility that stores entries for dialog, separated by language. Files are loaded into the
 * lang-pack as a [LangFile], and queried based on language context when querying dialog.
 *
 * Text is processed using a [LangProcessor] implementation. By default, lang-packs use the [DefaultProcessor].
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
 * @property name The String name of the pack.
 * @property dir (Optional) The File Object for the directory where the LangFiles are stored. DEFAULT: 'lang/'
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
open class LangPack(val name: String, val dir: File = File("lang")) {

    /**
     * Set to true to print all debug information to the Java console.
     */
    var debug = false

    /**
     * Handles formatting of fields.
     */
    var formatter: FieldFormatter = PercentFormatter()

    /**
     * Handles processing of text.
     */
    var processor: LangProcessor = DefaultProcessor(formatter)

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
     * Reads and loads the pack.
     *
     * @param save (Optional) Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force (Optional) Set to true to save resources, even if they are already present.
     */
    fun load(save: Boolean = false, force: Boolean = false) {
        append(name, save, force)
    }

    /**
     * Appends a pack.
     *
     * @param name The name of the package to append.
     * @param save (Optional) Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force (Optional) Set to true to save resources, even if they are already present.
     */
    fun append(name: String, save: Boolean = false, force: Boolean = false) {

        if (debug) {
            println("[$name] :: append($name)")
        }

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

        walk()
    }

    /**
     * TODO: Document.
     */
    fun walk() {
        for ((_, file) in files) file.unWalk()
        for ((_, file) in files) file.walk()
    }

    /**
     * Sets entries for the language.
     *
     * @param lang The language to modify.
     * @param entries The entries to set.
     */
    fun set(lang: Language, vararg entries: LangArg) {
        // Make sure that we have fields to set.
        if (entries.isEmpty()) return

        // Make sure the language has a file instance before setting anything.
        files.computeIfAbsent(lang) { LangFile(this, lang, lang.abbreviation) }

        for (field in entries) {
            set(lang, field.key, field.value)
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
        if (value != null) {
            if (value is Complex<*>) {
                file.set(key, ComplexDefinition(this, value))
            } else {
                file.set(key, StringDefinition(this, StringUtil.toAString(value)))
            }
        } else {
            file.remove(key)
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

        val resolved = resolve(query, lang, null) ?: return null
        val rawList = StringUtil.toAList(resolved.value!!)
        val processedList = ArrayList<String>()
        for (raw in rawList) {
            if (raw != null) {
                processedList.add(processor.process(raw, this, lang, resolved.parent, *args))
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
        return getString(query, lang, null, *args)
    }

    /**
     * Attempts to resolve a string with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param context (Optional) TODO: Document.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun getString(query: String, lang: Language = defaultLang, context: LangGroup?, vararg args: LangArg): String? {

        if (debug) {
            println("[$name] :: getString(query=$query, ${lang.abbreviation}, $context)")
        }

        val raw = resolve(query, lang, context) ?: return null

        println(raw)

        val value = raw.value ?: return null
        return if (value is Complex<*>) {
            value.process(this, lang, raw.parent ?: context, *args).toString()
        } else {
            processor.process(value.toString(), this, lang, raw.parent ?: context, *args)
        }
    }

    /**
     * Attempts to locate a stored value with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param context TODO: Document.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun resolve(query: String, lang: Language, context: LangGroup? = null): LangDefinition<*>? {

        if (debug) {
            println("[$name] :: resolve($query, ${lang.abbreviation}, $context)")
        }

        var raw: LangDefinition<*>? = null

        // If a context is provided, try to look up the absolute path + the query first.
        // Else, treat as Package scope.
        if (context != null && context !is LangFile) {
            var nextContext = context
            while (nextContext != null && nextContext !is LangFile) {
                raw = resolve("${context.getPath()}.$query", lang)
                if (raw != null) return raw
                nextContext = nextContext.parent
            }
        }

        // Attempt to grab the most relevant LangFile.
        var langFile = files[lang]
        if (langFile == null) {
            // Check language fallbacks if the file is not defined.
            val fallBack = lang.getFallback()
            if (fallBack != null) {
                langFile = files[fallBack]
            }
        }

        if (langFile != null) {
            raw = langFile.resolve(query)
        }

        // Check global last.
        if (raw == null && this != global) {
            raw = global.resolve(query, lang)
        }

        if (debug) {
            println("[$name] :: resolve($query, $lang, $context) = $raw")
        }

        return raw
    }

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves.
     */
    fun contains(lang: Language, query: String): Boolean = files[lang]?.contains(query.toLowerCase()) ?: false

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a [Complex] object.
     */
    fun isComplex(lang: Language, query: String): Boolean = files[lang]?.isComplex(query) ?: false

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a [StringPool].
     */
    fun isStringPool(lang: Language, query: String): Boolean = files[lang]?.isStringPool(query) ?: false

    /**
     * @param lang The language to query.
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the field for the language stores a ActionText.
     */
    fun isActionText(lang: Language, query: String): Boolean = files[lang]?.isActionText(query) ?: false

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

            // Store all global lang-files present in the jar.
            for (lang in Language.values()) {
                ResourceUtil.saveResource("lang${File.separator}global_${lang.abbreviation}.yml")
            }

            global = LangPack("global")
            global.load()
        }
    }
}
