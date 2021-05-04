package com.asledgehammer.config

/**
 * **UnresolvedException** is thrown when a query in a [ConfigSection] fails to resolve.
 *
 * @author Jab
 *
 * @param msg The message to display when thrown.
 */
class UnresolvedException(msg: String) : RuntimeException(msg)
