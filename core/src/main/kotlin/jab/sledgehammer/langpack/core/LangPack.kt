@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.core

import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangFile
import jab.sledgehammer.langpack.core.objects.LangGroup
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.complex.StringPool
import jab.sledgehammer.langpack.core.objects.definition.ComplexDefinition
import jab.sledgehammer.langpack.core.objects.definition.LangDefinition
import jab.sledgehammer.langpack.core.objects.definition.StringDefinition
import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.objects.formatter.PercentFormatter
import jab.sledgehammer.langpack.core.processor.DefaultProcessor
import jab.sledgehammer.langpack.core.processor.LangProcessor
import jab.sledgehammer.langpack.core.util.ResourceUtil
import jab.sledgehammer.langpack.core.util.StringUtil
import java.io.File
import java.util.*

/**
 * **LangPack** is a utility that stores entries for dialog, separated by language. Files are loaded into the
 * lang-pack as a [LangFile], and queried based on language context when querying dialog.
 *
 * Text is processed using a [LangProcessor] implementation. By default, lang-packs use the [DefaultProcessor].
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
 * @property classLoader (Optional) (Recommended) Pass the plugin classloader instance to use the save features for the
 * library.
 * @property dir (Optional) The File Object for the directory where the LangFiles are stored. DEFAULT: 'lang/'
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
open class LangPack(
    private val classLoader: ClassLoader = this::class.java.classLoader,
    val dir: File = File("lang"),
) {
    /**
     * Handles processing of text.
     */
    open var processor: LangProcessor = DefaultProcessor(PercentFormatter())

    /**
     * Handles processing of text.
     */
    open var formatter: FieldFormatter = PercentFormatter()

    /**
     * Set to true to print all debug information to the Java console.
     */
    var debug = false

    /**
     * The language file to default to if a raw string cannot be located with another language.
     */
    var defaultLang: Language = Language.ENGLISH_GENERIC

    /**
     * [Complex.Loader] instances are stored here to load when reading and loading lang files.
     */
    protected val loaders = HashMap<String, Complex.Loader<*>>()

    /**
     * The Map for LanguageFiles, assigned with their Languages.
     */
    protected val files: EnumMap<Language, LangFile> = EnumMap(Language::class.java)

    /**
     * Simple constructor.
     *
     * Use this constructor to define a classloader while still using the default 'Lang' directory in the server folder.
     *
     * @param classLoader The classloader instance to fetch lang resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    init {
        setDefaultLoaders(loaders)
        require(dir.isDirectory) { """The path "$dir" is not a valid directory.""" }
        if (!dir.exists()) require(dir.mkdirs()) { """The directory "$dir" could not be created.""" }
    }

    /**
     * Appends a pack.
     *
     * > **WARNING:** Not passing the classloader for the plugin calling this method will not save any lang files stored in
     * the plugin's JAR file.
     *
     * @param name The name of the package to append.
     * @param save Set to true to try to detect & save files from the plugin to the lang folder.
     * @param force Set to true to save resources, even if they are already present.
     */
    @JvmOverloads
    fun append(name: String, save: Boolean = false, force: Boolean = false): LangPack {

        if (debug) println("[$name] :: append($name)")

        // Save any resources detected.
        if (save) {
            for (lang in Language.values()) {
                val resourcePath = "${dir.path}${File.separator}${name}_${lang.abbreviation}.yml"
                try {
                    ResourceUtil.saveResource(resourcePath, classLoader, force)
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

        return this
    }

    fun print(): String {

        var text = ""
        var prefix = ""

        fun raw(string: String) {
            text += string
        }

        fun line(string: String) {
            text += "$prefix$string\n"
        }

        fun tab() {
            prefix += "  "
        }

        fun untab() {
            val index = prefix.lastIndex - 1
            prefix = if (index < 1) "" else prefix.substring(0, index)
        }

        fun recurse(key: String?, value: Any?) {
            if (value is Map<*, *>) {
                if (key != null) line("$key: {")
                else line("{")
                tab()
                for ((key2, value2) in value) if (value2 != null) recurse(key2.toString(), value2)
                untab()
                line("},")
            } else if (value is List<*>) {
                if (key != null) line("$key: [")
                else line("[")
                tab()
                for ((index, item) in value.withIndex()) recurse("$index", item)
                untab()
                line("],")
            } else {
                if (key != null) line("$key: $value")
                else line("$value,")
            }
        }

        fun recurseGroup(group: LangGroup) {
            if (group is LangFile) {
                line("File(${group.language.abbreviation}) {")
            } else {
                line("Section(${group.name}) {")
            }
            tab()
            for ((_, value) in group.children) recurseGroup(value)
            for ((key, value) in group.fields) recurse(key, value)
            untab()
            line("},")
        }

        line("${this::class.java.simpleName} {")
        tab()
        for ((_, file) in files) {
            recurseGroup(file)
        }
        untab()
        line("}")

        return text
    }

    /**
     * Attempts to locate a stored value with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param context (Optional) Pass a group as the scope to query fields relatively.
     * Otherwise, the scope is the package.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    @JvmOverloads
    open fun resolve(query: String, lang: Language, context: LangGroup? = null): LangDefinition<*>? {

        if (debug) println("[LangPack] :: resolve($query, ${lang.abbreviation}, $context)")

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

        if (langFile != null) raw = langFile.resolve(query)

        // Check global last.
        if (raw == null && this != global) raw = global?.resolve(query, lang)
        if (debug) println("[LangPack] :: resolve($query, $lang, $context) = $raw")
        return raw
    }

    /**
     * Attempts to resolve a string-list with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved string-list. If nothing is located at the destination of the query, null is returned.
     */
    fun getList(query: String, vararg args: LangArg): List<String>? = getList(query, defaultLang, *args)

    /**
     * Attempts to resolve a string-list with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved string-list. If nothing is located at the destination of the query, null is returned.
     */
    open fun getList(query: String, lang: Language, vararg args: LangArg): List<String>? {

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
     * Resolves a query, returning the result as a String.
     *
     * @param query The query to process. The query can be a field or set of fields delimited by a period.
     * @param lang The language to query.
     * @param context (Optional) Pass a group as the scope to query fields relatively.
     * Otherwise, the scope is the package.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    @JvmOverloads
    open fun getString(query: String, lang: Language, context: LangGroup? = null, vararg args: LangArg): String? {

        if (debug) println("[LangPack] :: getString(query=$query, ${lang.abbreviation}, $context)")

        val raw = resolve(query, lang, context) ?: return null
        val value = raw.value ?: return null
        return if (value is Complex<*>) {
            value.process(this, lang, raw.parent ?: context, *args).toString()
        } else {
            processor.process(value.toString(), this, lang, raw.parent ?: context, *args)
        }
    }

    /**
     * Performs a walk on all loaded definitions. Processes all resolve fields.
     */
    fun walk() {
        for ((_, file) in files) file.unWalk()
        for ((_, file) in files) file.walk()
    }

    /**
     * Clears all data from the package.
     */
    fun clear() {
        this.files.clear()
    }

    /**
     * @param type The type of complex object.
     *
     * @return Returns the loader assigned to the type. If one is not assigned, null is returned.
     */
    fun getLoader(type: String): Complex.Loader<*>? = loaders[type.toLowerCase()]

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
     * Sets a loader for the type.
     *
     * @param type The type of complex object.
     * @param loader The loader to assign.
     */
    fun setLoader(type: String, loader: Complex.Loader<*>?) {
        if (loader != null) {
            loaders[type.toLowerCase()] = loader
        } else {
            loaders.remove(type.toLowerCase())
        }
    }

    /**
     * Removes a loader assigned to the type.
     *
     * @param type The type of complex object.
     */
    fun removeLoader(type: String) {
        loaders.remove(type.toLowerCase())
    }

    /**
     * @param type The type of complex object.
     *
     * @return Returns true if a loader is assigned to the type.
     */
    fun containsLoader(type: String): Boolean = loaders.containsKey(type.toLowerCase())

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

    companion object {

        private val stringPoolLoader = StringPool.Loader()

        /**
         * The global context for all lang-packs to reference for unresolved queries.
         */
        var global: LangPack? = null

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
            if (!GLOBAL_DIRECTORY.exists()) GLOBAL_DIRECTORY.mkdirs()

            // Store all global lang-files present in the jar.
            for (lang in Language.values()) {
                ResourceUtil.saveResource("lang${File.separator}global_${lang.abbreviation}.yml", null)
            }

            global = LangPack()
            global!!.append("global", save = true, force = false)
        }

        /**
         * Set the default loaders for all LangPack instances.
         *
         * @param map the map to set the loaders.
         */
        fun setDefaultLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["pool"] = stringPoolLoader
        }
    }
}
