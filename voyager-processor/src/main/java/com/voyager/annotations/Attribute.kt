package com.voyager.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Attribute(val attrName: String)