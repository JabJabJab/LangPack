package jab.langpack.commons

/**
 * The **LangArg** struct is for a stored key->value pair to override & replace fields.
 *
 * @author Jab
 *
 * @property key The key to identify.
 * @property value The value to store.
 */
class LangArg(val key: String, val value: Any?) {

    override fun toString(): String {
        return "{key='$key', value='$value'}"
    }
}
