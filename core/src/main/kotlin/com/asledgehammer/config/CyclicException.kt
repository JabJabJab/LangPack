package com.asledgehammer.config

/**
 * **CyclicException** Is thrown when a [ConfigSection] detects a cyclic dependency in its hierarchy.
 *
 * @author Jab
 *
 * @param msg The message to display when thrown.
 */
class CyclicException(msg: String) : RuntimeException(msg)
