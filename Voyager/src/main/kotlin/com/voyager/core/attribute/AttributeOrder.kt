package com.voyager.core.attribute

object AttributeOrder {
    private const val LAYOUT_CONSTRAINT_PREFIX = "layout_constraint"
    private const val BIAS_KEYWORD = "bias"
    private const val ID_ATTRIBUTE = "id"

    fun isConstraintLayoutAttribute(name: String): Boolean =
        name.startsWith(LAYOUT_CONSTRAINT_PREFIX, ignoreCase = true)

    fun isConstraint(name: String): Boolean =
        isConstraintLayoutAttribute(name) && !name.contains(BIAS_KEYWORD, ignoreCase = true)

    fun isIDAttribute(name: String): Boolean =
        name.equals(ID_ATTRIBUTE, ignoreCase = true)
} 