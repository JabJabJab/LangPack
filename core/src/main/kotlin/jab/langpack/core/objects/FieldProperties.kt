package jab.langpack.core.objects

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property raw
 * @property name
 * @property placeholder
 * @property resolve
 * @property packageScope
 */
class FieldProperties(
    val raw: String,
    val name: String,
    val placeholder: String,
    val resolve: Boolean,
    val packageScope: Boolean,
) {
    override fun toString(): String =
        "FieldProperties(raw='$raw', name='$name', placeholder='$placeholder', resolve=$resolve, packageScope=$packageScope)"
}
