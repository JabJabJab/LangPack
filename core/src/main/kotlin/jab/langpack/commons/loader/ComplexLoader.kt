package jab.langpack.commons.loader

import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface ComplexLoader<E> {

    /**
     * TODO: Document.
     *
     * @param cfg
     *
     * @return
     */
    fun load(cfg: ConfigurationSection): E?

    companion object {

        private val loaders = HashMap<String, ComplexLoader<*>>()

        init {
            addDefaultLoaders()
        }

        /**
         * TODO: Document.
         *
         * @param type
         *
         * @return
         */
        fun get(type: String): ComplexLoader<*>? {
            return loaders[type.toLowerCase()]
        }

        /**
         * TODO: Document.
         *
         * @param type
         * @param loader
         */
        fun set(type: String, loader: ComplexLoader<*>?) {
            if (loader != null) {
                loaders[type.toLowerCase()] = loader
            } else {
                loaders.remove(type.toLowerCase())
            }
        }

        /**
         * TODO: Document.
         *
         * @param type
         */
        fun remove(type: String) {
            loaders.remove(type.toLowerCase())
        }

        /**
         * TODO: Document.
         *
         * @param type
         */
        fun contains(type: String): Boolean {
            return loaders.containsKey(type.toLowerCase())
        }

        fun addDefaultLoaders() {

            val actionTextLoader = ActionTextLoader()
            if (contains("action")) {
                set("action", actionTextLoader)
            }

            val stringPoolLoader = StringPoolLoader()
            if (contains("pool")) {
                set("pool", stringPoolLoader)
            }
        }
    }
}