package jab.langpack.core.objects

/**
 * TODO: Update documentation to reflect Definition API update.
 *
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
