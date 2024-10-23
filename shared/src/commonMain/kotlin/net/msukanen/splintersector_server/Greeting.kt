package net.msukanen.splintersector_server

class Greeting {
    private val platform = getPlatform()

    override fun toString(): String {
        return "${platform.name} ..."
    }
}