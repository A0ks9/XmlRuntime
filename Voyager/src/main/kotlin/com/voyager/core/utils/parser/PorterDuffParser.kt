package com.voyager.core.utils.parser

import android.graphics.PorterDuff
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing Android PorterDuff.Mode values.
 */
object PorterDuffParser {

    private val sPorterDuff = ConcurrentHashMap<String, PorterDuff.Mode>().apply {
        this["add"] = PorterDuff.Mode.ADD
        this["multiply"] = PorterDuff.Mode.MULTIPLY
        this["screen"] = PorterDuff.Mode.SCREEN
        this["src_atop"] = PorterDuff.Mode.SRC_ATOP
        this["src_in"] = PorterDuff.Mode.SRC_IN
        this["src_over"] = PorterDuff.Mode.SRC_OVER
    }

    /**
     * Parses a PorterDuff mode string to a PorterDuff.Mode.
     *
     * @param porterDuff The PorterDuff mode string (e.g., "src_in", "multiply")
     * @return The parsed PorterDuff.Mode
     */
    fun parsePorterDuff(porterDuff: String): PorterDuff.Mode =
        sPorterDuff[porterDuff] ?: PorterDuff.Mode.SRC_IN
} 