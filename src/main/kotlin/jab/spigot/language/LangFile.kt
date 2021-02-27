package jab.spigot.language

import jab.spigot.language.util.LangComplex
import jab.spigot.language.util.StringPool
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate")
class LangFile {

    var file: File? = null
    val lang: Language

    /** TODO: Document. */
    private val mapEntries: HashMap<String, Any> = HashMap()

    /** TODO: Document. */
    var yaml: YamlConfiguration? = null
        private set

    constructor(lang: Language) {
        this.lang = lang
    }

    constructor(file: File, lang: Language) {
        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }
        this.file = file
        this.lang = lang

        yaml = YamlConfiguration.loadConfiguration(file)
    }

    /**
     * TODO: Document.
     */
    fun load() {

        // Clear the current entries and reload from file.
        mapEntries.clear()
        readFile()

        if (yaml != null) {
            val yaml = yaml!!
            for (key in yaml.getKeys(false)) {
                if (yaml.isConfigurationSection(key)) {
                    val cfg = yaml.getConfigurationSection(key)!!

                    // Make sure that the type is defined.
                    if (!cfg.contains("type") || !cfg.isString("type")) {
                        LPPlugin.instance?.logger?.warning("Unknown complex type: [Not defined]")
                        continue
                    }

                    val type = cfg.getString("type")!!
                    when {
                        type.equals("ActionText", true) || type.equals("Action Text", true) || type.equals(
                            "Action_Text",
                            true
                        ) -> {
                            set(key, ActionText(cfg))
                        }
                        type.equals("StringPool", true) || type.equals("String Pool", true) || type.equals(
                            "Pool",
                            true
                        ) -> {
                            set(key, StringPool.read(cfg))
                        }
                        else -> {
                            LPPlugin.instance?.logger?.warning("Unknown complex type: $type")
                        }
                    }

                } else {
                    set(key, LangPackage.toAString(yaml.get(key)!!))
                }
            }
        }

//        // Load default variables if in English.
//        if (lang == Language.ENGLISH) {
//            setEnglishDefaults()
//        }
    }

    /**
     * Appends another LangFile's contents to this LangFile.
     *
     * @param file The file handle.
     */
    fun append(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        for (key in yaml.getKeys(false)) {
            if (yaml.isConfigurationSection(key)) {
                val cfg = yaml.getConfigurationSection(key)!!

                // Make sure that the type is defined.
                if (!cfg.contains("type") || !cfg.isString("type")) {
                    LPPlugin.instance?.logger?.warning("Unknown complex type: [Not defined]")
                    continue
                }

                val type = cfg.getString("type")!!
                when {
                    type.equals("ActionText", true) || type.equals("Action Text", true) || type.equals(
                        "Action_Text",
                        true
                    ) -> {
                        set(key, ActionText(cfg))
                    }
                    type.equals("StringPool", true) || type.equals("String Pool", true) || type.equals(
                        "Pool",
                        true
                    ) -> {
                        set(key, StringPool.read(cfg))
                    }
                    else -> {
                        LPPlugin.instance?.logger?.warning("Unknown complex type: $type")
                    }
                }

            } else {
                set(key, LangPackage.toAString(yaml.get(key)!!))
            }
        }
    }

    /**
     * @param key The id of the entry.
     *
     * @return Returns the entry with the given id. If no entry is registered with the given id, null
     *     is returned.
     */
    fun getString(key: String, pkg: LangPackage, lang: Language, vararg args: LangArg): String? {
        val keyLower = key.toLowerCase()
        if (mapEntries.containsKey(keyLower)) {
            val o = mapEntries[keyLower]
            return when {
                o is String -> {
                    o
                }
                o is LangComplex -> {
                    o.process(pkg, lang, *args)
                }
                o != null -> {
                    LangPackage.toAString(o)
                }
                else -> {
                    null
                }
            }
        }
        return null
    }

    /**
     * Reads and loads the file into a YamlConfiguration instance.
     *
     * @return Returns the loaded YamlConfiguration
     */
    fun readFile(): YamlConfiguration? {
        if (file != null) {
            yaml = YamlConfiguration.loadConfiguration(file!!)
        }
        return yaml
    }

    fun get(key: String): Any? {
        return mapEntries[key.toLowerCase()]
    }

    /**
     * Assigns a value with a ID.
     *
     * @param key The ID to assign the value.
     * @param value The value to assign to the ID.
     */
    fun set(key: String, value: Any?) {
        if (value != null) {
            mapEntries[key.toLowerCase()] = value
        } else {
            mapEntries.remove(key.toLowerCase())
        }
    }

    fun contains(field: String): Boolean {
        return mapEntries.containsKey(field.toLowerCase())
    }

    fun isComplex(field: String): Boolean {
        return contains(field) && mapEntries[field.toLowerCase()] is LangComplex
    }

    fun isStringPool(field: String): Boolean {
        return contains(field) && mapEntries[field.toLowerCase()] is StringPool
    }

    fun isActionText(field: String): Boolean {
        return contains(field) && mapEntries[field.toLowerCase()] is ActionText
    }

    fun getStringPool(field: String): StringPool {
        val value = mapEntries[field.toLowerCase()]
        if (value == null || value !is StringPool) {
            throw RuntimeException("The field $field is not a StringPool.")
        }
        return value
    }

    fun getActionText(field: String): ActionText {
        val value = mapEntries[field.toLowerCase()]
        if (value == null || value !is ActionText) {
            throw RuntimeException("The field $field is not a ActionText.")
        }
        return value
    }

//    /**
//     * Sets the default global variables for the [Language.ENGLISH] LangFiles.
//     */
//    private fun setEnglishDefaults() {
//        set("black", ChatColor.BLACK)
//        set("blue", ChatColor.DARK_BLUE)
//        set("green", ChatColor.DARK_GREEN)
//        set("cyan", ChatColor.DARK_AQUA)
//        set("aqua", ChatColor.DARK_AQUA)
//        set("red", ChatColor.DARK_RED)
//        set("purple", ChatColor.DARK_PURPLE)
//        set("pink", ChatColor.LIGHT_PURPLE)
//        set("gold", ChatColor.GOLD)
//        set("gray", ChatColor.DARK_GRAY)
//        set("light_gray", ChatColor.GRAY)
//        set("light_blue", ChatColor.BLUE)
//        set("light_green", ChatColor.GREEN)
//        set("light_cyan", ChatColor.AQUA)
//        set("light_aqua", ChatColor.AQUA)
//        set("light_red", ChatColor.RED)
//        set("light_purple", ChatColor.LIGHT_PURPLE)
//        set("yellow", ChatColor.YELLOW)
//        set("white", ChatColor.WHITE)
//        set("magic", ChatColor.MAGIC)
//        set("bold", ChatColor.BOLD)
//        set("strike", ChatColor.STRIKETHROUGH)
//        set("underline", ChatColor.UNDERLINE)
//        set("italic", ChatColor.ITALIC)
//        set("reset", ChatColor.RESET)
//        set("color_code", ChatColor.COLOR_CHAR)
//    }
}