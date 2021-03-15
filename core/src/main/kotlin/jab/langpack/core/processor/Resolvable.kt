package jab.langpack.core.processor

import jab.langpack.core.objects.Complex

interface Resolvable {

    /**
     * Attempts to locate a stored value with a query.
     *
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns the resolved query. If nothing is located at the destination of the query, null is returned.
     */
    fun resolve(query: String): QueryResult

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves.
     */
    fun contains(query: String): Boolean

    /**
     * @param query The string to process. The string can be a field or set of fields delimited by a period.
     *
     * @return Returns true if the query resolves and is the type [Complex].
     */
    fun isComplex(query: String): Boolean
}