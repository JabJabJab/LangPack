@file:Suppress("unused")

package jab.langpack.core

import jab.langpack.core.objects.LangArg
import java.util.*

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **LangCache** class proxies the processed calls for [LangPack], storing them as a cached result to be
 * referred to if called again. The purpose of the cache is to avoid wasted calculations on multiple calls that returns
 * the same result.
 *
 * If dynamic calls to the same field are passed different arguments or the arguments referenced in the string
 * processing are dynamic, do not use a cache to call to them, otherwise the first call to the cache for this string
 * will always return, regardless of successive requests.
 *
 * @author Jab
 *
 * @property pack The lang-pack instance to call to and cache the results.
 */
open class LangCache<Pack : LangPack>(val pack: Pack) {

    private val cache: EnumMap<Language, HashMap<String, String>> = EnumMap(Language::class.java)
    private val cacheList: EnumMap<Language, HashMap<String, List<String?>>> = EnumMap(Language::class.java)

    override fun toString(): String = "LangCache(pack=$pack, cache=$cache, cacheList=$cacheList)"

    /**
     * @see LangPack.getString
     */
    fun getString(query: String, lang: Language = pack.defaultLang, vararg args: LangArg): String {

        val fieldLower = query.toLowerCase()

        if (cache.containsKey(lang)) {
            val cache = cache[lang]!!
            if (cache.containsKey(fieldLower)) {
                return cache[fieldLower]!!
            }
        }

        var value = pack.getString(query, lang, *args)
        if (value == null) {
            value = query.toLowerCase()
        }

        val cache = cache.computeIfAbsent(lang) { HashMap() }
        cache[fieldLower] = value

        return value
    }

    /**
     * @see LangPack.getList
     */
    fun getList(query: String, lang: Language = pack.defaultLang, vararg args: LangArg): List<String?>? {

        val fieldLower = query.toLowerCase()

        if (cacheList.containsKey(lang)) {
            val cacheList = cacheList[lang]!!
            if (cacheList.containsKey(fieldLower)) {
                return cacheList[fieldLower]
            }
        }

        val value = pack.getList(query, lang, *args)
        if (value != null) {
            val cacheList = cacheList.computeIfAbsent(lang) { HashMap() }
            cacheList[fieldLower] = value
        }

        return value
    }

    /**
     * Clears results stored in the cache. If no arguments are provided, the entire language section of the cache is
     * removed.
     *
     * @param lang The language to clear.
     * @param fields The fields to clear.
     */
    fun clear(lang: Language, vararg fields: String) {

        // If no args are provided, remove the language.
        if (fields.isEmpty()) {
            cache.remove(lang)
            cacheList.remove(lang)
            return
        }

        for (field in fields) {
            cache[lang]?.remove(field)
            cacheList[lang]?.remove(field)
        }
    }

    /**
     * Clears all messages stored in the cache.
     */
    fun clear() {
        cache.clear()
        cacheList.clear()
    }
}
