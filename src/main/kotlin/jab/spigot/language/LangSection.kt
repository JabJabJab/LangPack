package jab.spigot.language

import jab.spigot.language.`object`.ActionText
import jab.spigot.language.`object`.LangComplex
import jab.spigot.language.`object`.LangComponent
import jab.spigot.language.`object`.StringPool
import jab.spigot.language.util.StringUtil
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property pkg
 * @property parent
 */
@Suppress("unused")
open class LangSection(var pkg: LangPackage, val name: String, var parent: LangSection? = null) {

    /**
     * TODO: Document.
     */
    val fields = HashMap<String, Any>()

    /**
     * TODO: Document.
     *
     * @param cfg
     *
     * @return Returns the instance for single-line executions.
     */
    fun append(cfg: ConfigurationSection): LangSection {
        read(cfg, Metadata())
        return this
    }

    /**
     * TODO: Document.
     *
     * @param cfg
     * @param metadata
     *
     * @return Returns the instance for single-line executions.
     */
    fun read(cfg: ConfigurationSection, metadata: Metadata = Metadata()): LangSection {

        if (cfg.isConfigurationSection("__metadata__")) {
            metadata.read(cfg.getConfigurationSection("__metadata__")!!)

            // Load imports prior to in-file fields, potentially overriding a import.
            if (metadata.imports.isNotEmpty()) {
                val localDir = pkg.dir

                for (next in metadata.imports) {

                    var import = next
                    if (!import.endsWith(".yml", true)) {
                        import += ".yml"
                    }

                    // Try local file paths first.
                    var importFile = File(localDir, import)
                    if (importFile.exists()) {
                        read(
                            YamlConfiguration.loadConfiguration(importFile),
                            Metadata()
                        )
                        continue
                    }

                    // Try absolute path second.
                    importFile = File(import)
                    if (!importFile.exists()) {
                        System.err.println("Cannot import language file: $import (Not found)")
                        continue
                    }

                    read(YamlConfiguration.loadConfiguration(importFile), Metadata())
                }
            }

        }

        fun readSection(section: ConfigurationSection) {
            val langSection = LangSection(pkg, section.name, this)
            langSection.read(section, Metadata())
            set(section.name, langSection)
        }

        fun readComplex(section: ConfigurationSection) {

            if (!section.contains("type") || !section.isString("type")) {
                readSection(section)
                return
            }

            val type = section.getString("type")!!
            when {
                type.equals("ActionText", true) || type.equals("Action Text", true) || type.equals(
                    "Action_Text",
                    true
                ) || type.equals("Text", true) -> {
                    set(section.name, ActionText(section))
                }
                type.equals("StringPool", true) || type.equals("String Pool", true) || type.equals(
                    "Pool",
                    true
                ) -> {
                    set(section.name, StringPool.read(section))
                }
                else -> {
                    System.err.println("Unknown complex type: $type")
                }
            }
        }

        for (key in cfg.getKeys(false)) {

            // Exclude the metadata section.
            if (key.equals("__metadata__", true)) {
                continue
            }

            if (cfg.isConfigurationSection(key)) {

                val section = cfg.getConfigurationSection(key)!!
                if (section.contains("type")) {
                    readComplex(section)
                } else {
                    readSection(section)
                }

            } else {
                set(key, StringUtil.toAString(cfg.get(key)!!))
            }
        }

        return this
    }

    /**
     * TODO: Document.
     *
     * @param query
     *
     * @return
     */
    fun resolve(query: String): Any? {

        if (query.contains(".")) {

            val split = query.split(".")

            val sectionId = split[0]

            // Make sure
            val raw = fields[sectionId.toLowerCase()]
            if (raw !is LangSection) {
                return null
            }

            var rebuiltQuery = split[1]
            if (split.size > 2) {
                for (index in 2..split.lastIndex) {
                    rebuiltQuery += ".${split[index]}"
                }
            }

            return raw.resolve(rebuiltQuery)

        } else {
            return fields[query.toLowerCase()]
        }
    }

    /**
     * TODO: Document.
     *
     * @param query
     *
     * @return
     */
    fun getString(query: String): String? {
        val o = resolve(query)
        return when {
            o is LangComplex -> o.get()
            o is String -> o
            o != null -> StringUtil.toAString(o)
            else -> null
        }
    }

    /**
     * TODO: Document.
     *
     * @param query
     *
     * @return
     */
    fun getSection(query: String): LangSection {

        val value = resolve(query)

        if (value == null || value !is LangSection) {
            throw RuntimeException("The field $query is not a LangSection.")
        }

        return value
    }

    /**
     * TODO: Document.
     *
     * @param query
     *
     * @return
     */
    fun getStringPool(query: String): StringPool {

        val value = resolve(query)

        if (value == null || value !is StringPool) {
            throw RuntimeException("The field $query is not a StringPool.")
        }

        return value
    }

    /**
     * TODO: Document.
     *
     * @param query
     *
     * @return
     */
    fun getActionText(query: String): ActionText {

        val value = resolve(query)

        if (value == null || value !is ActionText) {
            throw RuntimeException("The field $query is not a ActionText.")
        }

        return value
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

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun contains(field: String): Boolean {
        return fields.containsKey(field.toLowerCase())
    }

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun isComplex(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is LangComplex
    }

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun isLangComponent(field: String): Boolean {
        val value = fields[field] ?: return false
        return value is LangComponent
    }

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun isStringPool(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is StringPool
    }

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun isActionText(field: String): Boolean {
        return contains(field) && fields[field.toLowerCase()] is ActionText
    }

    /**
     * TODO: Document.
     *
     * @author Jab
     */
    class Metadata {

        val imports = ArrayList<String>()

        fun read(cfg: ConfigurationSection) {
            if (cfg.contains("import")) {
                if (cfg.isList("import")) {

                    for (next in cfg.getStringList("import")) {
                        if (next != null) {
                            imports.add(next)
                        }
                    }

                } else if (cfg.isString("import")) {
                    imports.add(cfg.getString("import")!!)
                }
            }
        }
    }
}
