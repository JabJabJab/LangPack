package jab.langpack.core.objects

import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.loader.ComplexLoader
import jab.langpack.core.util.StringUtil
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * The **LangGroup** class is a nestable container for objects stored as fields. Groups have the ability to be modified
 * and appended.
 *
 * Fields are noted as lower-case strings. Any fields passed with upper-case characters are forced as lowered when
 * stored. Fields can reference nested objects using periods as a delimiter.
 * ###
 * **Examples**:
 * - **a_field**
 * - **a_section.a_field**
 *
 * @author Jab
 *
 * @property pack The lang-pack instance.
 * @property language TODO: Document.
 * @property name The name of the group.
 * @property parent (Optional) The parent group.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Group(var pack: LangPack, val language: Language, val name: String, var parent: Group? = null) {

    /**
     * The metadata for the lang group. (Used for imports)
     */
    val meta = Metadata()

    /**
     * TODO: Document.
     */
    val children = HashMap<String, Group>()

    /**
     * The stored fields for the lang group. Fields are stored as lower-case.
     */
    val fields = HashMap<String, Definition<*>>()

    /**
     * Appends YAML data by reading it and adding it to the lang group.
     *
     * @param cfg The YAML data to read.
     *
     * @return Returns the instance for single-line executions.
     */
    fun append(cfg: ConfigurationSection): Group {
        read(cfg, meta)
        return this
    }

    /**
     * Reads YAML data, processing it into the lang group.
     *
     * @param cfg The YAML data to read.
     * @param metadata The metadata object to process while reading the YAML data.
     *
     * @return Returns the instance for single-line executions.
     */
    fun read(cfg: ConfigurationSection, metadata: Metadata = Metadata()): Group {

        if (cfg.isConfigurationSection("__metadata__")) {
            metadata.read(cfg.getConfigurationSection("__metadata__")!!)

            // Load imports prior to in-file fields, potentially overriding a import.
            if (metadata.imports.isNotEmpty()) {

                val localDir = pack.dir

                for (next in metadata.imports) {

                    var import = next
                    if (!import.endsWith(".yml", true)) {
                        import += ".yml"
                    }

                    // Try local file paths first.
                    var importFile = File(localDir, import)
                    if (importFile.exists()) {

                        if (pack.debug) {
                            println("[$name] :: Loading import: ${importFile.path}")
                        }

                        read(YamlConfiguration.loadConfiguration(importFile))
                        continue
                    }

                    // Try absolute path second.
                    importFile = File(import)
                    if (!importFile.exists()) {

                        if (pack.debug) {
                            System.err.println("Cannot import language file: $import (Not found)")
                        }

                        continue
                    }

                    if (pack.debug) {
                        println("[$name] :: Loading import: ${importFile.path}")
                    }

                    read(YamlConfiguration.loadConfiguration(importFile))
                }
            }

        }

        for (key in cfg.getKeys(false)) {

            // Exclude the metadata group.
            if (key.equals("__metadata__", true)) {
                continue
            }

            if (cfg.isConfigurationSection(key)) {

                val group = cfg.getConfigurationSection(key)!!
                if (group.contains("type")) {
                    readComplex(group)
                } else {
                    readSection(group)
                }

            } else {
                val raw = StringUtil.toAString(cfg.get(key)!!)
                set(key, LangString(pack, this, raw))
            }
        }

        return this
    }

    /**
     * TODO: Document.
     *
     * @param group
     */
    fun append(group: Group) {
        children[group.name] = group
    }

    /**
     * Processes a YAML section as a lang group.
     *
     * @param cfg The YAML section to read.
     */
    private fun readSection(cfg: ConfigurationSection) {
        val langSection = Group(pack, language, cfg.name.toLowerCase(), this)
        langSection.read(cfg, Metadata())
        append(langSection)
    }

    /**
     * Attempts to read a YAML section as a complex object. All complex objects are YAML sections with a defined
     * **type** string. If the field doesn't exist or is not a string, the YAML section is loaded as a lang group.
     *
     * @param cfg The YAML section to read.
     */
    private fun readComplex(cfg: ConfigurationSection) {

        if (!cfg.contains("type") || !cfg.isString("type")) {
            readSection(cfg)
            return
        }

        val type = cfg.getString("type")!!
        val loader = ComplexLoader.get(type)
        if (loader != null) {
            set(cfg.name, LangComplex(pack, this, loader.load(cfg)))
        } else {
            System.err.println("Unknown complex type: $type")
        }
    }

    /**
     * Attempts to locate a stored value with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun resolve(query: String): Definition<*>? {

        if (pack.debug) {
            println("[$name] :: resolve($query)")
        }

        if (query.contains(".")) {

            val split = query.split(".")

            val groupId = split[0]

            // Make sure
            val raw = children[groupId.toLowerCase()] ?: return null

            var rebuiltQuery = split[1]
            if (split.size > 2) {
                for (index in 2..split.lastIndex) {
                    rebuiltQuery += ".${split[index]}"
                }
            }

            if (pack.debug) {
                println("[$name] :: rebuiltQuery: $rebuiltQuery")
            }

            return raw.resolve(rebuiltQuery)

        } else {
            return fields[query.toLowerCase()]
        }
    }

    /**
     * TODO: Document.
     */
    fun walk() {
        for ((_, child) in children) child.walk()
        for ((_, field) in fields) field.walk()
    }

    /**
     * TODO: Document.
     */
    fun unWalk() {
        for ((_, child) in children) child.unWalk()
        for ((_, field) in fields) field.unWalk()
    }

    /**
     * Attempts to resolve a string with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun getString(query: String): String? {
        val o = resolve(query) ?: return null
        if (o is LangComplex) {
            return o.value.get().toString()
        } else if (o is LangString) {
            return o.value
        }
        return null
    }

    /**
     * Attempts to resolve a lang group with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query.
     *
     * @throws RuntimeException Thrown if the query is unresolved or the resolved object is not a lang group.
     */
    fun getSection(query: String): Group {

        val value = resolve(query)

        if (value == null || value !is Group) {
            throw RuntimeException("The field $query is not a LangSection.")
        }

        return value
    }

    /**
     * Attempts to resolve a string pool with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query.
     *
     * @throws RuntimeException Thrown if the query is unresolved or the resolved object is not a lang group.
     */
    fun getStringPool(query: String): StringPool {

        val value = resolve(query)

        if (value == null || value !is StringPool) {
            throw RuntimeException("The field $query is not a StringPool.")
        }

        return value
    }

    /**
     * Attempts to resolve an action text with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query.
     *
     * @throws RuntimeException Thrown if the query is unresolved or the resolved object is not a lang group.
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
    fun set(key: String, value: Definition<*>?) {

        if (pack.debug) {
            println("[$name] :: set($key, $value)")
        }

        if (key.contains(".")) {

            val split = key.split(".")
            val groupId = split[0].toLowerCase()

            var raw = children[groupId]
            if (raw !is Group) {
                raw = Group(pack, language, groupId, this)
                children[groupId] = raw
            }

            var rebuiltQuery = split[1]
            if (split.size > 2) {
                for (index in 2..split.lastIndex) {
                    rebuiltQuery += ".${split[index]}"
                }
            }

            return raw.set(rebuiltQuery, value)

        } else {
            if (value != null) {
                fields[key.toLowerCase()] = value
            } else {
                fields.remove(key.toLowerCase())
            }
        }
    }

    /**
     * TODO: Document.
     *
     * @param id
     */
    fun remove(id: String) {
        fields.remove(id.toLowerCase())
    }

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves.
     */
    fun contains(query: String): Boolean = fields.containsKey(query.toLowerCase())

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves and is the type [Complex].
     */
    fun isComplex(query: String): Boolean = contains(query) && fields[query.toLowerCase()] is Complex<*>

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves and is the type [StringPool].
     */
    fun isStringPool(query: String): Boolean = contains(query) && fields[query.toLowerCase()] is StringPool

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves and is the type [ActionText].
     */
    fun isActionText(query: String): Boolean = contains(query) && fields[query.toLowerCase()] is ActionText

    /**
     * The ***Metadata*** class handles all metadata defined for lang groups.
     *
     * Metadata is formed by creating a YAML section inside of the lang group.
     *
     * Example:
     * ```yml
     * group:
     *   __metadata__:
     *     import: ..
     * ```
     *
     * Metadata supports importing from other lang-files. Formats are as follows:
     * - **import:** Imports only one file as a string.
     * - **imports:** Imports multiple files as a string-list.
     *
     * Additionally, file-names can be given without extensions, however the files stored must have the .yml extension.
     * Files can be referenced by name in the same location as the lang-pack's directory. Otherwise, Java-File supported
     * paths will be tried to locate the lang-file.
     *
     * @author Jab
     */
    class Metadata {

        /**
         * The import files defined in the metadata.
         */
        val imports = ArrayList<String>()

        /**
         * Reads a YAML section as metadata.
         *
         * @param cfg The YAML section to read.
         */
        fun read(cfg: ConfigurationSection) {
            if (cfg.contains("imports")) {
                if (cfg.isList("imports")) {
                    for (next in cfg.getStringList("imports")) {
                        if (next != null) {
                            imports.add(next)
                        }
                    }
                }
            } else if (cfg.contains("import")) {
                if (cfg.isString("import")) {
                    imports.add(cfg.getString("import")!!)
                }
            }
        }
    }
}
