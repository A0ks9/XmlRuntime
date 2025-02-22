package com.runtimexml.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoViewAttributes(val viewClassName: String)