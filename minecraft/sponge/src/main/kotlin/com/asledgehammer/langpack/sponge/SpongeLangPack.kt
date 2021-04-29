@file:Suppress("unused")

package com.asledgehammer.langpack.sponge

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.Languages
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangFile
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.complex.StringPool
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.processor.LangProcessor
import com.asledgehammer.langpack.core.util.StringUtil
import com.asledgehammer.langpack.sponge.objects.complex.SpongeActionText
import com.asledgehammer.langpack.sponge.objects.complex.SpongeStringPool
import com.asledgehammer.langpack.sponge.processor.SpongeProcessor
import com.asledgehammer.langpack.sponge.util.text.TextComponent
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.io.File

/**
 * **SpongeLangPack** wraps the LangPack class to provide additional support for the Sponge API.
 *
 * @author Jab
 *
 * @param classLoader The classloader to load resources.
 * @param dir The directory to handle lang files.
 */
class SpongeLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    override var processor: LangProcessor = SpongeProcessor(formatter)

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
            if (fallBack != null) {
                langFile = files[fallBack]
            }
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

        val cache = HashMap<Language, Text>()

        for (player in Sponge.getServer().onlinePlayers) {

            // Grab the players language, else fallback to default.
            val langPlayer = getLanguage(player)
            var lang = langPlayer

            val cacheText: Text? = cache[lang]
            if (cacheText != null) {
                player.sendMessage(cacheText)
                continue
            }

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
                    (processor as SpongeProcessor).process(component, this, langPlayer, resolved.parent, *args)
                } else {
                    (processor as SpongeProcessor).process(component, this, langPlayer, null, *args)
                }
            val text = result.toText()
            cache[lang] = text
            cache[langPlayer] = text
            player.sendMessage(text)
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
                (processor as SpongeProcessor).process(component, this, langPlayer, resolved.parent, *args)
            } else {
                (processor as SpongeProcessor).process(component, this, langPlayer, null, *args)
            }
        player.sendMessage(result.toText())
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

        private val stringPoolLoader = SpongeStringPool.Loader()
        private val actionTextLoader = SpongeActionText.Loader()

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
            sender.sendMessage(Text.of(lines))
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
            sender.sendMessage(Text.of(array))
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: Array<String>) {
            val channel = Sponge.getServer().broadcastChannel
            for (line in lines) channel.send(Text.of(line))
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: List<String>) {
            val channel = Sponge.getServer().broadcastChannel
            for (line in lines) channel.send(Text.of(line))
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: Array<String?>) {
            val channel = Sponge.getServer().broadcastChannel
            for (line in lines) if (line != null) channel.send(Text.of(line))
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: List<String?>) {
            val channel = Sponge.getServer().broadcastChannel
            for (line in lines) if (line != null) channel.send(Text.of(line))
        }
    }
}
