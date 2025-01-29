package com.flipkart.android.proteus

object ProteusConstants {

    const val TYPE = "type"
    const val LAYOUT = "layout"
    const val DATA = "data"
    const val COLLECTION = "collection"
    const val DATA_NULL = "null"
    const val STYLE_DELIMITER = "\\."
    const val EMPTY = ""

    @JvmField
    val EMPTY_STRING = Primitive(EMPTY)

    @JvmField
    val TRUE = Primitive(true)

    @JvmField
    val FALSE = Primitive(false)

    private var isLoggingEnabled = false

    @JvmStatic
    fun setIsLoggingEnabled(isLoggingEnabled: Boolean) {
        this.isLoggingEnabled = isLoggingEnabled
    }

    @JvmStatic
    fun isLoggingEnabled(): Boolean = isLoggingEnabled
}