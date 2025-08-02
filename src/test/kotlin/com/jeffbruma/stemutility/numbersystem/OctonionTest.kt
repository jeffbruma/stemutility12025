package com.jeffbruma.stemutility.numbersystem

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OctonionTest {
    private val e = listOf(
        Octonion(o0 = 1),
        Octonion(o1 = 1),
        Octonion(o2 = 1),
        Octonion(o3 = 1),
        Octonion(o4 = 1),
        Octonion(o5 = 1),
        Octonion(o6 = 1),
        Octonion(o7 = 1),
    )
    private val oComponentRange = -10..10

    @Test
    fun `multiplication table for octonions`() {
        assertTrue(e[0] * e[0] == e[0])
        assertTrue(e[0] * e[1] == e[1])
        assertTrue(e[0] * e[2] == e[2])
        assertTrue(e[0] * e[3] == e[3])
        assertTrue(e[0] * e[4] == e[4])
        assertTrue(e[0] * e[5] == e[5])
        assertTrue(e[0] * e[6] == e[6])
        assertTrue(e[0] * e[7] == e[7])
        assertTrue(e[1] * e[0] == e[1])
        assertTrue(e[1] * e[1] == -e[0])
        assertTrue(e[1] * e[2] == e[3])
        assertTrue(e[1] * e[3] == -e[2])
        assertTrue(e[1] * e[4] == e[5])
        assertTrue(e[1] * e[5] == -e[4])
        assertTrue(e[1] * e[6] == -e[7])
        assertTrue(e[1] * e[7] == e[6])
        assertTrue(e[2] * e[0] == e[2])
        assertTrue(e[2] * e[1] == -e[3])
        assertTrue(e[2] * e[2] == -e[0])
        assertTrue(e[2] * e[3] == e[1])
        assertTrue(e[2] * e[4] == e[6])
        assertTrue(e[2] * e[5] == e[7])
        assertTrue(e[2] * e[6] == -e[4])
        assertTrue(e[2] * e[7] == -e[5])
        assertTrue(e[3] * e[0] == e[3])
        assertTrue(e[3] * e[1] == e[2])
        assertTrue(e[3] * e[2] == -e[1])
        assertTrue(e[3] * e[3] == -e[0])
        assertTrue(e[3] * e[4] == e[7])
        assertTrue(e[3] * e[5] == -e[6])
        assertTrue(e[3] * e[6] == e[5])
        assertTrue(e[3] * e[7] == -e[4])
        assertTrue(e[4] * e[0] == e[4])
        assertTrue(e[4] * e[1] == -e[5])
        assertTrue(e[4] * e[2] == -e[6])
        assertTrue(e[4] * e[3] == -e[7])
        assertTrue(e[4] * e[4] == -e[0])
        assertTrue(e[4] * e[5] == e[1])
        assertTrue(e[4] * e[6] == e[2])
        assertTrue(e[4] * e[7] == e[3])
        assertTrue(e[5] * e[0] == e[5])
        assertTrue(e[5] * e[1] == e[4])
        assertTrue(e[5] * e[2] == -e[7])
        assertTrue(e[5] * e[3] == e[6])
        assertTrue(e[5] * e[4] == -e[1])
        assertTrue(e[5] * e[5] == -e[0])
        assertTrue(e[5] * e[6] == -e[3])
        assertTrue(e[5] * e[7] == e[2])
        assertTrue(e[6] * e[0] == e[6])
        assertTrue(e[6] * e[1] == e[7])
        assertTrue(e[6] * e[2] == e[4])
        assertTrue(e[6] * e[3] == -e[5])
        assertTrue(e[6] * e[4] == -e[2])
        assertTrue(e[6] * e[5] == e[3])
        assertTrue(e[6] * e[6] == -e[0])
        assertTrue(e[6] * e[7] == -e[1])
        assertTrue(e[7] * e[0] == e[7])
        assertTrue(e[7] * e[1] == -e[6])
        assertTrue(e[7] * e[2] == e[5])
        assertTrue(e[7] * e[3] == e[4])
        assertTrue(e[7] * e[4] == -e[3])
        assertTrue(e[7] * e[5] == -e[2])
        assertTrue(e[7] * e[6] == e[1])
        assertTrue(e[7] * e[7] == -e[0])
    }

    @Test
    fun `product of basis equals negative one`() {
        var product = 1.toOctonion()
        e.forEach { product *= it }
        assertTrue(product == -e[0])
    }

    @Test
    fun `multiplying two octonions with precomputed product`() {
        val oct1 = Octonion(o0 = 1, o1 = 1)
        val oct2 = Octonion(o0 = 1, o2 = 1)
        val product = Octonion(o0 = 1, o1 = 1, o2 = 1, o3 = 1)
        assertTrue(product == oct1 * oct2)
    }

    @Test
    fun `square and square root test`() {
        val o = Octonion { oComponentRange.random() }
        assertTrue(o.root(2).all { it * it == o })
        assertTrue(o.pow(2).root(2).contains(o))
        assertTrue(o.pow(2).root(2).contains(-o))
    }

    @Test
    fun `some other test`() {
        var n = 0
        var check: Boolean
        var o = Octonion()
        try {
            do {
                o = Octonion { oComponentRange.random() }
                check = o.pow(3).root(3).contains(o)
                n++
            } while (check || n < 1_000)
        } catch (e: InfiniteSetException) {
            println(o)
            println("n = $n")
            throw InfiniteSetException("Something happened", e)
        }
        println("Done")
    }

    @Test
    fun commutatorTest() {
        val o1 = Octonion { oComponentRange.random() }
        val o2 = Octonion { oComponentRange.random() }
        println(o1.commutator(o2))
        println(o2.commutator(o1))
        assertTrue(o1.commutator(o2) == -o2.commutator(o1))
    }
}