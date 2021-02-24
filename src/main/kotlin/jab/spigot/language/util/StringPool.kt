package jab.spigot.language.util

import jab.spigot.language.LangPackage
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param type
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class StringPool(val type: Type) {

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

        when (type) {
            Type.RANDOM -> {
                return strings[random.nextInt(strings.size)]
            }
            Type.SEQUENTIAL -> {
                val result: String = strings[index++]
                if (index == strings.size) {
                    index = 0
                }
                return result
            }
            Type.SEQUENTIAL_REVERSED -> {
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

        index = if (type == Type.SEQUENTIAL_REVERSED) {
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
         *
         * @param cfg
         *
         * @return
         */
        fun read(cfg: ConfigurationSection): StringPool {
            var type: Type = Type.RANDOM

            // Load the type if defined.
            if (cfg.contains("type")) {
                val typeCheck: Type? = Type.getType(cfg.getString("type")!!)
                if (typeCheck == null) {
                    System.err.println("""The type "$type" is an invalid StringPool type. Using ${type.name}.""")
                } else {
                    type = typeCheck
                }
            }

            val pool = StringPool(type)
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
     *
     *
     * @author Jab
     */
    enum class Type {
        RANDOM,
        SEQUENTIAL,
        SEQUENTIAL_REVERSED;

        companion object {

            /**
             * TODO: Document.
             *
             * @param type
             */
            fun getType(type: String): Type? {
                if (type.isNotEmpty()) {
                    for (next in values()) {
                        if (next.name.equals(type, true)) {
                            return next
                        }
                    }
                }
                return null
            }
        }
    }
}