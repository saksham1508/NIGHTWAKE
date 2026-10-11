package com.example.nightwake.core

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * High-performance 2D Spatial Hash Grid with ZERO per-frame garbage allocation.
 *
 * Used for rapid broadphase neighbor collision and auto-aim targeting for 150+ units.
 * Operates on integer cell keys and pre-allocated arrays to avoid collections overhead.
 */
class SpatialHashGrid(
    private val cellSize: Float = GameConfig.SPATIAL_CELL_SIZE,
    private val maxEntities: Int = GameConfig.MAX_ENEMIES_CAPACITY
) {
    // Spatial grid bounding parameters
    private val invCellSize: Float = 1f / cellSize
    private val tableSize: Int = 1024 // Power of 2 hash table
    private val hashMask: Int = tableSize - 1

    // Pre-allocated flat buffers for linked-list hashing
    // head[hash] points to the first entity index in that bucket
    private val head: IntArray = IntArray(tableSize) { -1 }
    // next[entityIndex] points to the next entity index in the same bucket
    private val next: IntArray = IntArray(maxEntities) { -1 }

    // Reusable query result buffer to prevent allocating lists per frame
    val queryResultBuffer: IntArray = IntArray(maxEntities)
    var queryResultCount: Int = 0
        private set

    /**
     * Clears the grid for the current frame.
     */
    fun clear() {
        head.fill(-1)
        next.fill(-1)
        queryResultCount = 0
    }

    private fun hash(cellX: Int, cellY: Int): Int {
        // Spatial hash algorithm (primes 73856093 and 19349663)
        val h = (cellX * 73856093) xor (cellY * 19349663)
        return (h and 0x7FFFFFFF) and hashMask
    }

    /**
     * Inserts an entity at coordinates (x, y).
     * @param entityIndex unique index of the entity [0 until maxEntities]
     */
    fun insert(entityIndex: Int, x: Float, y: Float) {
        if (entityIndex < 0 || entityIndex >= maxEntities) return
        val cx = floor(x * invCellSize).toInt()
        val cy = floor(y * invCellSize).toInt()
        val bucket = hash(cx, cy)

        next[entityIndex] = head[bucket]
        head[bucket] = entityIndex
    }

    /**
     * Queries all entities within the axis-aligned bounding box of a circle (cx, cy, radius).
     * Results are populated into [queryResultBuffer] up to [queryResultCount].
     * Zero allocations.
     */
    fun queryRadius(cx: Float, cy: Float, radius: Float): Int {
        queryResultCount = 0
        val minX = floor((cx - radius) * invCellSize).toInt()
        val maxX = floor((cx + radius) * invCellSize).toInt()
        val minY = floor((cy - radius) * invCellSize).toInt()
        val maxY = floor((cy + radius) * invCellSize).toInt()

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                val bucket = hash(x, y)
                var curr = head[bucket]
                while (curr != -1) {
                    if (queryResultCount < maxEntities) {
                        queryResultBuffer[queryResultCount++] = curr
                    }
                    curr = next[curr]
                }
            }
        }
        return queryResultCount
    }
}
