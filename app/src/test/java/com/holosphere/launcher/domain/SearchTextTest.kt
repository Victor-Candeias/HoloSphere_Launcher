package com.holosphere.launcher.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchTextTest {
    @Test fun removesAccentsAndNormalizesCase() {
        assertEquals("camara", "Câmara".normalizeForSearch())
    }

    @Test fun trimsWhitespace() {
        assertEquals("mapas", "  MAPÁS  ".normalizeForSearch())
    }
}
