package net.msukanen.splintersector_server

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform