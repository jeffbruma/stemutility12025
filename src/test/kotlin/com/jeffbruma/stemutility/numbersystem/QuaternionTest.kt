package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.TOLERANCE
import com.jeffbruma.stemutility.miscellaneous.div
import com.jeffbruma.stemutility.miscellaneous.root
import com.jeffbruma.stemutility.miscellaneous.times
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.PI

class QuaternionTest {
    private val e = listOf(
        Quaternion(w = 1),
        Quaternion(x = 1),
        Quaternion(y = 1),
        Quaternion(z = 1)
    )
    private val componentRange = -9..9

    @Test
    fun `multiplication table for quaternion`() {
        assertTrue(e[0] * e[0] == e[0])
        assertTrue(e[0] * e[1] == e[1])
        assertTrue(e[0] * e[2] == e[2])
        assertTrue(e[0] * e[3] == e[3])
        assertTrue(e[1] * e[0] == e[1])
        assertTrue(e[1] * e[1] == -e[0])
        assertTrue(e[1] * e[2] == e[3])
        assertTrue(e[1] * e[3] == -e[2])
        assertTrue(e[2] * e[0] == e[2])
        assertTrue(e[2] * e[1] == -e[3])
        assertTrue(e[2] * e[2] == -e[0])
        assertTrue(e[2] * e[3] == e[1])
        assertTrue(e[3] * e[0] == e[3])
        assertTrue(e[3] * e[1] == e[2])
        assertTrue(e[3] * e[2] == -e[1])
        assertTrue(e[3] * e[3] == -e[0])
    }

    @Test
    fun `product of bases equals negative one`() {
        assertTrue(e[1] * e[2] * e[3] == -e[0])
    }

    @Test
    fun `quaternion raised to zeroth power equals one`() {
        val h = Quaternion.random(componentRange)
        assertTrue(1.toQuaternion() == h.pow(0))
    }

    @Test
    fun `quaternion raised to first power equals itself`() {
        val h = Quaternion.random(componentRange)
        assertTrue(h == h.pow(1))
    }

    @Test
    fun `pure real quaternion raised to positive power equals the usual`() {
        val h = Quaternion(w = 2)
        assertTrue(4.toQuaternion() == h.pow(2))
    }

    @Test
    fun `quaternion raised to negative power equals inverse raised to power`() {
        val h = Quaternion.random(componentRange)
        val exponent = (Byte.MIN_VALUE..-1).random()
        assertTrue(h.pow(exponent) == h.inverse.pow(-exponent))
    }

    @Test
    fun `quaternion powers`() {
        val number = Int.MAX_VALUE.root(3).toInt()
        val testRange = -number..number
        val h = Quaternion { testRange.random() }
        val hCubed = h.pow(3)
        assertTrue(h * h * h == hCubed)
    }

    @Test
    fun `number extensions of basis`() {
        val h = 1 + 2.i + 3.j + 4.k
        assertTrue(Quaternion(1, 2, 3, 4) == h)
    }

    @Test
    fun `conjugate of a product of two quaternions is the product of the conjugates in the reverse order`() {
        val h1 = Quaternion.random(componentRange)
        val h2 = Quaternion.random(componentRange)
        val conjugateOfProduct = (h1 * h2).conjugate
        val productOfConjugateInTheReverseOrder = h2.conjugate * h1.conjugate
        assertTrue(conjugateOfProduct == productOfConjugateInTheReverseOrder)
    }

    @Test
    fun `quaternion norms are multiplicative`() {
        val h1 = Quaternion.random(componentRange)
        val h2 = Quaternion.random(componentRange)
        val h3 = h1 * h2
        val productOfNorms = h1.norm * h2.norm
        val normOfProduct = h3.norm
        assertTrue(kotlin.math.abs(1.0 - (productOfNorms / normOfProduct).toDouble()) < TOLERANCE)
    }

    @Test
    fun `quaternion distance to another quaternion`() {
        val h1 = Quaternion.random(componentRange)
        val h2 = Quaternion.random(componentRange)
        assertTrue(kotlin.math.abs(1.0 - (h1.distance(h2) / h2.distance(h1)).toDouble()) < TOLERANCE)
    }

    @Test
    fun `pair of complex can be converted to quaternion`() {
        val c1 = Complex.random(componentRange)
        val c2 = Complex.random(componentRange)
        val h = Pair(c1, c2).toQuaternion()
        assertTrue(c1 + c2 * 1.j == h)
    }

    @Test
    fun `quaternion multiplication should not be commutative`() {
        repeat(10) {
            val h1 = Quaternion.random(componentRange)
            val h2 = Quaternion.random(componentRange)
            if (h1 != h2) {
                assertNotEquals(h1 * h2, h2 * h1, "Quaternion multiplication should be non-commutative")
            }
        }
    }

    @Test
    fun `geodesic distance`() {
        val h1 = Quaternion.random(componentRange)
        val h2 = Quaternion.random(componentRange)
        val h1h2 = h1.geodesicDistance(h2)
        val h2h1 = h2.geodesicDistance(h1)
        assertTrue(kotlin.math.abs(1.0 - (h1h2 / h2h1).toDouble()) < TOLERANCE)
    }

    @Test
    fun `exponential and logarithm`() {
        val h = Quaternion.random(-1.0, 1.0) // Quaternion components are between -1.0 and 1.0
        assertTrue(exp(log(h)) == h)
        //assertTrue(log(exp(h)) == h) // Not always true; in fact, one has yet to see this be true
        assertTrue(log(h) == log(h, Quaternion(w = 2.718281828459045)))
        assertTrue(log(1.toQuaternion()) == Quaternion()) // Quaternion() is the zero quaternion
        assertTrue(
            log(Quaternion(x = 1)) == Quaternion(x = PI / 2) &&
            log(Quaternion(y = 1)) == Quaternion(y = PI / 2) &&
            log(Quaternion(z = 1)) == Quaternion(z = PI / 2)
        )
    }

    @Test
    fun `hyperbolic functions, inverses, and identities`() {
        val h = Quaternion.random(componentRange)
        assertTrue(sinh(-h) == -sinh(h))
        assertTrue(cosh(-h) == cosh(h))
        assertTrue(tanh(-h) == -tanh(h))
    }

    @Test
    fun `trigonometric functions, inverses, and identities`() {
        val h = Quaternion.random(componentRange).normalized
        assertTrue(sin(-h) == -sin(h))
        assertTrue(cos(-h) == cos(h))
        assertTrue(tan(-h) == -tan(h))
        println("h = $h")
        asin(sin(h)).forEach { println("asin(sin(h)) = $it") }
        acos(cos(h)).forEach { println("acos(cos(h)) = $it") }
        println("atan(tan(h)) = ${atan(tan(h))}")
        asin(h).forEach { println("sin(asin(h) = ${sin(it)}") }
        acos(h).forEach { println("cos(acos(h) = ${cos(it)}") }
        println("tan(atan(h)) = ${tan(atan(h))}")
        println(cos(h).pow(2) + sin(h).pow(2))
    }
}