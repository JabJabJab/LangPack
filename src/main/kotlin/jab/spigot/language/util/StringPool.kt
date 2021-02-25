package jab.spigot.language.util

import jab.spigot.language.LangPackage
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param mode
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class StringPool(val mode: Mode) {

    var random = LangPackage.DEFAULT_RANDOM
    private var strings: Array<String> = emptyArray()
    private var index: Int = 0

    override fun toString(): String {
        return roll()
    }

    /**
     * TODO: Document.
     *
     * @return
     */
    fun roll(): String {
        if (strings.isEmpty()) {
            return ""
        }

        when (mode) {
            Mode.RANDOM -> {
                return strings[random.nextInt(strings.size)]
            }
            Mode.SEQUENTIAL -> {
                val result: String = strings[index++]
                if (index == strings.size) {
                    index = 0
                }
                return result
            }
            Mode.SEQUENTIAL_REVERSED -> {
                val result: String = strings[index--]
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
                        pool.add(LangPackage.toAString(o))
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