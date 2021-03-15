package jab.langpack.core

/**
 * The **LangArg** struct is for a stored key->value pair to override & replace fields.
 *
 * @author Jab
 *
 * @property key The key to identify.
 * @property value The value to store.
 */
data class LangArg(val key: String, val value: Any) {
    override fun toString(): String = "{key='$key', value='$value'}"
}
