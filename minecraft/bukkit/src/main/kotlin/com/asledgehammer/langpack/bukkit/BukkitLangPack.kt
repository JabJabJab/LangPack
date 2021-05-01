@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.bukkit

import com.asledgehammer.langpack.bukkit.objects.complex.BukkitActionText
import com.asledgehammer.langpack.bukkit.objects.complex.BukkitStringPool
import com.asledgehammer.langpack.bukkit.processor.BukkitProcessor
import com.asledgehammer.langpack.bukkit.util.text.TextComponent
import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.Languages
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangFile
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.processor.LangProcessor
import com.asledgehammer.langpack.core.util.StringUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File

/**
 * **BukkitLangPack** wraps the LangPack class to provide additional support for the Bukkit API.
 *
 * @author Jab
 *
 * @param classLoader The classloader to load resources.
 * @param dir The directory to handle lang files.
 */
class BukkitLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    override var processor: LangProcessor = BukkitProcessor(formatter)

    /**
     * Basic constructor. Uses the 'lang' directory in the server folder.
     *
     * @param classLoader The classloader to load resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    override fun resolve(query: String, lang: Language, context: LangGroup?): LangDefinition<*>? {

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
            val fallBack = lang.fallback
            if (fallBack != null) langFile = files[fallBack]
        }

        if (langFile != null) raw = langFile.resolve(query)

        // Check global last.
        if (raw == null && this != global) raw = global?.resolve(query, lang)
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

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
     * @param query The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcast(query: String, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in Bukkit.getOnlinePlayers()) {
            // Grab the players language, else fallback to default.
            val langPlayer = getLanguage(player)
            var lang = langPlayer

            val cacheText: String? = cache[lang]
            if (cacheText != null) {
                player.sendMessage(cacheText)
                continue
            }

            var resolved = resolve(query, lang)
            if (resolved == null) {
                lang = defaultLang
                resolved = resolve(query, lang)
            }

            val component: String
            if (resolved != null) {
                val value = resolved.value
                component = when (value) {
                    is Complex<*> -> {
                        val result = value.get()
                        val processedComponent: String = if (result is String) {
                            result
                        } else {
                            result.toString()
                        }
                        processedComponent
                    }
                    else -> {
                        value.toString()
                    }
                }
            } else {
                component = query
            }

            val result =
                if (resolved != null) {
                    (processor as BukkitProcessor).process(component, this, langPlayer, resolved.parent, *args)
                } else {
                    (processor as BukkitProcessor).process(component, this, langPlayer, null, *args)
                }
            cache[lang] = result
            cache[langPlayer] = result
            player.sendMessage(result)
        }
    }

    /**
     * Messages a player with a given field and arguments. The language will be based on [Player.getLocale].
     *   If the language is not supported, [LangPack.defaultLang] will be used.
     *
     * @param player The player to send the message.
     * @param query The field to send.
     * @param args Additional arguments to apply.
     */
    fun message(player: Player, query: String, vararg args: LangArg) {
        val langPlayer = getLanguage(player)
        var lang = langPlayer

        var resolved = resolve(query, lang)
        if (resolved == null) {
            lang = defaultLang
            resolved = resolve(query, lang)
        }

        val component: TextComponent
        if (resolved != null) {
            val value = resolved.value
            component = when (value) {
                is Complex<*> -> {
                    val result = value.get()
                    val processedComponent: TextComponent = if (result is TextComponent) {
                        result
                    } else {
                        TextComponent(result.toString())
                    }
                    processedComponent
                }
                else -> {
                    TextComponent(value.toString())
                }
            }
        } else {
            component = TextComponent(query)
        }

        val result =
            if (resolved != null) {
                (processor as BukkitProcessor).process(component, this, langPlayer, resolved.parent, *args)
            } else {
                (processor as BukkitProcessor).process(component, this, langPlayer, null, *args)
            }
        player.sendMessage(result.toLegacyText())
    }

    /**
     * @param player The player to read.
     *
     * @return Returns the language of [Player.getLocale]. If the locale set is invalid, the fallBack
     *   is returned.
     */
    fun getLanguage(player: Player): Language = Languages.getClosest(player.locale, defaultLang)

    init {
        setSpongeLoaders(loaders)
    }

    companion object {

        private val stringPoolLoader = BukkitStringPool.Loader()
        private val actionTextLoader = BukkitActionText.Loader()

        fun setSpongeLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["pool"] = stringPoolLoader
            map["action"] = actionTextLoader
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: Player, lines: Array<String>) {
            if (lines.isEmpty()) return
            sender.sendMessage(lines)
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: Player, lines: List<String>) {
            // Convert to an array to send all messages at once.
            var array = emptyArray<String>()
            for (line in lines) array = array.plus(line)
            sender.sendMessage(array)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: Array<String>) {
            for (line in lines) Bukkit.broadcastMessage(line)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: List<String>) {
            for (line in lines) Bukkit.broadcastMessage(line)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: Array<String?>) {
            for (line in lines) if (line != null) Bukkit.broadcastMessage(line)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: List<String?>) {
            for (line in lines) if (line != null) Bukkit.broadcastMessage(line)
        }
    }
}
