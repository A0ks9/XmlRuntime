package com.voyager.core.data.utils

/**
 * Interface for streaming XML tokens from native code.
 * This allows efficient parsing of XML without building a complete JSON string.
 */
interface XmlTokenStream {
    /**
     * Called when a new token is available from the native parser.
     * @param token The parsed XML token
     */
    fun onToken(token: XmlToken)

    /**
     * Called when parsing is complete.
     * @param sha256Hash The SHA256 hash of the parsed XML
     */
    fun onComplete(sha256Hash: ByteArray)
} 