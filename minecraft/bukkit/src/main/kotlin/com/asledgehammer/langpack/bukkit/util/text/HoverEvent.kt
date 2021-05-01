package com.asledgehammer.langpack.bukkit.util.text

/**
 * TODO: Document.
 *
 * @author Jab
 */
class HoverEvent {

    /**
     * TODO: Document.
     */
    val contents = ArrayList<String>()

    /**
     * TODO: Document.
     *
     * @param lines
     */
    constructor(lines: Array<String>) {
        for (line in lines) this.contents.add(line)
    }

    /**
     * TODO: Document.
     *
     * @param lines
     */
    constructor(lines: Collection<String>) {
        for (line in lines) this.contents.add(line)
    }
}
