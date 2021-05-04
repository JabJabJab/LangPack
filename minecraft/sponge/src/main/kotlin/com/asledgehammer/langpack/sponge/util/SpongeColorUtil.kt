@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.asledgehammer.langpack.sponge.util

import com.asledgehammer.langpack.minecraft.commons.util.text.ColorUtil
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors

/**
 * **SpongeColorUtil** Aids in providing utilities for complex processing for the Sponge LangPack plugin.
 *
 * @author Jab
 */
object SpongeColorUtil {

    private const val ALL_CODES = "0123456789AaBbCcDdEeFf"
    private val COLORS = HashMap<Char, TextColor>()
    private val RAW_COLORS = HashMap<TextColor, Char>()

    fun getRaw(color: TextColor): Char = RAW_COLORS[color]!!

    fun toString(color: TextColor): String = "${ColorUtil.COLOR_CHAR}${getRaw(color)}"

    fun getByChar(char: Char): TextColor = COLORS[char] ?: TextColors.NONE

    fun isColorCode(char: Char): Boolean = ALL_CODES.contains(char)

    private fun register(char: Char, color: TextColor) {
        COLORS[char] = color
        RAW_COLORS[color] = char
    }

    init {
        register('0', TextColors.BLACK)
        register('1', TextColors.DARK_BLUE)
        register('2', TextColors.DARK_GREEN)
        register('3', TextColors.DARK_AQUA)
        register('4', TextColors.DARK_RED)
        register('5', TextColors.DARK_PURPLE)
        register('6', TextColors.GOLD)
        register('7', TextColors.GRAY)
        register('8', TextColors.DARK_GRAY)
        register('9', TextColors.BLUE)
        register('a', TextColors.GREEN)
        register('b', TextColors.AQUA)
        register('c', TextColors.RED)
        register('d', TextColors.LIGHT_PURPLE)
        register('e', TextColors.YELLOW)
        register('f', TextColors.WHITE)
    }
}
