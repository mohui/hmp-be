package com.bjknrt.article.service.utils

fun titleToString(titles: List<String>): String {
    return titles.map { "《${it}》" }.joinToString { it }
}