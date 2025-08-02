package com.jeffbruma.stemutility.numbersystem.miscellaneous

import com.jeffbruma.stemutility.numbersystem.Octonion
import com.jeffbruma.stemutility.numbersystem.toOctonion
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime

private fun makeTable(dimension: Int) {
    for (i in 1 until dimension) {
        for (j in 1 until dimension) {
            if (i < j) println("\"e${i}e${j}\" to \"e?\",") else continue
        }
    }
}

private fun makeMultiplication() {
    val tableOct: Map<String, String> = mapOf(
        "e0e0" to "e0",
        "e0e1" to "e1",
        "e0e2" to "e2",
        "e0e3" to "e3",
        "e0e4" to "e4",
        "e0e5" to "e5",
        "e0e6" to "e6",
        "e0e7" to "e7",
        "e1e0" to "e1",
        "e1e1" to "-e0",
        "e1e2" to "e3",
        "e1e3" to "-e2",
        "e1e4" to "e5",
        "e1e5" to "-e4",
        "e1e6" to "-e7",
        "e1e7" to "e6",
        "e2e0" to "e2",
        "e2e1" to "-e3",
        "e2e2" to "-e0",
        "e2e3" to "e1",
        "e2e4" to "e6",
        "e2e5" to "e7",
        "e2e6" to "-e4",
        "e2e7" to "-e5",
        "e3e0" to "e3",
        "e3e1" to "e2",
        "e3e2" to "-e1",
        "e3e3" to "-e0",
        "e3e4" to "e7",
        "e3e5" to "-e6",
        "e3e6" to "e5",
        "e3e7" to "-e4",
        "e4e0" to "e4",
        "e4e1" to "-e5",
        "e4e2" to "-e6",
        "e4e3" to "-e7",
        "e4e4" to "-e0",
        "e4e5" to "e1",
        "e4e6" to "e2",
        "e4e7" to "e3",
        "e5e0" to "e5",
        "e5e1" to "e4",
        "e5e2" to "-e7",
        "e5e3" to "e6",
        "e5e4" to "-e1",
        "e5e5" to "-e0",
        "e5e6" to "-e3",
        "e5e7" to "e2",
        "e6e0" to "e6",
        "e6e1" to "e7",
        "e6e2" to "e4",
        "e6e3" to "-e5",
        "e6e4" to "-e2",
        "e6e5" to "e3",
        "e6e6" to "-e0",
        "e6e7" to "-e1",
        "e7e0" to "e7",
        "e7e1" to "-e6",
        "e7e2" to "e5",
        "e7e3" to "e4",
        "e7e4" to "-e3",
        "e7e5" to "-e2",
        "e7e6" to "e1",
        "e7e7" to "-e0"
    )

    fun distribute(dimension: Int) {
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                val str = tableOct["e${i}e$j"]
                print("($str)(o$i * other.o$j) + ")
            }
            println()
        }
    }
    distribute(Octonion.Companion.Dimension)
}

private fun makeListOfZeroDivisors() {
    val zeroDivisors: MutableList<Pair<IntArray, IntArray>> = mutableListOf()
    val size = Octonion.Companion.Dimension
    val i = IntArray(size) { -1 }
    val j = IntArray(size) { -1 }
    val counter = AtomicInteger(0) // Thread-safe counter

    fun findZeroDivisors(index: Int, action: (IntArray, IntArray) -> Unit) {
        if (index == size) {
            action(i, j) // Process when both arrays are fully populated
            return
        }
        for (x in -1..1) {
            i[index] = x
            for (y in -1..1) {
                j[index] = y
                findZeroDivisors(index + 1, action)
            }
        }
    }

    val time = measureTime {
        // Start a background thread to display the counter every second
        val counterThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                println("Checked ${counter.get()} pairs. Found ${zeroDivisors.size} pairs.")
                Thread.sleep(1000) // Update every second
            }
        }
        counterThread.start()

        // Main computation
        findZeroDivisors(0) { i, j ->
            counter.incrementAndGet()
            val s1 = Octonion(i.copyOf())
            val s2 = Octonion(j.copyOf())
            if (s1 != 0.toOctonion() && s2 != 0.toOctonion() && s1 * s2 == 0.toOctonion()) {
                zeroDivisors.add(Pair(i.copyOf(), j.copyOf()))
            }
        }

        // Stop the counter thread when done
        counterThread.interrupt()
        counterThread.join()
    }

    println("Made ${counter.get()} comparisons in $time")
    println("Found ${zeroDivisors.size} zero divisors.")
    println("Adding to file")

    writeToFile(zeroDivisors)
}

private fun writeToFile(zeroDivisors: List<Pair<IntArray, IntArray>>) {
    val file = File("zero_divisors_original.txt")

    // Confirm if file exists and ask before overwriting
    if (file.exists()) {
        print("${file.name} already exists. Overwrite? (y/n): ")
        if (readln().lowercase() != "y") {
            println("Aborting. ZERO divisors not saved.")
            return
        }
    }

    try {
        file.bufferedWriter().use { writer ->
            zeroDivisors.forEach { (i, j) ->
                writer.write("${i.joinToString(",")} | ${j.joinToString(",")}\n")
            }
        }
        println("Successfully saved zero divisors to ${file.name}")
    } catch (e: Exception) {
        println("Error writing to file: ${e.message}")
    }
}

private fun loadZeroDivisorsFromFile(filename: String): List<Pair<IntArray, IntArray>> {
    val file = File(filename)

    // Check if the file exists
    if (!file.exists()) {
        println("File '$filename' does not exist.")
        return emptyList()
    }

    // Check if the file is empty
    if (file.length() == 0L) {
        println("File '$filename' is empty.")
        return emptyList()
    }

    return try {
        file.readLines().mapNotNull { line ->
            val parts = line.split(" | ")
            if (parts.size == 2) {
                val first = parts[0].split(",").map { it.trim().toInt() }.toIntArray()
                val second = parts[1].split(",").map { it.trim().toInt() }.toIntArray()
                Pair(first, second)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        println("Error reading file: ${e.message}")
        emptyList()
    }
}


fun main() {
    makeListOfZeroDivisors()
}
