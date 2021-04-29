@file:Suppress("unused")

package com.asledgehammer.langpack.textcomponent

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.Languages
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangFile
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.complex.StringPool
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.objects.formatter.PercentFormatter
import com.asledgehammer.langpack.core.processor.LangProcessor
import com.asledgehammer.langpack.core.util.ResourceUtil
import com.asledgehammer.langpack.core.util.StringUtil
import com.asledgehammer.langpack.textcomponent.objects.complex.ActionText
import com.asledgehammer.langpack.textcomponent.processor.TextComponentProcessor
import java.io.File

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param classLoader
 * @param dir
 */
open class TextComponentLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    override var processor: LangProcessor = TextComponentProcessor(PercentFormatter())
    override var formatter: FieldFormatter = PercentFormatter()

    override fun resolve(query: String, lang: Language, context: LangGroup?): LangDefinition<*>? {

        if (debug) {
            println("""[${this::class.java.simpleName}] :: resolve(query = "$query", lang = ${lang.rawLocale}, context = $context)""")
            // if (context == null) {
            //     println("### STACKTRACE: ")
            //     for (element in Thread.currentThread().stackTrace) {
            //         println(element)
            //     }
            //     println()
            // }
        }

        var raw: LangDefinition<*>? = null

        // If a context is provided, try to look up the absolute path + the query first.
        // Else, treat as Package scope.
        if (context != null && context !is LangFile) {
            var nextContext = context
            while (nextContext != null && nextContext !is LangFile) {
                raw = resolve("${context.getPath()}.$query", lang)
                if (raw != null) {
                    if (debug) {
                        println("""[${this::class.java.simpleName}] :: resolve(query = "$query", language = $lang, context = "$context") = "$raw""")
                    }
                    return raw
                }
                nextContext = nextContext.parent
            }
        }

        // Attempt to grab the most relevant LangFile.
        var langFile = files[lang]
        if (langFile == null) {
            // Check language fallbacks if the file is not defined.
            val fallBack = lang.fallback
            if (fallBack != null) langFile = files[fallBack]
        }

        if (langFile != null) raw = langFile.resolve(query)

        // Check global last.
        if (raw == null && this != global) raw = global?.resolve(query, lang)

        if (debug) {
            println("""[${this::class.java.simpleName}] :: resolve(query = "$query", language = $lang, context = "$context") = "$raw"""")
        }

        return raw
    }

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
        val raw = resolve(query, lang, context) ?: return null
        val value = raw.value ?: return null
        return if (value is Complex<*>) {
            value.process(this, lang, raw.parent ?: context, *args).toString()
        } else {
            processor.process(value.toString(), this, lang, raw.parent ?: context, *args)
        }
    }

    init {
        setDefaultLoaders(loaders)
    }

    companion object {

        private val stringPoolLoader = StringPool.Loader()
        private val actionTextLoader = ActionText.Loader()

        fun setDefaultLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["pool"] = stringPoolLoader
            map["action"] = actionTextLoader
        }

        init {
            // The global 'lang' directory.
            if (!GLOBAL_DIRECTORY.exists()) GLOBAL_DIRECTORY.mkdirs()
            // Store all global lang-files present in the jar.
            for (lang in Languages.values) {
                ResourceUtil.saveResource("lang${File.separator}global_${lang.rawLocale}.yml", null)
            }
            global = TextComponentLangPack()
            global!!.append("global", save = true, force = false)
        }
    }
}