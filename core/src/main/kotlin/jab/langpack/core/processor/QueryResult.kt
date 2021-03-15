package jab.langpack.core.processor

class QueryResult {

    val resolvable: Resolvable?
    val query: String
    val result: Any?

    constructor(query: String, result: Any?) {
        this.resolvable = null
        this.query = query
        this.result = result
    }

    constructor(resolvable: Resolvable?, query: String, result: Any?) {
        this.resolvable = resolvable
        this.query = query
        this.result = result
    }
}