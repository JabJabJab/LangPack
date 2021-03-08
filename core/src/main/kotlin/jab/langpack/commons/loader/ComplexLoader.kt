package jab.langpack.commons.loader

import org.bukkit.configuration.ConfigurationSection

/**
 * The **ComplexLoader** interface allows third-party installments of complex objects that require code extensions in
 * specific environments such as ***Bukkit***, ***Spigot***, and ***BungeeCord***.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface ComplexLoader<E> {

    /**
     * Loads a object from configured YAML.
     *
     * @param cfg The YAML to read.
     *
     * @return Returns the loaded object.
     */
    fun load(cfg: ConfigurationSection): E?

    companion object {

        private val loaders = HashMap<String, ComplexLoader<*>>()
        private val actionTextLoader = ActionTextLoader()
        private val stringPoolLoader = StringPoolLoader()

        init {
            addDefaultLoaders()
        }

        /**
         * @param type The type of complex object.
         *
         * @return Returns the loader assigned to the type. If one is not assigned, null is returned.
         */
        fun get(type: String): ComplexLoader<*>? = loaders[type.toLowerCase()]

        /**
         * Sets a loader for the type.
         *
         * @param type The type of complex object.
         * @param loader The loader to assign.
         */
        fun set(type: String, loader: ComplexLoader<*>?) {
            if (loader != null) {
                loaders[type.toLowerCase()] = loader
            } else {
                loaders.remove(type.toLowerCase())
            }
        }

        /**
         * Removes a loader assigned to the type.
         *
         * @param type The type of complex object.
         */
        fun remove(type: String) {
            loaders.remove(type.toLowerCase())
        }

        /**
         * @param type The type of complex object.
         *
         * @return Returns true if a loader is assigned to the type.
         */
        fun contains(type: String): Boolean = loaders.containsKey(type.toLowerCase())

        /**
         * Adds the default loaders for the core.
         */
        fun addDefaultLoaders() {
            if (!contains("action")) set("action", actionTextLoader)
            if (!contains("pool")) set("pool", stringPoolLoader)
        }
    }
}
