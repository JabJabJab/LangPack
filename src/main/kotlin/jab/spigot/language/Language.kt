package jab.spigot.language

import org.bukkit.entity.Player

/**
 * The <i>Language</i> enum stores the constants for identification of languages supported by the LanguagePackage library.
 *
 * TODO: Add all languages supported by Minecraft.
 * TODO: Document.
 *
 * https://minecraft.gamepedia.com/Language
 *
 * @author Jab
 */
@Suppress("unused", "SpellCheckingInspection")
enum class Language {

    // English
    ENGLISH("en"),
    ENGLISH_UNITED_STATES("en_us", "en"),
    ENGLISH_AUSTRALIA("en_au", "en"),
    ENGLISH_CANADA("en_ca", "en"),
    ENGLISH_UNITED_KINGDOM("en_gb", "en"),
    ENGLISH_NEW_ZEALAND("en_nz", "en"),
    ENGLISH_SOUTH_AFRICA("en_za", "en"),
    ENGLISH_PIRATE_SPEAK("en_pt", "en"),
    ENGLISH_UPSIDE_DOWN("en_ud", "en"),
    ANGLISH("enp", "en"),
    SHAKESPEAREAN("enws", "en"),

    ESPERANTO("eo_uy"),

    // Spanish
    ESPANOL("es"),
    ESPANOL_ARGENTINA("es_ar", "es"),
    ESPANOL_CHILE("es_cl", "es"),
    ESPANOL_EQUADOR("es_ec", "es"),
    ESPANOL_ESPANA("es_es", "es"),
    ESPANOL_MEXICO("es_mx", "es"),
    ESPANOL_URUGUAY("es_uy", "es"),
    ESPANOL_VENEZUELA("es_ve", "es"),

    // Japanese
    JAPANESE("jp"),
    JAPANESE_NIHONGO("ja_jp", "jp"),

    // Korean
    KOREAN("kr"),
    KOREAN_HANGUG("ko_kr", "kr"),

    // Chinese
    CHINESE("zh"),
    CHINESE_SIMPLIFIED("zh_cn", "zh"),
    CHINESE_TRADITIONAL("zh_tw", "zh"),

    ;

    /** The abbreviation of the Language. */
    val abbreviation: String

    /** The fallback language abbreviation. (Optional) */
    private val fallBack: String?

    /**
     * Full constructor.
     *
     * @param abbreviation The abbreviation of the Language.
     * @param fallback The fallback language to refer to.
     */
    constructor(abbreviation: String, fallback: String?) {
        this.abbreviation = abbreviation
        this.fallBack = fallback
    }

    /**
     * Constructor with no fallback.
     *
     * @param abbreviation The abbreviation of the Language.
     */
    constructor(abbreviation: String) {
        this.abbreviation = abbreviation
        this.fallBack = null
    }

    /**
     * @return Returns the fallback language. If a fallback language is not defined, null is returned.
     */
    fun getFallback(): Language? {
        return if (fallBack != null) {
            getLanguageAbbrev(fallBack)
        } else {
            null
        }
    }

    companion object {

        var DEFAULT_LANGUAGE = ENGLISH

        /**
         * @param name The name of the Language.
         *
         * @return Returns the Language that identifies with the given name. If none identifies, null is returned.
         */
        fun getLanguage(name: String): Language? {
            for (lang in values()) {
                if (lang.name.equals(name, true)) {
                    return lang
                }
            }
            return null
        }

        /**
         * @param abbreviation The abbreviation of the Language.
         *
         * @return Returns the Language that identifies with the given abbreviation. If none identifies, null is returned.
         */
        fun getLanguageAbbrev(abbreviation: String): Language? {
            for (lang in values()) {
                if (lang.abbreviation.equals(abbreviation, true)) {
                    return lang
                }
            }
            return null
        }

        /**
         * TODO: Document.
         *
         * @param player
         *
         * @return
         */
        fun getLanguage(player: Player, fallBack: Language = DEFAULT_LANGUAGE): Language {
            val locale = player.locale
            println("player ${player.name} locale: ${player.locale}")
            for (lang in values()) {
                if (lang.abbreviation.equals(locale, true)) {
                    return lang
                }
            }
            return fallBack
        }
    }
}