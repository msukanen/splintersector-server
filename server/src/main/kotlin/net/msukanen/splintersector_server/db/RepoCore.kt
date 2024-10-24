package net.msukanen.splintersector_server.db

/**
 * Core interface for our repos.
 */
interface RepoCore<T> {
    suspend fun byRef(refId: Int): T?
    suspend fun upsert(refId: Int, obj: T): Boolean
}

interface RepoMultiEntryCore<T> {
    suspend fun allByRef(refId: Int): List<T>?
    suspend fun upsert(refId: Int, obj: T): Boolean
}
