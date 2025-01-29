package com.flipkart.android.proteus.value

import com.flipkart.android.proteus.ProteusConstants

object Null : Value() {

    private const val NULL_STRING = "NULL"

    override fun copy(): Null = this
    override fun toString(): String = NULL_STRING
    override fun getAsString(): String = ProteusConstants.EMPTY
    override fun hashCode(): Int = Null::class.hashCode()
    override fun equals(other: Any?): Boolean = this === other || other is Null
}