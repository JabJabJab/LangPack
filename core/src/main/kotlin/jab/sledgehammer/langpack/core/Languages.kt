@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package jab.sledgehammer.langpack.core

import java.util.*

/**
 * **Languages**
 *
 * TODO: Document.
 *
 * @author Jab
 */
object Languages {

    private val mapLocale = HashMap<Locale, Language>()
    private val mapString = HashMap<String, Language>()
    private var values = emptyList<Language>()

    /**
     * TODO: Document.
     */
    fun getClosest(rawLocale: String, defaultLanguage: Language): Language {
        val result = getClosest(toLocale(rawLocale), defaultLanguage)
        return result
    }

    /**
     * TODO: Document.
     */
    fun getClosest(locale: Locale, defaultLanguage: Language): Language {
        var language: Language? = get(locale)
        if (language != null) return language

        fun search(language: Boolean, region: Boolean = false): Language? {
            for ((nextLocale, nextLanguage) in mapLocale) {
                if (
                    (!language || nextLocale.language == locale.language)
                    && (!region || nextLocale.country == locale.country)
                ) {
                    return nextLanguage
                }
            }
            return null
        }

        language = search(language = true, region = true)
        if (language != null) return language

        language = search(true)
        if (language != null) return language

        return defaultLanguage
    }

    /**
     * TODO: Document.
     *
     * @param locale
     *
     * @return
     */
    fun get(locale: Locale): Language? {
        for ((nextLocale, language) in mapLocale) if (locale == nextLocale) return language
        return null
    }

    /**
     * @param rawLocale The raw locale of the Language.
     *
     * @return Returns the Language that identifies with the given abbreviation. If none identifies, null is returned.
     */
    fun get(rawLocale: String): Language? {
        val lower = rawLocale.toLowerCase()
        for ((nextRawLocale, language) in mapString) if (nextRawLocale == lower) return language
        return null
    }

    /**
     * TODO: Document.
     */
    fun register(rawLocale: String, rawLocaleFallback: String? = null): Language {
        require(rawLocale.isNotEmpty()) { "The raw locale is empty." }
        require(rawLocaleFallback == null || rawLocaleFallback.isNotEmpty()) { "The fallback raw locale is empty." }

        val locale: Locale = toLocale(rawLocale)
        val fallback: Language? = if (rawLocaleFallback != null) mapLocale[toLocale(rawLocaleFallback)] else null
        val language = Language(locale, fallback)
        register(language)
        return language
    }

    /**
     * TODO: Document.
     */
    fun toLocale(raw: String): Locale {
        return if (raw.contains("_")) {
            val split = raw.toLowerCase().split("_")
            Locale(split[0], split[1])
        } else {
            Locale(raw)
        }
    }

    /**
     * TODO: Document.
     */
    fun register(locale: Locale, fallback: Language? = null) {
        register(Language(locale, fallback))
    }

    /**
     * TODO: Document.
     */
    fun buildValues() {
        values = Collections.unmodifiableList(ArrayList(mapLocale.values))
    }

    /**
     * TODO: Document.
     */
    fun register(language: Language) {
        mapLocale[language.locale] = language
        mapString[language.rawLocale.toLowerCase()] = language
        buildValues()
    }

    /**
     * TODO: Document.
     */
    fun unregister(language: Language) {
        mapLocale.remove(language.locale)
        mapString.remove(language.rawLocale.toLowerCase())
        buildValues()
    }

    /**
     * TODO: Document.
     */
    fun values(): List<Language> = values

    val AFRIKAANS_GENERIC: Language = register("af")
    val AFRIKAANS: Language = register("af_za", "af")
    val ARABIC_GENERIC: Language = register("ar")
    val ARABIC: Language = register("ar_sa", "ar")
    val AZERBAIJANI_GENERIC: Language = register("az")
    val AZERBAIJANI: Language = register("az_az", "az")
    val BOSNIAN_GENERIC: Language = register("bs")
    val BOSNIAN: Language = register("bs_ba", "bs")
    val CHINESE_GENERIC: Language = register("zh")
    val CHINESE_SIMPLIFIED: Language = register("zh_cn", "zh")
    val CHINESE_TRADITIONAL: Language = register("zh_tw", "zh")
    val CZECH_GENERIC: Language = register("cz")
    val CZECH: Language = register("cs_cz", "cz")
    val DANISH_GENERIC: Language = register("da")
    val DANISH: Language = register("da_dk", "da")
    val DUTCH_GENERIC: Language = register("nl")
    val DUTCH: Language = register("nl_nl", "nl")
    val DUTCH_FLEMISH: Language = register("nl_be", "nl")
    val ENGLISH_GENERIC: Language = register("en")
    val ENGLISH_UNITED_STATES: Language = register("en_us", "en")
    val ENGLISH_AUSTRALIA: Language = register("en_au", "en")
    val ENGLISH_CANADA: Language = register("en_ca", "en")
    val ENGLISH_UNITED_KINGDOM: Language = register("en_gb", "en")
    val ENGLISH_NEW_ZEALAND: Language = register("en_nz", "en")
    val ENGLISH_SOUTH_AFRICA: Language = register("en_za", "en")
    val ENGLISH_PIRATE_SPEAK: Language = register("en_pt", "en")
    val ENGLISH_UPSIDE_DOWN: Language = register("en_ud", "en")
    val ANGLISH: Language = register("enp", "en")
    val SHAKESPEAREAN: Language = register("enws", "en")
    val ESPERANTO_GENERIC: Language = register("eo")
    val ESPERANTO: Language = register("eo_uy", "eo")
    val ESTONIAN_GENERIC: Language = register("et")
    val ESTONIAN: Language = register("et_ee", "et")
    val FAROESE_GENERIC: Language = register("fo")
    val FAROESE: Language = register("fo_fo", "fo")
    val FILIPINO_GENERIC: Language = register("fil")
    val FILIPINO: Language = register("fil_ph", "fil")
    val FINNISH_GENERIC: Language = register("fi")
    val FINNISH: Language = register("fi_fi", "fi")
    val FRENCH_GENERIC: Language = register("fr")
    val FRENCH: Language = register("fr_fr", "fr")
    val FRENCH_CANADAIAN: Language = register("fr_ca", "fr")
    val BRETON: Language = register("br_fr", "fr")
    val FRISIAN_GENERIC: Language = register("fy")
    val FRISIAN: Language = register("fy_nl", "fy")
    val GERMAN_GENERIC: Language = register("de")
    val GERMAN: Language = register("de_de", "de")
    val AUSTRIAN: Language = register("de_at", "de")
    val SWISS: Language = register("de_ch", "de")
    val EAST_FRANCONIAN: Language = register("fra_de", "de")
    val LOW_GERMAN: Language = register("nds_de", "de")
    val GREEK_GENERIC: Language = register("gr")
    val GREEK: Language = register("el_gr", "gr")
    val INDONESIAN_GENERIC: Language = register("id")
    val INDONESIAN: Language = register("id_id", "id")
    val IRISH_GENERIC: Language = register("ga")
    val IRISH: Language = register("ga_ie", "ga")
    val ITALIAN_GENERIC: Language = register("it")
    val ITALIAN: Language = register("it_it", "it")
    val JAPANESE_GENERIC: Language = register("jp")
    val JAPANESE: Language = register("ja_jp", "jp")
    val KABYLE_GENERIC: Language = register("kab")
    val KABYLE: Language = register("kab_kab", "kab")
    val KOREAN_GENERIC: Language = register("kr")
    val KOREAN_HANGUG: Language = register("ko_kr", "kr")
    val LATIN_GENERIC: Language = register("la")
    val LATIN: Language = register("la_la", "la")
    val LATVIAN_GENERIC: Language = register("lv")
    val LATVIAN: Language = register("lv_lv")
    val LIMBURGISH_GENERIC: Language = register("li")
    val LIMBURGISH: Language = register("li_li", "li")
    val LITHUANIAN_GENERIC: Language = register("lt")
    val LITHUANIAN: Language = register("lt_lt", "lt")
    val MACEDONIAN_GENERIC: Language = register("mk")
    val MACEDONIAN: Language = register("mk_mk", "mk")
    val MALTESE_GENERIC: Language = register("mt")
    val MALTESE: Language = register("mt_mt", "mt")
    val MONGOLIAN_GENERIC: Language = register("mn")
    val MONGOLIAN: Language = register("mn_mn", "mn")
    val PERSIAN_GENERIC: Language = register("fa")
    val PERSIAN: Language = register("fa_ir")
    val POLISH_GENERIC: Language = register("pl")
    val POLISH: Language = register("pl_pl", "pl")
    val PORTUGUESE_GENERIC: Language = register("pt")
    val PORTUGUESE: Language = register("pt_pt", "pt")
    val ROMANIAN_GENERIC: Language = register("ro")
    val ROMANIAN: Language = register("ro_ro")
    val RUSSIAN_GENERIC: Language = register("ru")
    val RUSSIAN: Language = register("ru_ru", "ru")
    val BASHKIR: Language = register("ba_ru", "ru")
    val BELARUSIAN: Language = register("be_by", "ru")
    val BULGARIAN: Language = register("bg_bg", "ru")
    val SCOTTISH_GENERIC: Language = register("gd")
    val SCOTTISH_GAELIC: Language = register("gd_gb", "gd")
    val SOMALI_GENERIC: Language = register("so")
    val SOMALI: Language = register("so_so", "so")
    val ESPANOL_GENERIC: Language = register("es")
    val ESPANOL_ARGENTINA: Language = register("es_ar", "es")
    val ESPANOL_CHILE: Language = register("es_cl", "es")
    val ESPANOL_EQUADOR: Language = register("es_ec", "es")
    val ESPANOL_ESPANA: Language = register("es_es", "es")
    val ESPANOL_MEXICO: Language = register("es_mx", "es")
    val ESPANOL_URUGUAY: Language = register("es_uy", "es")
    val ESPANOL_VENEZUELA: Language = register("es_ve", "es")
    val ASTURIAN: Language = register("ast_es", "es")
    val BASQUE: Language = register("eu_es", "es")
    val CATALAN: Language = register("ca_es", "es")
    val THAI_GENERIC: Language = register("th")
    val THAI: Language = register("th_th", "th")
    val TURKISH_GENERIC: Language = register("tr")
    val TURKISH: Language = register("tr_tr", "tr")
    val WELSH_GENERIC: Language = register("cy")
    val WELSH: Language = register("cy_gb", "cy")
    val ANDALUSIAN: Language = register("esan")
    val BAVARIAN: Language = register("bar")
    val BRABANTIAN: Language = register("brb")
    val GALICIAN: Language = register("gl_es")
    val GOTHIC: Language = register("got_de")
    val ALBANIAN: Language = register("sq_al")
    val ALLGOVIAN_GERMAN: Language = register("swg")
    val ARMENIAN: Language = register("hy_am")
    val BRAZILIAN_PORTUGUESE: Language = register("pt_br", "pt")
    val CORNISH: Language = register("kw_gb")
    val CROATIAN: Language = register("hr_hr")
    val ELFDALIAN: Language = register("ovd")
    val GEORGIAN: Language = register("ka_ge")
    val HAWAIIAN: Language = register("haw_us")
    val HEBREW: Language = register("he_il")
    val HINDI: Language = register("hi_in")
    val HUNGARIAN: Language = register("hu_hu")
    val ICELANDIC: Language = register("is_is")
    val IDO: Language = register("io_en")
    val IGBO: Language = register("ig_ng")
    val INTERSLAVIC: Language = register("isv")
    val KANNADA: Language = register("kn_in")
    val KAZAKH: Language = register("kk_kz")
    val KLINGON: Language = register("til_aa")
    val KOLSCH_RIPUARIAN: Language = register("ksh")
    val LOJBAN: Language = register("jbo_en")
    val LOLCAT: Language = register("lol_us")
    val LUXEMBOURGISH: Language = register("lb_lu")
    val MALAY: Language = register("ms_my")
    val MANX: Language = register("gv_im")
    val MOHAWK: Language = register("moh_ca")
    val MAON: Language = register("mi_nz")
    val NORTHERN_SAMI: Language = register("sme")
    val NORTHERN_BOKMAL: Language = register("nb_no")
    val NORWEGIAN_NYNORSK: Language = register("nn_no")
    val NUUCHAHNULTH: Language = register("nuk")
    val OCCITAN: Language = register("oc_fr")
    val OJIBWE: Language = register("oj_ca")
    val QUENYA: Language = register("qya_aa")
    val SICILIAN: Language = register("scn")
    val SLOVAK: Language = register("sk_sk")
    val SLOVENIAN: Language = register("sl_si")
    val SERBIAN: Language = register("sr_sp")
    val SWEDISH: Language = register("sv_se")
    val UPPER_SAXON_GERMAN: Language = register("sxu")
    val SILESIAN: Language = register("szl")
    val TAMIL: Language = register("ta_in")
    val TATAR: Language = register("tt_ru")
    val TALOSSAN: Language = register("tzl_tzl")
    val UKRAINIAN: Language = register("uk_ua")
    val VALENCIAN: Language = register("val_es")
    val VENETIAN: Language = register("vec_it")
    val VIETNAMESE: Language = register("vi_vn")
    val YIDDISH: Language = register("yi_de")
    val YORUBA: Language = register("yo_ng")
}
