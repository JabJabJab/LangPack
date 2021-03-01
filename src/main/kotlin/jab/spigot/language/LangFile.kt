package jab.spigot.language

import jab.spigot.language.`object`.ActionText
import jab.spigot.language.`object`.LangComplex
import jab.spigot.language.`object`.LangComponent
import jab.spigot.language.`object`.StringPool
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class LangFile {

    var file: File? = null
    val lang: Language

    /** TODO: Document. */
    private val fields: HashMap<String, Any> = HashMap()

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
        fields.clear()
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
        if (fields.containsKey(keyLower)) {
            val o = fields[keyLower]
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
        return fields[key.toLowerCase()]
    }

    /**
     * Assigns a value with a ID.
     *
     * @param key The ID to assign the value.
     * @param value The value to assign to the ID.
     */
    fun set(key: String, value: Any?) {
        if (value != null) {
            fields[key.toLowerCase()] = value
        } else {
            fields.remove(key.toLowerCase())
        }
    }

    fun contains(field: String): Boolean {
        return fields.containsKey(field.toLowerCase())
    }

    fun isComplex(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is LangComplex
    }

    fun isLangComponent(field: String): Boolean {
        val value = fields[field] ?: return false
        return value is LangComponent
    }

    fun isStringPool(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is StringPool
    }

    fun isActionText(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is ActionText
    }

    fun getStringPool(field: String): StringPool {
        val value = fields[field.toLowerCase()]
        if (value == null || value !is StringPool) {
            throw RuntimeException("The field $field is not a StringPool.")
        }
        return value
    }

    fun getActionText(field: String): ActionText {
        val value = fields[field.toLowerCase()]
        if (value == null || value !is ActionText) {
            throw RuntimeException("The field $field is not a ActionText.")
        }
        return value
    }
}