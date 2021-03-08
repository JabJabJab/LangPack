package jab.langpack.commons

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * The **LangFile** class acts as a lang section that identifies with a language. The lang-files are used to
 * resolve dialog based on language.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class LangFile : LangSection {

    /**
     * The file storing the YAML data.
     */
    var file: File? = null

    /**
     * The language that represents the file.
     */
    val lang: Language

    /**
     * Runtime constructor.
     *
     * @param pack The lang-pack instance.
     * @param lang The language of the file.
     * @param name The name of the section.
     */
    constructor(pack: LangPack, lang: Language, name: String) : super(pack, name) {
        this.lang = lang
    }

    /**
     * File constructor.
     *
     * @param pack The lang-pack instance.
     * @param file The file to read.
     * @param lang The language of the file.
     */
    constructor(pack: LangPack, file: File, lang: Language) : super(pack, file.nameWithoutExtension) {

        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }

        this.file = file
        this.lang = lang
    }

    override fun toString(): String = "LangFile(lang=$lang)"

    /**
     * Attempts to parse the File to YAML data to read as a lang section.
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
     * Appends a YAML file to the lang-file.
     *
     * @param file The file to parse and append.
     *
     * @return Returns the instance of the file for single-line executions.
     */
    fun append(file: File): LangFile {
        read(YamlConfiguration.loadConfiguration(file))
        return this
    }
}
