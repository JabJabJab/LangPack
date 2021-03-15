package jab.langpack.core.objects

import jab.langpack.core.LangPack

/**
 * The **Definition** class. TODO: Document.
 *
 * @property pack
 * @property parent
 * @property value
 */
abstract class Definition<E>(val pack: LangPack, val parent: Group?, val raw: E) {

    var value: E = raw
    var walked: Boolean = false

    fun walk() {
        value = onWalk()
        walked = true
    }

    /**
     * Walks the value for the definition. This allows for post-load transformations of the value.
     */
    abstract fun onWalk(): E
}