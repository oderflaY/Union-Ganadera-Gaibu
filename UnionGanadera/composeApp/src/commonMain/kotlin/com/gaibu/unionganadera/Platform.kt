package com.gaibu.unionganadera

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform