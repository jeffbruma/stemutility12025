package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.TOLERANCE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.sqrt

class ComplexTest {
    @Test
    fun `create Complex number using IntArray`() {
        val intArray = intArrayOf(1, 1)
        assertEquals(Complex(1, 1), Complex(intArray))
    }

    @Test
    fun `create Complex number using DoubleArray`() {
        val doubleArray = doubleArrayOf(1.0, 1.0)
        assertEquals(Complex(1, 1), Complex(doubleArray))
    }

    @Test
    fun `test toPolar conversion`() {
        val polar = Complex(1, 1).polarForm
        assertEquals(sqrt(2.0), polar.norm.toDouble(), TOLERANCE)
        assertEquals(PI / 4, polar.angle.toDouble(), TOLERANCE)
    }

    @Test
    fun `test fromPolar conversion`() {
        assertTrue(Complex(1, 0) == Complex.fromPolar(1, 0))
        assertTrue(Complex(0, 1) == Complex.fromPolar(1, PI / 2))
        assertTrue(Complex(-1, 0) == Complex.fromPolar(1, PI))
        assertTrue(Complex(0, -1) == Complex.fromPolar(1, -PI / 2))
    }

    @Test
    fun `test cis function`() {
        assertTrue(Complex(3, 4) == 5 cis 0.9272952180016)
    }

    @Test
    fun `test toComplex method`() {
        assertEquals(Complex(1, 0), 1.toComplex())
    }

    @Test
    fun `test toComplexRec method`() {
        assertEquals(Complex(3, 4), Pair(3, 4).toComplexRec())
    }

    @Test
    fun `test toComplexPol method`() {
        assertEquals(Complex.fromPolar(5, 0.927295218), Pair(5, 0.927295218).toComplexPol())
    }

    @Test
    fun `test i value`() {
        assertEquals(Complex(1, 2), 1 + 2.i)
    }

    @Test
    fun testSin() {
        assertEquals(Complex(0, 0), sin(Complex(0, 0)))
        assertEquals(Complex(0, 1.1752011936438014), sin(Complex(0, 1)))
    }

    @Test
    fun testCos() {
        assertEquals(Complex(1, 0), cos(Complex(0, 0)))
        assertEquals(Complex(1.543080634815244, 0), cos(Complex(0, 1)))
    }

    @Test
    fun testingZeros() {
        val z1 = 0.0 + 0.0.i
        val z2 = 0.0 - 0.0.i
        assertEquals(z1, z2)
    }

    @Test
    fun testTan() {
        assertEquals(Complex(0, 0), tan(Complex(0, 0)))
    }

    @Test
    fun testAsin() {
        val result = asin(Complex(0.5, 0))
        result.forEach { println(it) }
        assertTrue(result.any { it == Real(0.523598775598299) })
        assertTrue(result.any { it == Real(2.617993877991494) })
    }

    @Test
    fun testAcos() {
        val result = acos(Complex(0.5, 0))
        result.forEach { println(it) }
        assertTrue(result.any { it == Real(1.047197551196598) })
        assertTrue(result.any { it == Real(-1.047197551196598) })
    }

    @Test
    fun testAtan() {
        assertEquals(Complex(0, 0), atan(Complex(0, 0)))
    }

    @Test
    fun testExp() {
        assertEquals(Complex(1, 0), exp(Complex(0, 0)))
        assertEquals(Complex(2.718281828459045, 0), exp(Complex(1, 0)))
    }

    @Test
    fun testLog() {
        assertEquals(Complex(0, 0), log(Complex(1, 0)))
    }

    @Test
    fun testLogBaseTen() {
        assertEquals(0.1505149978319906 + 0.3410940884604603.i, log(Complex(1, 1), Complex(a = 10)))
    }

    @Test
    fun testSinh() {
        assertEquals(Complex(0, 0), sinh(Complex(0, 0)))
        assertEquals(Complex(0, 0.8414709848078965), sinh(Complex(0, 1)))
    }

    @Test
    fun testCosh() {
        assertEquals(Complex(1, 0), cosh(Complex(0, 0)))
        assertEquals(Complex(0.5403023058681398, 0), cosh(Complex(0, 1)))
    }

    @Test
    fun testTanh() {
        assertEquals(Complex(0, 0), tanh(Complex(0, 0)))
    }

    @Test
    fun testAsinh() {
        val result = asinh(Complex(0.5, 0))
        assertTrue(result.any { it == Real(0.481211825059603)})
        assertTrue(result.any { it == Complex(-0.481211825059603, 3.141592653589793)})
    }

    @Test
    fun testAcosh() {
        val result = acosh(Complex(1.5, 0))
        assertTrue(result.any { it == Real(0.962423650119207)})
        assertTrue(result.any { it == Real(-0.962423650119207)})
    }

    @Test
    fun testAtanh() {
        assertEquals(Complex(0, 0), atanh(Complex(0, 0)))
    }
}
