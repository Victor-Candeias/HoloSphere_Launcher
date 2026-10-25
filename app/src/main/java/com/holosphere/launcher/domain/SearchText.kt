package com.holosphere.launcher.domain

import java.text.Normalizer

fun String.normalizeForSearch(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "")
    .lowercase()
    .trim()
