package jab.langpack.core.objects

import jab.langpack.core.LangPack
import jab.langpack.core.Language
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileNotFoundException

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **LangFile** class acts as a lang section that identifies with a language. The lang-files are used to
 * resolve dialog based on language.
 *
 * @author Jab
 */
class LangFile : LangGroup {

    /**
     * The file storing the YAML data.
     */
    var file: File? = null

    /**
     * Runtime constructor.
     *
     * @param pack The lang-pack instance.
     * @param language The language of the file.
     * @param name The name of the section.
     */
    constructor(pack: LangPack, language: Language, name: String) : super(pack, language, name)

    /**
     * File constructor.
     *
     * @param pack The lang-pack instance.
     * @param file The file to read.
     * @param language The language of the file.
     */
    constructor(pack: LangPack, file: File, language: Language) : super(pack, language, file.nameWithoutExtension) {

        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }

        this.file = file
    }

    override fun toString(): String = "LangFile(lang=$language)"

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
