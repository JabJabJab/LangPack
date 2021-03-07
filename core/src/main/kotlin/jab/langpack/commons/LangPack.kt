package jab.langpack.commons

import jab.langpack.commons.objects.LangComplex
import jab.langpack.commons.objects.LangComponent
import jab.langpack.commons.objects.StringPool
import jab.langpack.commons.processor.LangProcessor
import jab.langpack.commons.processor.PercentProcessor
import jab.langpack.commons.util.ResourceUtil
import jab.langpack.commons.util.StringUtil
import java.io.File
import java.util.*

/**
 * The **LangPack** class.
 *
 * TODO: Document.
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
     * Sets a value for a language.
     *
     * @param lang The language to set.
     * @param key The field to set.
     * @param value The value to set.
     */
    fun set(lang: Language, key: String, value: Any?) {
        val file: LangFile = files.computeIfAbsent(lang) { LangFile(this, lang, lang.abbreviation) }
        file.set(key, value)
    }

    /**
     * Sets a value for the language.
     *
     * @param lang The language to set.
     * @param args The fields to set.
     */
    fun set(lang: Language, vararg args: LangArg) {

        // Make sure that we have fields to set.
        if (args.isEmpty()) {
            return
        }

        val file: LangFile = files.computeIfAbsent(lang) { LangFile(this, lang, lang.abbreviation) }
        for (field in args) {
            file.set(field.key, field.value)
        }
    }

    /**
     * TODO: Document.
     *
     * @param key
     * @param lang
     * @param args
     *
     * @return
     */
    fun getList(key: String, lang: Language = defaultLang, vararg args: LangArg): List<String>? {

        val string = getString(key, lang, *args) ?: return null
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
     * @param key
     * @param lang
     *
     * @return
     */
    fun getString(key: String, lang: Language = defaultLang, vararg args: LangArg): String? {

        val raw = resolve(key, lang)
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
     * @param query
     * @param lang
     *
     * @return
     */
    fun resolve(query: String, lang: Language): Any? {

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
            raw = global.resolve(query, lang)
        }

        return raw
    }

    /**
     * TODO: Document.
     *
     * @param lang
     * @param query
     *
     * @return
     */
    fun contains(lang: Language, query: String): Boolean {
        return files[lang]?.contains(query.toLowerCase()) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param query The field to test.
     *
     * @return Returns true if the field for the language stores a [LangComplex] object.
     */
    fun isComplex(lang: Language, query: String): Boolean {
        return files[lang]?.isComplex(query) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param query The field to test.
     *
     * @return Returns true if the field for the language stores a component-based value.
     */
    fun isLangComponent(lang: Language, query: String): Boolean {
        return files[lang]?.isLangComponent(query) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param query The field to test.
     *
     * @return Returns true if the field for the language stores a [StringPool].
     */
    fun isStringPool(lang: Language, query: String): Boolean {
        return files[lang]?.isStringPool(query) ?: false
    }

    /**
     * TODO: Document.
     *
     * @param lang The language to test.
     * @param query The field to test.
     *
     * @return Returns true if the field for the language stores a ActionText.
     */
    fun isActionText(lang: Language, query: String): Boolean {
        return files[lang]?.isActionText(query) ?: false
    }

    companion object {

        val global: LangPack

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

            global = LangPack("global")
            global.load()
        }
    }
}
