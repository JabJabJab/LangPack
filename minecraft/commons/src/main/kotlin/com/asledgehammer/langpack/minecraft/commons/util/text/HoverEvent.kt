package com.asledgehammer.langpack.minecraft.commons.util.text

/**
 * **HoverEvent** stores hover text for ActionText definitions.
 *
 * @author Jab
 */
class HoverEvent {

    /**
     * The hover text, stored as a list.
     */
    val contents = ArrayList<String>()

    /**
     * @param lines The lines of text.
     */
    constructor(lines: Array<String>) {
        for (line in lines) this.contents.add(line)
    }

    /**
     * @param lines The lines of text.
     */
    constructor(lines: Collection<String>) {
        for (line in lines) this.contents.add(line)
    }
}
