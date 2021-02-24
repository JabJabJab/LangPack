package jab.spigot.language

import jab.spigot.language.util.StringPool
import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param file
 * @param language
 */
class LangFile(val file: File, val language: Language) {

    /** TODO: Document. */
    private val mapEntries: HashMap<String, Any> = HashMap()

    /** TODO: Document. */
    var yaml: YamlConfiguration
        private set

    init {
        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }
        yaml = YamlConfiguration.loadConfiguration(file)
    }

    /**
     * TODO: Document.
     */
    fun load() {

        // Clear the current entries and reload from file.
        mapEntries.clear()
        readFile()

        for (key in yaml.getKeys(false)) {
            if (yaml.isConfigurationSection(key)) {
                set(key, StringPool.read(yaml.getConfigurationSection(key)!!))
            } else {
                set(key, LangPackage.toAString(yaml.get(key)!!))
            }
        }

        // Load default variables if in English.
        if (language == Language.ENGLISH) {
            setEnglishDefaults()
        }
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
                set(key, StringPool.read(yaml.getConfigurationSection(key)!!))
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
    fun get(key: String): String? {
        val keyLower = key.toLowerCase()
        if (mapEntries.containsKey(keyLower)) {
            val o = mapEntries[keyLower]
            return if (o is StringPool) {
                o.roll()
            } else if (o is String) {
                o
            } else if (o != null) {
                LangPackage.toAString(o)
            } else {
                null
            }
        }
        return null
    }

    /**
     * Reads and loads the file into a YamlConfiguration instance.
     *
     * @return Returns the loaded YamlConfiguration
     */
    fun readFile(): YamlConfiguration {
        yaml = YamlConfiguration.loadConfiguration(file)
        return yaml
    }

    /**
     * Assigns a value with a ID.
     *
     * @param key The ID to assign the value.
     * @param value The value to assign to the ID.
     */
    fun set(key: String, value: Any) {
        mapEntries[key.toLowerCase()] = value
    }

    /**
     * Sets the default global variables for the [Language.ENGLISH] LangFiles.
     */
    private fun setEnglishDefaults() {
        set("black", ChatColor.BLACK)
        set("blue", ChatColor.DARK_BLUE)
        set("green", ChatColor.DARK_GREEN)
        set("cyan", ChatColor.DARK_AQUA)
        set("aqua", ChatColor.DARK_AQUA)
        set("red", ChatColor.DARK_RED)
        set("purple", ChatColor.DARK_PURPLE)
        set("pink", ChatColor.LIGHT_PURPLE)
        set("gold", ChatColor.GOLD)
        set("gray", ChatColor.DARK_GRAY)
        set("light_gray", ChatColor.GRAY)
        set("light_blue", ChatColor.BLUE)
        set("light_green", ChatColor.GREEN)
        set("light_cyan", ChatColor.AQUA)
        set("light_aqua", ChatColor.AQUA)
        set("light_red", ChatColor.RED)
        set("light_purple", ChatColor.LIGHT_PURPLE)
        set("yellow", ChatColor.YELLOW)
        set("white", ChatColor.WHITE)
        set("magic", ChatColor.MAGIC)
        set("bold", ChatColor.BOLD)
        set("strike", ChatColor.STRIKETHROUGH)
        set("underline", ChatColor.UNDERLINE)
        set("italic", ChatColor.ITALIC)
        set("reset", ChatColor.RESET)
        set("color_code", ChatColor.COLOR_CHAR)
    }
}