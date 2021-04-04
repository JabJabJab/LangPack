@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.basic

import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangFile
import jab.sledgehammer.langpack.core.objects.LangGroup
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.definition.LangDefinition
import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.objects.formatter.PercentFormatter
import jab.sledgehammer.langpack.core.processor.DefaultProcessor
import jab.sledgehammer.langpack.core.processor.LangProcessor
import jab.sledgehammer.langpack.core.util.ResourceUtil
import jab.sledgehammer.langpack.core.util.StringUtil
import java.io.File
import java.util.*

open class BasicLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    /**
     * Handles processing of text.
     */
    override var processor: LangProcessor = DefaultProcessor(PercentFormatter())

    /**
     * Handles processing of text.
     */
    override var formatter: FieldFormatter = PercentFormatter()

    /**
     * Simple constructor.
     *
     * Use this constructor to define a classloader while still using the default 'Lang' directory in the server folder.
     *
     * @param classLoader The classloader instance to fetch lang resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

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
    override fun resolve(query: String, lang: Language, context: LangGroup?): LangDefinition<*>? {

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
     * @param lang The language to query.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the resolved string-list. If nothing is located at the destination of the query, null is returned.
     */
    override fun getList(query: String, lang: Language, vararg args: LangArg): List<String>? {

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

    override fun getString(query: String, lang: Language, context: LangGroup?, vararg args: LangArg): String? {

        if (debug) println("[LangPack] :: getString(query=$query, ${lang.abbreviation}, $context)")

        val raw = resolve(query, lang, context) ?: return null
        val value = raw.value ?: return null
        return if (value is Complex<*>) {
            value.process(this, lang, raw.parent ?: context, *args).toString()
        } else {
            processor.process(value.toString(), this, lang, raw.parent ?: context, *args)
        }
    }

    companion object {
        init {
            // The global 'lang' directory.
            if (!GLOBAL_DIRECTORY.exists()) GLOBAL_DIRECTORY.mkdirs()

            // Store all global lang-files present in the jar.
            for (lang in Language.values()) {
                ResourceUtil.saveResource("lang${File.separator}global_${lang.abbreviation}.yml", null)
            }

            global = BasicLangPack()
            global!!.append("global", save = true, force = false)
        }
    }
}
