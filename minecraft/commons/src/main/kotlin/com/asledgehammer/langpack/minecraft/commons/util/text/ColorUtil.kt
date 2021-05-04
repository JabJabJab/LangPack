@file:Suppress("MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.minecraft.commons.util.text

import com.asledgehammer.langpack.core.LangPack

/**
 * **ColorUtil** stores general color utilities for Minecraft plugins of [LangPack].
 *
 * @author Jab
 */
object ColorUtil {

    const val COLOR_CHAR = '\u00a7'

    /**
     * Translates the alternate color character into Minecraft color character.
     *
     * @param string The string to transform.
     * @param char The character to convert.
     */
    fun color(string: String, char: Char = '&'): String = string.replace(char, COLOR_CHAR)
}
