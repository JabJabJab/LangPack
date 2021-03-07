package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangFile
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import jab.langpack.commons.util.StringUtil
import org.bukkit.configuration.ConfigurationSection
import java.util.*

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class StringPool : LangComplex {

    /**
     * TODO: Document.
     */
    val mode: Mode

    /**
     * TODO: Document.
     */
    var random: Random

    private var strings: Array<String> = emptyArray()
    private var index: Int = 0

    /**
     * Basic constructor.
     *
     * @param mode (Optional) The mode of the StringPool. (DEFAULT: [Mode.RANDOM])
     * @param random (Optional) The random instance to use.
     */
    constructor(mode: Mode = Mode.RANDOM, random: Random = LangPack.DEFAULT_RANDOM) {
        this.mode = mode
        this.random = random
    }

    /**
     * [LangFile] import constructor.
     *
     * @param cfg The ConfigurationSection to load.
     */
    constructor(cfg: ConfigurationSection) {
        var mode: Mode = Mode.RANDOM
        this.random = LangPack.DEFAULT_RANDOM

        // Load the mode if defined.
        if (cfg.contains("mode")) {
            val modeCheck: Mode? = Mode.getType(cfg.getString("mode")!!)
            if (modeCheck == null) {
                System.err.println("""The mode "$mode" is an invalid StringPool mode. Using ${mode.name}.""")
            } else {
                mode = modeCheck
            }
        }
        this.mode = mode

        val list = cfg.getList("pool")!!
        if (list.isNotEmpty()) {
            for (o in list) {
                if (o != null) {
                    add(StringUtil.toAString(o))
                } else {
                    add("")
                }
            }
        }
    }

    override fun process(pkg: LangPack, lang: Language, vararg args: LangArg): String {
        return if (strings.isEmpty()) {
            ""
        } else {
            pkg.processor.processString(poll(), pkg, lang, *args)
        }
    }

    override fun get(): String {
        return if (strings.isEmpty()) {
            ""
        } else {
            poll()
        }
    }

    /**
     * @return Returns the next result in the pool.
     */
    fun poll(): String {
        if (strings.isEmpty()) {
            throw RuntimeException("The StringPool is empty and cannot poll.")
        }
        return strings[roll()]
    }

    /**
     * TODO: Document.
     *
     * @return
     */
    fun roll(): Int {
        if (strings.isEmpty()) {
            return -1
        }

        when (mode) {
            Mode.RANDOM -> {
                return random.nextInt(strings.size)
            }
            Mode.SEQUENTIAL -> {
                val result = index++
                if (index == strings.size) {
                    index = 0
                }
                return result
            }
            Mode.SEQUENTIAL_REVERSED -> {
                val result = index--
                if (index == -1) {
                    index = strings.lastIndex
                }
                return result
            }
        }
    }

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun add(string: String) {
        if (strings.isEmpty()) {
            strings = arrayOf(string)
            return
        }

        strings = strings.plus(string)

        index = if (mode == Mode.SEQUENTIAL_REVERSED) {
            strings.lastIndex
        } else {
            0
        }
    }

    /**
     * TODO: Document.
     */
    fun clear() {
        strings = emptyArray()
        index = 0
    }

    /**
     * @return Returns true if the StringPool is empty.
     */
    fun isEmpty(): Boolean {
        return strings.isNullOrEmpty()
    }

    companion object {

        /**
         * TODO: Document.
         *
         * @param cfg
         *
         * @return
         */
        fun read(cfg: ConfigurationSection): StringPool {
            var mode: Mode = Mode.RANDOM

            // Load the mode if defined.
            if (cfg.contains("mode")) {
                val modeCheck: Mode? = Mode.getType(cfg.getString("mode")!!)
                if (modeCheck == null) {
                    System.err.println("""The mode "$mode" is an invalid StringPool mode. Using ${mode.name}.""")
                } else {
                    mode = modeCheck
                }
            }

            val pool = StringPool(mode)
            val list = cfg.getList("pool")!!
            if (list.isNotEmpty()) {
                for (o in list) {
                    if (o != null) {
                        pool.add(StringUtil.toAString(o))
                    } else {
                        pool.add("")
                    }
                }
            }
            return pool
        }
    }

    /**
     * TODO: Document.
     *
     * @author Jab
     */
    enum class Mode {
        RANDOM,
        SEQUENTIAL,
        SEQUENTIAL_REVERSED;

        companion object {

            /**
             * TODO: Document.
             *
             * @param mode
             */
            fun getType(mode: String): Mode? {
                if (mode.isNotEmpty()) {
                    for (next in values()) {
                        if (next.name.equals(mode, true)) {
                            return next
                        }
                    }
                }
                return null
            }
        }
    }
}
