package jab.langpack.commons

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class LangFile : LangSection {

    /**
     * TODO: Document.
     */
    var file: File? = null

    /**
     * TODO: Document.
     */
    val lang: Language

    /**
     * TODO: Document.
     *
     * @param lang
     */
    constructor(pkg: LangPack, lang: Language, name: String) : super(pkg, name) {
        this.lang = lang
    }

    /**
     * TODO: Document.
     *
     * @param file
     * @param lang
     */
    constructor(pkg: LangPack, file: File, lang: Language) : super(pkg, file.nameWithoutExtension) {

        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }

        this.file = file
        this.lang = lang
    }

    override fun toString(): String {
        return "LangFile(lang=$lang)"
    }

    /**
     * TODO: Document.
     *
     * @return Returns the instance of the file for single-line executions.
     */
    fun load(): LangFile {

        // Clear the current entries and reload from file.
        fields.clear()

        if (file != null) {
            read(YamlConfiguration.loadConfiguration(file!!))
        }

        return this
    }

    /**
     * TODO: Document.
     *
     * @param file
     *
     * @return Returns the instance of the file for single-line executions.
     */
    fun append(file: File): LangFile {
        read(YamlConfiguration.loadConfiguration(file))
        return this
    }
}
