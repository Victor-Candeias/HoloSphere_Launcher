package com.holosphere.launcher.domain

data class Category(val id: String, val name: String, val color: Long, val appPackageNames: Set<String> = emptySet())

val defaultCategories = listOf(
    Category("communication", "Comunicação", 0xFF63D8FF),
    Category("media", "Multimédia", 0xFFFF8DC7),
    Category("work", "Trabalho", 0xFFFFD166),
    Category("tools", "Ferramentas", 0xFF7BE495),
    Category("other", "Outras", 0xFFB8C2E8)
)
