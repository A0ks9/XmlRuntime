package com.voyager.core.attribute

/**
 * Utility object that defines the ordering and categorization of attributes in the Voyager framework.
 * This object is responsible for determining the type and processing order of various view attributes.
 * It provides methods to identify and categorize different types of attributes, particularly those
 * related to ConstraintLayout and standard Android view attributes.
 */
internal object AttributeOrder {
    /** Prefix used to identify ConstraintLayout-specific attributes */
    private const val LAYOUT_CONSTRAINT_PREFIX = "layout_constraint"
    
    /** Keyword used to identify bias-related attributes in ConstraintLayout */
    private const val BIAS_KEYWORD = "bias"
    
    /** Standard Android view ID attribute name */
    private const val ID_ATTRIBUTE = "id"

    /**
     * Checks if an attribute name belongs to ConstraintLayout.
     * This method identifies attributes that are specific to ConstraintLayout
     * by checking if they start with the ConstraintLayout prefix.
     * 
     * @param name The attribute name to check
     * @return true if the attribute is a ConstraintLayout attribute, false otherwise
     */
    internal fun isConstraintLayoutAttribute(name: String): Boolean =
        name.startsWith(LAYOUT_CONSTRAINT_PREFIX, ignoreCase = true)

    /**
     * Checks if an attribute is a pure constraint (not a bias attribute).
     * This method identifies attributes that are pure constraints by checking
     * if they are ConstraintLayout attributes but don't contain the bias keyword.
     * 
     * @param name The attribute name to check
     * @return true if the attribute is a pure constraint, false otherwise
     */
    internal fun isConstraint(name: String): Boolean =
        isConstraintLayoutAttribute(name) && !name.contains(BIAS_KEYWORD, ignoreCase = true)

    /**
     * Checks if an attribute is the standard Android view ID attribute.
     * This method identifies the standard ID attribute by checking if the name
     * matches the ID attribute constant, ignoring case.
     * 
     * @param name The attribute name to check
     * @return true if the attribute is the ID attribute, false otherwise
     */
    internal fun isIDAttribute(name: String): Boolean =
        name.equals(ID_ATTRIBUTE, ignoreCase = true)
} 