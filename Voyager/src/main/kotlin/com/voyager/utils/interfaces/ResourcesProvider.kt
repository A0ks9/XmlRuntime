package com.voyager.utils.interfaces

interface ResourcesProvider {
    fun getResId(type: String, name: String): Int
}