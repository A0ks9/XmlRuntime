package com.voyager.utils.view.transformation

import android.text.method.ReplacementTransformationMethod

class ReverseTransformation : ReplacementTransformationMethod() {
    override fun getOriginal(): CharArray = "abcdefghijklmnopqrstuvwxyz".toCharArray()
    override fun getReplacement(): CharArray = "zyxwvutsrqponmlkjihgfedcba".toCharArray()
}