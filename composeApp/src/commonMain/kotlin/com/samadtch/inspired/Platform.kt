package com.samadtch.inspired

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform