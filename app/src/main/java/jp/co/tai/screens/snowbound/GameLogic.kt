package jp.co.tai.screens.snowbound

import jp.co.tai.R

fun hasAnyAdjacentMatch(
    rows: Int,
    cols: Int,
    elements: List<Int?>
): Boolean {
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val idx = row * cols + col
            val v = elements[idx] ?: continue

            if (col + 1 < cols) {
                val rightIdx = idx + 1
                if (elements[rightIdx] == v) return true
            }

            if (row + 1 < rows) {
                val downIdx = idx + cols
                if (elements[downIdx] == v) return true
            }
        }
    }
    return false
}

fun findMatchGroup(
    rows: Int,
    cols: Int,
    elements: List<Int?>,
    startIndex: Int
): Set<Int> {
    val target = elements[startIndex] ?: return emptySet()
    val visited = BooleanArray(rows * cols) { false }
    val cluster = mutableSetOf<Int>()

    val directions = listOf(
        1 to 0,
        -1 to 0,
        0 to 1,
        0 to -1
    )

    fun dfs(index: Int) {
        if (visited[index]) return
        visited[index] = true
        cluster.add(index)

        val row = index / cols
        val col = index % cols

        directions.forEach { (dr, dc) ->
            val nr = row + dr
            val nc = col + dc
            if (nr in 0 until rows && nc in 0 until cols) {
                val neighborIndex = nr * cols + nc
                if (!visited[neighborIndex] && elements[neighborIndex] == target) {
                    dfs(neighborIndex)
                }
            }
        }
    }

    dfs(startIndex)

    return if (cluster.size >= 2) cluster else emptySet()
}

fun calculateDropStepsAfterRemoval(
    rows: Int,
    cols: Int,
    elements: List<Int?>,
    removed: Set<Int>
): IntArray {
    val tempList = elements.toMutableList()

    removed.forEach { idx -> tempList[idx] = null }

    val dropSteps = IntArray(rows * cols)

    for (col in 0 until cols) {
        var emptyBelow = 0
        for (row in rows - 1 downTo 0) {
            val idx = row * cols + col
            if (tempList[idx] == null) {
                emptyBelow++
            } else {
                dropSteps[idx] = emptyBelow
            }
        }
    }

    return dropSteps
}

fun applyGravityAndGetNewIndices(
    rows: Int,
    cols: Int,
    elements: List<Int?>,
    choosingElements: List<Int>
): Pair<MutableList<Int?>, List<Int>> {
    val newList = elements.toMutableList()
    val newIndices = mutableListOf<Int>()

    for (col in 0 until cols) {
        val columnIndices = (rows - 1 downTo 0).map { row -> row * cols + col }

        val existing = mutableListOf<Int>()
        for (idx in columnIndices) {
            val v = newList[idx]
            if (v != null) existing.add(v)
        }

        var existingIndex = 0

        for (idx in columnIndices) {
            if (existingIndex < existing.size) {
                newList[idx] = existing[existingIndex++]
            } else {
                val newVal = choosingElements.random()
                newList[idx] = newVal
                newIndices += idx
            }
        }
    }

    return Pair(newList, newIndices)
}

fun bonusResForCluster(size: Int): Int? = when (size) {
    2 -> R.drawable.x100
    3 -> R.drawable.x200
    4 -> R.drawable.x300
    in 5..Int.MAX_VALUE -> R.drawable.x500
    else -> null
}