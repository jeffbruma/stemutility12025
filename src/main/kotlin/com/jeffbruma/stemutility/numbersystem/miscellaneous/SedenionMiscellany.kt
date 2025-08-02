package com.jeffbruma.stemutility.numbersystem.miscellaneous

import com.jeffbruma.stemutility.numbersystem.Sedenion
import com.jeffbruma.stemutility.numbersystem.toSedenion
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime

private fun makeMultiplication() {
    val tableSed: Map<String, String> = mapOf(
        "z0z0" to "z0",
        "z0z1" to "z1",
        "z0z2" to "z2",
        "z0z3" to "z3",
        "z0z4" to "z4",
        "z0z5" to "z5",
        "z0z6" to "z6",
        "z0z7" to "z7",
        "z0z8" to "z8",
        "z0z9" to "z9",
        "z0zA" to "zA",
        "z0zB" to "zB",
        "z0zC" to "zC",
        "z0zD" to "zD",
        "z0zE" to "zE",
        "z0zF" to "zF",
        "z1z0" to "z1",
        "z1z1" to "-z0",
        "z1z2" to "z3",
        "z1z3" to "-z2",
        "z1z4" to "z5",
        "z1z5" to "-z4",
        "z1z6" to "-z7",
        "z1z7" to "z6",
        "z1z8" to "z9",
        "z1z9" to "-z8",
        "z1zA" to "-zB",
        "z1zB" to "zA",
        "z1zC" to "-zD",
        "z1zD" to "zC",
        "z1zE" to "zF",
        "z1zF" to "-zE",
        "z2z0" to "z2",
        "z2z1" to "-z3",
        "z2z2" to "-z0",
        "z2z3" to "z1",
        "z2z4" to "z6",
        "z2z5" to "z7",
        "z2z6" to "-z4",
        "z2z7" to "-z5",
        "z2z8" to "zA",
        "z2z9" to "zB",
        "z2zA" to "-z8",
        "z2zB" to "-z9",
        "z2zC" to "-zE",
        "z2zD" to "-zF",
        "z2zE" to "zC",
        "z2zF" to "zD",
        "z3z0" to "z3",
        "z3z1" to "z2",
        "z3z2" to "-z1",
        "z3z3" to "-z0",
        "z3z4" to "z7",
        "z3z5" to "-z6",
        "z3z6" to "z5",
        "z3z7" to "-z4",
        "z3z8" to "zB",
        "z3z9" to "-zA",
        "z3zA" to "z9",
        "z3zB" to "-z8",
        "z3zC" to "-zF",
        "z3zD" to "zE",
        "z3zE" to "-zD",
        "z3zF" to "zC",
        "z4z0" to "z4",
        "z4z1" to "-z5",
        "z4z2" to "-z6",
        "z4z3" to "-z7",
        "z4z4" to "-z0",
        "z4z5" to "z1",
        "z4z6" to "z2",
        "z4z7" to "z3",
        "z4z8" to "zC",
        "z4z9" to "zD",
        "z4zA" to "zE",
        "z4zB" to "zF",
        "z4zC" to "-z8",
        "z4zD" to "-z9",
        "z4zE" to "-zA",
        "z4zF" to "-zB",
        "z5z0" to "z5",
        "z5z1" to "z4",
        "z5z2" to "-z7",
        "z5z3" to "z6",
        "z5z4" to "-z1",
        "z5z5" to "-z0",
        "z5z6" to "-z3",
        "z5z7" to "z2",
        "z5z8" to "zD",
        "z5z9" to "-zC",
        "z5zA" to "zF",
        "z5zB" to "-zE",
        "z5zC" to "z9",
        "z5zD" to "-z8",
        "z5zE" to "zB",
        "z5zF" to "-zA",
        "z6z0" to "z6",
        "z6z1" to "z7",
        "z6z2" to "z4",
        "z6z3" to "-z5",
        "z6z4" to "-z2",
        "z6z5" to "z3",
        "z6z6" to "-z0",
        "z6z7" to "-z1",
        "z6z8" to "zE",
        "z6z9" to "-zF",
        "z6zA" to "-zC",
        "z6zB" to "zD",
        "z6zC" to "zA",
        "z6zD" to "-zB",
        "z6zE" to "-z8",
        "z6zF" to "z9",
        "z7z0" to "z7",
        "z7z1" to "-z6",
        "z7z2" to "z5",
        "z7z3" to "z4",
        "z7z4" to "-z3",
        "z7z5" to "-z2",
        "z7z6" to "z1",
        "z7z7" to "-z0",
        "z7z8" to "zF",
        "z7z9" to "zE",
        "z7zA" to "-zD",
        "z7zB" to "-zC",
        "z7zC" to "zB",
        "z7zD" to "zA",
        "z7zE" to "-z9",
        "z7zF" to "-z8",
        "z8z0" to "z8",
        "z8z1" to "-z9",
        "z8z2" to "-zA",
        "z8z3" to "-zB",
        "z8z4" to "-zC",
        "z8z5" to "-zD",
        "z8z6" to "-zE",
        "z8z7" to "-zF",
        "z8z8" to "-z0",
        "z8z9" to "z1",
        "z8zA" to "z2",
        "z8zB" to "z3",
        "z8zC" to "z4",
        "z8zD" to "z5",
        "z8zE" to "z6",
        "z8zF" to "z7",
        "z9z0" to "z9",
        "z9z1" to "z8",
        "z9z2" to "-zB",
        "z9z3" to "zA",
        "z9z4" to "-zD",
        "z9z5" to "zC",
        "z9z6" to "zF",
        "z9z7" to "-zE",
        "z9z8" to "-z1",
        "z9z9" to "-z0",
        "z9zA" to "-z3",
        "z9zB" to "z2",
        "z9zC" to "-z5",
        "z9zD" to "z4",
        "z9zE" to "z7",
        "z9zF" to "-z6",
        "zAz0" to "zA",
        "zAz1" to "zB",
        "zAz2" to "z8",
        "zAz3" to "-z9",
        "zAz4" to "-zE",
        "zAz5" to "-zF",
        "zAz6" to "zC",
        "zAz7" to "zD",
        "zAz8" to "-z2",
        "zAz9" to "z3",
        "zAzA" to "-z0",
        "zAzB" to "-z1",
        "zAzC" to "-z6",
        "zAzD" to "-z7",
        "zAzE" to "z4",
        "zAzF" to "z5",
        "zBz0" to "zB",
        "zBz1" to "-zA",
        "zBz2" to "z9",
        "zBz3" to "z8",
        "zBz4" to "-zF",
        "zBz5" to "zE",
        "zBz6" to "-zD",
        "zBz7" to "zC",
        "zBz8" to "-z3",
        "zBz9" to "-z2",
        "zBzA" to "z1",
        "zBzB" to "-z0",
        "zBzC" to "-z7",
        "zBzD" to "z6",
        "zBzE" to "-z5",
        "zBzF" to "z4",
        "zCz0" to "zC",
        "zCz1" to "zD",
        "zCz2" to "zE",
        "zCz3" to "zF",
        "zCz4" to "z8",
        "zCz5" to "-z9",
        "zCz6" to "-zA",
        "zCz7" to "-zB",
        "zCz8" to "-z4",
        "zCz9" to "z5",
        "zCzA" to "z6",
        "zCzB" to "z7",
        "zCzC" to "-z0",
        "zCzD" to "-z1",
        "zCzE" to "-z2",
        "zCzF" to "-z3",
        "zDz0" to "zD",
        "zDz1" to "-zC",
        "zDz2" to "zF",
        "zDz3" to "-zE",
        "zDz4" to "z9",
        "zDz5" to "z8",
        "zDz6" to "zB",
        "zDz7" to "-zA",
        "zDz8" to "-z5",
        "zDz9" to "-z4",
        "zDzA" to "z7",
        "zDzB" to "-z6",
        "zDzC" to "z1",
        "zDzD" to "-z0",
        "zDzE" to "z3",
        "zDzF" to "-z2",
        "zEz0" to "zE",
        "zEz1" to "-zF",
        "zEz2" to "-zC",
        "zEz3" to "zD",
        "zEz4" to "zA",
        "zEz5" to "-zB",
        "zEz6" to "z8",
        "zEz7" to "z9",
        "zEz8" to "-z6",
        "zEz9" to "-z7",
        "zEzA" to "-z4",
        "zEzB" to "z5",
        "zEzC" to "z2",
        "zEzD" to "-z3",
        "zEzE" to "-z0",
        "zEzF" to "z1",
        "zFz0" to "zF",
        "zFz1" to "zE",
        "zFz2" to "-zD",
        "zFz3" to "-zC",
        "zFz4" to "zB",
        "zFz5" to "zA",
        "zFz6" to "-z9",
        "zFz7" to "z8",
        "zFz8" to "-z7",
        "zFz9" to "z6",
        "zFzA" to "-z5",
        "zFzB" to "-z4",
        "zFzC" to "z3",
        "zFzD" to "z2",
        "zFzE" to "-z1",
        "zFzF" to "-z0"
    )

    fun distribute(dimension: Int) {
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                val iHex = i.toString(16).uppercase() // hex representation, not dimension
                val jHex = j.toString(16).uppercase() // ditto
                val str = tableSed["z${iHex}z$jHex"]
                print("($str)(s$iHex * other.s$jHex) + ")
            }
            println()
        }
    }
    distribute(Sedenion.Companion.Dimension)
}

private fun makeListOfZeroDivisors() {
    val zeroDivisors: MutableList<Pair<IntArray, IntArray>> = mutableListOf()
    val zero = 0.toSedenion()
    val size = 16
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
                println("Checked ${counter.get()} pairs...")
                Thread.sleep(1000) // Update every second
            }
        }
        counterThread.start()

        // Main computation
        findZeroDivisors(0) { i, j ->
            counter.incrementAndGet()
            val s1 = Sedenion(i.copyOf())
            val s2 = Sedenion(j.copyOf())
            if (s1 != zero && s2 != zero && s1 * s2 == zero) {
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
    val file = File("zero_divisors.txt")

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
    for (i in 1..15) {
        println("assertEquals(negativeOne, e[$i] * e[$i])")
    }
}