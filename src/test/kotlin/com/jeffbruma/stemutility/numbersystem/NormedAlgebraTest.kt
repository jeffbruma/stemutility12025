package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.acos
import com.jeffbruma.stemutility.miscellaneous.div
import com.jeffbruma.stemutility.miscellaneous.pow
import com.jeffbruma.stemutility.miscellaneous.sqrt
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.ulp

class NormedAlgebraTest {
    private val real = Real(1)
    private val comp = Complex { 1 }
    private val quat = Quaternion { 1 }
    private val octo = Octonion { 1 }
    private val sede = Sedenion { 1 }

    @Test
    fun `get norm`() {
        assertEquals(1, real.norm)
        assertEquals(sqrt(2), comp.norm)
        assertEquals(2, quat.norm)
        assertEquals(sqrt(8), octo.norm)
        assertEquals(4, sede.norm)
    }

    @Test
    fun `get polar form`() {
        assertTrue(PolarForm(1.0) == real.polarForm)
        assertTrue(PolarForm(sqrt(2), PI / 4, arrayOf(1)) == comp.polarForm)
        assertTrue(PolarForm(2.0, PI / 3, Array(3) { 3.pow(-0.5) }) == quat.polarForm)
        assertTrue(PolarForm(sqrt(8), acos(1 / sqrt(8)), Array(7) { 7.pow(-0.5) }) == octo.polarForm)
        assertTrue(PolarForm(4.0, acos(1 / 4.0), Array(15) { 15.pow(-0.5) }) == sede.polarForm)
    }

    @Test
    fun `get conjugate`() {
        assertEquals(2.inverse, 0.5)
        assertEquals(Real(1), real.conjugate)
        assertEquals(Complex(1, -1), comp.conjugate)
        assertEquals(Quaternion(1, -1, -1, -1), quat.conjugate)
        assertEquals(Octonion { i -> if (i == 0) 1 else -1 }, octo.conjugate)
        assertEquals(Sedenion { i -> if (i == 0) 1 else -1 }, sede.conjugate)
    }

    @Test
    fun `get inverse`() {
        assertEquals(Real(1), real.inverse)
        assertEquals(Complex(0.5, -0.5), comp.inverse)
        assertEquals(Quaternion(0.25, -0.25, -0.25, -0.25), quat.inverse)
        assertEquals(Octonion { i -> if (i == 0) 0.125 else -0.125}, octo.inverse)
        assertEquals(Sedenion { i -> if (i == 0) 0.0625 else -0.0625}, sede.inverse)
    }

    @Test
    fun getNormalized() {
        assertEquals(real, real.normalized)
        assertEquals(Complex(2.pow(-0.5), 1 / kotlin.math.sqrt(2.0)), comp.normalized)
        assertEquals(Quaternion(0.5, 0.5, 0.5, 0.5), quat.normalized)
        assertEquals(Octonion { 8.pow(-0.5) }, octo.normalized)
        assertEquals(Sedenion { 0.25 }, sede.normalized)
    }

    @Test
    fun `get component 0`() {
        assertEquals(1, real[0])
        assertEquals(1, comp[0])
        assertEquals(1, quat[0])
        assertEquals(1, octo[0])
        assertEquals(1, sede[0])
    }

    @Test
    fun `from polar to rectangular using polar form`() {
        assertTrue(comp == Complex.fromPolar(comp.polarForm))
        assertTrue(quat == Quaternion.fromPolar(quat.polarForm))
        assertTrue(octo == Octonion.fromPolar(octo.polarForm))
        assertTrue(sede == Sedenion.fromPolar(sede.polarForm))
    }

    @Test
    fun `unary minus`() {
        assertEquals(Real(-1), -real)
        assertEquals(Complex { -1 }, -comp)
        assertEquals(Quaternion { -1 }, -quat)
        assertEquals(Octonion { -1 }, -octo)
        assertEquals(Sedenion { -1 }, -sede)
    }

    @Test
    fun `addition of Number`() {
        assertEquals(Real(2), real + 1)
        assertEquals(Complex(2, 1), comp + 1)
        assertEquals(Quaternion(2, 1, 1, 1), quat + 1)
        assertEquals(Octonion { i -> if (i == 0) 2 else 1 }, octo + 1)
        assertEquals(Sedenion { i -> if (i == 0) 2 else 1 }, sede + 1)
    }

    @Test
    fun `addition with same type`() {
        assertEquals(Real(2), real + real)
        assertEquals(Complex(2, 2), comp + comp)
        assertEquals(Quaternion(2, 2, 2, 2), quat + quat)
        assertEquals(Octonion { 2 }, octo + octo)
        assertEquals(Sedenion { 2 }, sede + sede)
    }

    @Test
    fun `addition with different type`() {
        assertEquals(Complex(2, 1), real + comp)
        assertEquals(Quaternion(2, 1, 1, 1), real + quat)
        assertEquals(Octonion { i -> if (i == 0) 2 else 1 }, real + octo)
        assertEquals(Sedenion { i -> if (i == 0) 2 else 1 }, real + sede)

        assertEquals(Complex(2, 1), comp + real)
        assertEquals(Quaternion(2, 2, 1, 1), comp + quat)
        assertEquals(Octonion { i -> if (i in 0..1) 2 else 1 }, comp + octo)
        assertEquals(Sedenion { i -> if (i in 0..1) 2 else 1 }, comp + sede)

        assertEquals(Quaternion(2, 1, 1, 1), quat + real)
        assertEquals(Quaternion(2, 2, 1, 1), quat + comp)
        assertEquals(Octonion { i -> if (i in 0..3) 2 else 1 }, quat + octo)
        assertEquals(Sedenion { i -> if (i in 0..3) 2 else 1 }, quat + sede)

        assertEquals(Octonion { i -> if (i == 0) 2 else 1 }, octo + real)
        assertEquals(Octonion { i -> if (i in 0..1) 2 else 1 }, octo + comp)
        assertEquals(Octonion { i -> if (i in 0..3) 2 else 1 }, octo + quat)
        assertEquals(Sedenion { i -> if (i in 0..7) 2 else 1 }, octo + sede)

        assertEquals(Sedenion { i -> if (i == 0) 2 else 1 }, sede + real)
        assertEquals(Sedenion { i -> if (i in 0..1) 2 else 1 }, sede + comp)
        assertEquals(Sedenion { i -> if (i in 0..3) 2 else 1 }, sede + quat)
        assertEquals(Sedenion { i -> if (i in 0..7) 2 else 1 }, sede + octo)
    }

    @Test
    fun `subtraction of Number`() {
        assertEquals(Real(0), real - 1)
        assertEquals(Complex(0, 1), comp - 1)
        assertEquals(Quaternion(0, 1, 1, 1), quat - 1)
        assertEquals(Octonion { i -> if (i == 0) 0 else 1 }, octo - 1)
        assertEquals(Sedenion { i -> if (i == 0) 0 else 1 }, sede - 1)
    }

    @Test
    fun `subtraction with same type`() {
        assertEquals(0.toReal(), real - real)
        assertEquals(0.toComplex(), comp - comp)
        assertEquals(0.toQuaternion(), quat - quat)
        assertEquals(0.toOctonion(), octo - octo)
        assertEquals(0.toSedenion(), sede - sede)
    }

    @Test
    fun `subtraction with different type`() {
        assertEquals(Complex(0, -1), real - comp)
        assertEquals(Quaternion(0, -1, -1, -1), real - quat)
        assertEquals(Octonion { i -> if (i == 0) 0 else -1 }, real - octo)
        assertEquals(Sedenion { i -> if (i == 0) 0 else -1 }, real - sede)

        assertEquals(Complex(0, 1), comp - real)
        assertEquals(Quaternion(0, 0, -1, -1), comp - quat)
        assertEquals(Octonion { i -> if (i in 0..1) 0 else -1 }, comp - octo)
        assertEquals(Sedenion { i -> if (i in 0..1) 0 else -1 }, comp - sede)

        assertEquals(Quaternion(0, 1, 1, 1), quat - real)
        assertEquals(Quaternion(0, 0, 1, 1), quat - comp)
        assertEquals(Octonion { i -> if (i in 0..3) 0 else -1 }, quat - octo)
        assertEquals(Sedenion { i -> if (i in 0..3) 0 else -1 }, quat - sede)

        assertEquals(Octonion { i -> if (i == 0) 0 else 1 }, octo - real)
        assertEquals(Octonion { i -> if (i in 0..1) 0 else 1 }, octo - comp)
        assertEquals(Octonion { i -> if (i in 0..3) 0 else 1 }, octo - quat)
        assertEquals(Sedenion { i -> if (i in 0..7) 0 else -1 }, octo - sede)

        assertEquals(Sedenion { i -> if (i == 0) 0 else 1 }, sede - real)
        assertEquals(Sedenion { i -> if (i in 0..1) 0 else 1 }, sede - comp)
        assertEquals(Sedenion { i -> if (i in 0..3) 0 else 1 }, sede - quat)
        assertEquals(Sedenion { i -> if (i in 0..7) 0 else 1 }, sede - octo)
    }

    @Test
    fun `multiplication by a Number`() {
        assertEquals(Real(2), real * 2)
        assertEquals(Complex { 1 }, comp * 1)
        assertEquals(Quaternion { 0.5 }, quat * 0.5)
        assertEquals(Octonion { sqrt(2) }, octo * sqrt(2))
        assertEquals(Sedenion { 1 }, sede * 1)
    }

    @Test
    fun `multiplication by zero`() {
        val resultR: Real = real * 0
        val resultC: Complex = comp * 0
        val resultQ: Quaternion = quat * 0
        val resultO: Octonion = octo * 0
        val resultS: Sedenion = sede * 0

        assertEquals(Real(0), resultR)
        assertEquals(Complex { 0 }, resultC)
        assertEquals(Quaternion { 0 }, resultQ)
        assertEquals(Octonion { 0 }, resultO)
        assertEquals(Sedenion { 0 }, resultS)
    }

    @Test
    fun `multiplication by same type`() {
        assertEquals(Real(1), real * real)
        assertEquals(Complex(0, 2), comp * comp)
        assertEquals(Quaternion(-2, 2, 2, 2), quat * quat)
        assertEquals(Octonion(-6, 2, 2, 2, 2, 2, 2, 2), octo * octo)
        assertEquals(Sedenion { i -> if (i == 0) -14 else 2 }, sede * sede)
    }

    @Test
    fun `multiplication by different type`() {
        assertEquals(comp, real * comp)
        assertEquals(quat, real * quat)
        assertEquals(octo, real * octo)
        assertEquals(sede, real * sede)

        assertEquals(comp, comp * real)
        assertEquals(Quaternion(x = 2, z = 2), comp * quat)
        assertEquals(Octonion(o1 = 2, o3 = 2, o5 = 2, o6 = 2), comp * octo)
        assertEquals(Sedenion(s1 = 2, s3 = 2, s5 = 2, s6 = 2, s9 = 2, sA = 2, sC = 2, sF = 2), comp * sede)

        assertEquals(quat, quat * real)
        assertEquals(Quaternion(x = 2, y = 2), quat * comp)
        assertEquals(Octonion(-2, 2, 2, 2, -2, 2, 2, 2), quat * octo)
        assertEquals(Sedenion(-2, 2, 2, 2, -2, 2, 2, 2, -2, 2, 2, 2, 4), quat * sede)

        assertEquals(octo, octo * real)
        assertEquals(Octonion(o1 = 2, o2 = 2, o4 = 2, o7 = 2), octo * comp)
        assertEquals(Octonion(-2, 2, 2, 2, 4), octo * quat)
        assertEquals(Sedenion(-6, 2, 2, 2, 2, 2, 2, 2, -6, 2, 2, 2, 2, 2, 2, 2), octo * sede)

        assertEquals(sede, sede * real)
        assertEquals(Sedenion(s1 = 2, s2 = 2, s4 = 2, s7 = 2, s8 = 2, sB = 2, sD = 2, sE = 2), sede * comp)
        assertEquals(Sedenion(-2, 2, 2, 2, 4, s8 = 4, sC = - 2, sD = 2, sE = 2, sF = 2), sede * quat)
        assertEquals(Sedenion(-6, 2, 2, 2, 2, 2, 2, 2, 8), sede * octo)
    }

    @Test
    fun `division by a Number`() {
        assertEquals(real, real / 1)
        assertEquals(comp, comp / 1)
        assertEquals(quat, quat / 1)
        assertEquals(octo, octo / 1)
        assertEquals(sede, sede / 1)
    }

    @Test
    fun `division by the same type`() {
        assertEquals(1.toReal(), real / real)
        assertEquals(1.toComplex(), comp / comp)
        assertEquals(1.toQuaternion(), quat / quat)
        assertEquals(1.toOctonion(), octo / octo)
    }

    @Test
    fun `division by a different type`() {
        assertEquals(Complex(0.5, -0.5), real / comp)
        assertEquals(quat.inverse, real / quat)
        assertEquals(octo.inverse, real / octo)

        assertEquals(comp, comp / real)
        assertEquals(Quaternion(w = 0.5, z = -0.5), comp / quat)
        assertEquals(Octonion(o0 = 0.25, o3 = -0.25, o5 = -0.25, o6 = -0.25), comp / octo)

        assertEquals(quat, quat / real)
        assertEquals(Quaternion(w = 1, z = 1), quat / comp)
        assertEquals(Octonion(0.5, o4 = 0.25, o5 = -0.25, o6 = -0.25, o7 = -0.25), quat / octo)

        assertEquals(octo, octo / real)
        assertEquals(Octonion(1, o3 = 1, o5 = 1, o6 = 1), octo / comp)
        assertEquals(Octonion(1, o4 = -0.5, o5 = 0.5, o6 = 0.5, o7 = 0.5), octo / quat)

        assertEquals(sede, sede / real)
        assertEquals(Sedenion(1, s3 = 1, s5 = 1, s6 = 1, s9 = 1, sA = 1, sC = 1, sF = 1), sede / comp)
        assertEquals(Sedenion(1, s4 = - 0.5, s5 = 0.5, s6 = 0.5, s7 = 0.5, s8 = -0.5, s9 = 0.5, sA = 0.5, sB = 0.5, sC = 1), sede / quat)
        assertEquals(Sedenion(1, s8 = -0.75, s9 = 0.25, sA = 0.25, sB = 0.25, sC = 0.25, sD = 0.25, sE = 0.25, sF = 0.25), sede / octo)
    }

    @Test
    fun `zeroth power is one`() {
        val realNumber = Real.random()
        val complexNumber = Complex.random()
        val quaternionNumber = Quaternion.random()
        val octonionNumber = Octonion.random()
        val sedenionNumber = Sedenion.random()
        assertTrue(realNumber.pow(0) == 1.toReal())
        assertTrue(complexNumber.pow(0) == 1.toComplex())
        assertTrue(quaternionNumber.pow(0) == 1.toQuaternion())
        assertTrue(octonionNumber.pow(0) == 1.toOctonion())
        assertTrue(sedenionNumber.pow(0) == 1.toSedenion())
    }

    @Test
    fun `first power is itself`() {
        val realNumber = Real.random()
        val complexNumber = Complex.random()
        val quaternionNumber = Quaternion.random()
        val octonionNumber = Octonion.random()
        val sedenionNumber = Sedenion.random()
        assertTrue(realNumber.pow(1) == realNumber)
        assertTrue(complexNumber.pow(1) == complexNumber)
        assertTrue(quaternionNumber.pow(1) == quaternionNumber)
        assertTrue(octonionNumber.pow(1) == octonionNumber)
        assertTrue(sedenionNumber.pow(1) == sedenionNumber)
    }

    @Test
    fun `positive nth power`() {
        val realNumber = Real.random()
        val complexNumber = Complex.random()
        val quaternionNumber = Quaternion.random()
        val octonionNumber = Octonion.random()
        val sedenionNumber = Sedenion.random()
        assertTrue(realNumber.pow(3) == realNumber * realNumber * realNumber)
        println(complexNumber.pow(3))
        println(complexNumber * (complexNumber * complexNumber))
        assertTrue(complexNumber.pow(3) == complexNumber * complexNumber * complexNumber)
        assertTrue(quaternionNumber.pow(3) == quaternionNumber * (quaternionNumber * quaternionNumber))
        assertTrue(octonionNumber.pow(3) == octonionNumber * (octonionNumber * octonionNumber))
        assertTrue(sedenionNumber.pow(3) == sedenionNumber * (sedenionNumber * sedenionNumber))
    }

    @Test
    fun `negative first power is the inverse`() {
        val realNumber = Real.random()
        val complexNumber = Complex.random()
        val quaternionNumber = Quaternion.random()
        val octonionNumber = Octonion.random()
        val sedenionNumber = Sedenion.random()
        assertTrue(realNumber.pow(-1) == realNumber.inverse)
        assertTrue(complexNumber.pow(-1) == complexNumber.inverse)
        assertTrue(quaternionNumber.pow(-1) == quaternionNumber.inverse)
        assertTrue(octonionNumber.pow(-1) == octonionNumber.inverse)
        assertTrue(sedenionNumber.pow(-1) == sedenionNumber.inverse)
    }

    @Test
    fun `negative nth power is nth power of inverse`() {
        val range = -99..99
        val realNumber = Real.random(range)
        val complexNumber = Complex.random(range)
        val quaternionNumber = Quaternion.random(range)
        val octonionNumber = Octonion.random(range)
        val sedenionNumber = Sedenion.random(range)
        assertTrue(realNumber.pow(-3) == realNumber.inverse.pow(3))
        assertTrue(complexNumber.pow(-3) == complexNumber.inverse.pow(3))
        assertTrue(quaternionNumber.pow(-3) == quaternionNumber.inverse.pow(3))
        assertTrue(octonionNumber.pow(-3) == octonionNumber.inverse.pow(3))
        assertTrue(sedenionNumber.pow(-3) == sedenionNumber.inverse.pow(3))
    }

    @Test
    fun `hashCode should be consistent for same values`() {
        val h1 = Quaternion { it + 1 } // Quaternion(1, 2, 3, 4)
        val h2 = Quaternion { it + 1 } // Quaternion(1, 2, 3, 4)

        assertEquals(h1.hashCode(), h2.hashCode())
    }

    @Test
    fun `hashCode should treat 0_0 and minus_0_0 as the same`() {
        val h1 = Quaternion { 0.0 }
        val h2 = Quaternion { -0.0 }

        assertEquals(h1.hashCode(), h2.hashCode())
    }

    @Test
    fun `hashCode should differ for different values`() {
        val h1 = Quaternion.random()
        val h2 = Quaternion.random()

        assertNotEquals(h1.hashCode(), h2.hashCode())
    }

    @Test
    fun `relative difference of 0_0 and negative 0_0 is negligible`() {
        assertEquals(Real(0.0), Real(-0.0))
    }

    @Test
    fun `equals in the context of normed algebra means equivalent`() {
        assertTrue(Complex(a = 1).equals(real))
        assertTrue(Complex(a = 1).equals(1))
        assertTrue(Quaternion(w = 1, x = 1).equals(comp))
        assertTrue(Quaternion(w = 1).equals(real))
        assertTrue(Quaternion(w = 1).equals(1))
        assertTrue(Octonion(o0 = 1, o1 = 1, o2 = 1, o3 = 1).equals(quat))
        assertTrue(Octonion(o0 = 1, o1 = 1).equals(comp))
        assertTrue(Octonion(o0 = 1).equals(real))
        assertTrue(Octonion(o0 = 1).equals(1))
        assertTrue(Sedenion { i -> if (i in 0..7) 1 else 0}.equals(octo))
        assertTrue(Sedenion(s0 =1, s1 = 1, s2 = 1, s3 = 1).equals(quat))
        assertTrue(Sedenion(s0 = 1, s1 = 1).equals(comp))
        assertTrue(Sedenion(s0 = 1).equals(real))
        assertTrue(Sedenion(s0 = 1).equals(1))
    }

    @Test
    fun `Numbers are the same across all normed algebra`() {
        val allAreZero = listOf(
            Real.Zero.equals(0),
            Complex().equals(0),
            Quaternion().equals(0),
            Octonion().equals(0),
            Sedenion().equals(0),
            Real().equals(Complex.Zero),
            Real().equals(Quaternion.Zero),
            Real().equals(Octonion.Zero),
            Real().equals(Sedenion.Zero),
            Complex().equals(Quaternion.Zero),
            Complex().equals(Octonion.Zero),
            Complex().equals(Sedenion.Zero),
            Quaternion().equals(Octonion.Zero),
            Quaternion().equals(Sedenion.Zero),
            Octonion().equals(Sedenion.Zero)
        )
        assertTrue(allAreZero.all { it })
    }

    @Test
    fun toComplex() {
        assertEquals(Complex(1, 0), real.toComplex())
    }

    @Test
    fun toQuaternion() {
        assertEquals(Quaternion(w = 1, x = 1), comp.toQuaternion())
        assertEquals(Quaternion(w = 1), real.toQuaternion())
    }

    @Test
    fun toOctonion() {
        assertEquals(Octonion(o0 = 1, o1 = 1, o2 = 1, o3 = 1), quat.toOctonion())
        assertEquals(Octonion(o0 = 1, o1 = 1), comp.toOctonion())
        assertEquals(Octonion(o0 = 1), real.toOctonion())
    }

    @Test
    fun toSedenion() {
        assertEquals(Sedenion { i -> if (i in 0..7) 1 else 0}, octo.toSedenion())
        assertEquals(Sedenion(s0 = 1, s1 = 1, s2 = 1, s3 = 1), quat.toSedenion())
        assertEquals(Sedenion(s0 = 1, s1 = 1), comp.toSedenion())
        assertEquals(Sedenion(s0 = 1), real.toSedenion())
    }

    @Test
    fun ` test contentEquals for sets`() {
        val q1 = Quaternion(1, 2, -3, -4)
        val q2 = Quaternion(-2, 3, 5, -7)
        val set1 = setOf(q1, q2)
        val set2 = setOf(q2 - 1.0.ulp, q1)
        assertTrue(set1.contentEquals(set2))
    }

    @Test
    fun `test hasZeroScalar`() {
        val q = Quaternion(0, 1, 2, 3)
        assertTrue(q.hasZeroScalar())
    }


    @Test
    fun `powers of normed algebra exponents`() {
        val r = Real(Int.MAX_VALUE)
        println("r = $r")
        println("r × r = ${r * r}")
        println("r × r × r = ${r * r * r}")
        println("r^2 = ${r.pow(2)}")
        println("r^3 = ${r.pow(3)}")

    }
}