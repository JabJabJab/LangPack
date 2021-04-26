package jab.sledgehammer.config

/**
 * **CyclicDependencyException** TODO: Document.
 *
 * @author Jab
 *
 * @param msg The message to display when thrown.
 */
class CyclicDependencyException(msg: String) : RuntimeException(msg)
