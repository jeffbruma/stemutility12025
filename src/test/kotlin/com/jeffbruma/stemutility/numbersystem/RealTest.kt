package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.acos
import com.jeffbruma.stemutility.miscellaneous.arcosh
import com.jeffbruma.stemutility.miscellaneous.arsinh
import com.jeffbruma.stemutility.miscellaneous.artanh
import com.jeffbruma.stemutility.miscellaneous.asin
import com.jeffbruma.stemutility.miscellaneous.atan
import com.jeffbruma.stemutility.miscellaneous.atan2
import com.jeffbruma.stemutility.miscellaneous.cos
import com.jeffbruma.stemutility.miscellaneous.cosh
import com.jeffbruma.stemutility.miscellaneous.exp
import com.jeffbruma.stemutility.miscellaneous.sin
import com.jeffbruma.stemutility.miscellaneous.sinh
import com.jeffbruma.stemutility.miscellaneous.tan
import com.jeffbruma.stemutility.miscellaneous.tanh
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.ulp
import kotlin.random.Random

class RealTest {
    private val realDomain: Real
        get() = Real.random(-100.0, 100.0)

    private val positiveDomain: Real
        get() = Real.random(Double.MIN_VALUE, 100.0)

    private val negativeDomain: Real
        get() = Real.random(-100.0, -Double.MIN_VALUE)

    private val logBaseRealDomain: Real
        get() {
            fun randomize(): Double {
                val x = Random.nextDouble(-100.0, 100.0)
                return if (x == 1.0) randomize() else x
            }
            return Real(randomize())
        }

    private val acoshRealDomain: Real
        get() = Real.random(1.0, Double.MAX_VALUE)

    private val atanhRealDomain: Real
        get() = Real.random(-1.0 + (-1.0).ulp, 1.0)

    private val acosRealDomain: Real
        get() = Real.random(-1.0, 1.0 + 1.0.ulp)

    @Test
    fun `exponential and logarithm of real`() {
        val real = realDomain
        val logBase = logBaseRealDomain
        val posDomain = positiveDomain
        val negDomain = negativeDomain
        assertTrue(exp(real) == Real(exp(real.r)))
        assertTrue(exp(Real(0)) == Real(1))
        assertTrue(log(posDomain) is Real)
        assertTrue(log(negDomain) is Complex)
        assertTrue(log(real, logBase) is Real || log(real, logBase) is Complex)
        assertTrue(exp(log(posDomain) as Real) == posDomain)
        assertTrue(log(exp(posDomain)) == posDomain)
        assertThrows<ArithmeticException> {
            log(Real(0))
        }
        assertThrows<ArithmeticException> {
            log(realDomain, Real(1))
        }
    }

    @Test
    fun `hyperbolic functions of real`() {
        val real = realDomain
        assertTrue(cosh(real) == Real(cosh(real.r)))
        assertTrue(sinh(real) == Real(sinh(real.r)))
        assertTrue(tanh(real) == Real(tanh(real.r)))
    }

    @Test
    fun `inverse hyperbolic functions of real`() {
        val acoshSample = acoshRealDomain
        val asinhSample = realDomain
        val atanhSample = atanhRealDomain
        assertTrue(acosh(acoshSample).any { it == Real(arcosh(acoshSample.r)) })
        assertTrue(asinh(asinhSample) == Real(arsinh(asinhSample.r)))
        assertTrue(atanh(atanhSample) == Real(artanh(atanhSample.r)))
    }

    @Test
    fun `trigonometric functions of real`() {
        val real = realDomain
        assertTrue(cos(real) == Real(cos(real.r)))
        assertTrue(sin(real) == Real(sin(real.r)))
        assertTrue(tan(real) == Real(tan(real.r)))
    }

    @Test
    fun `inverse trigonometric functions of real`() {
        val asinAcosSample = acosRealDomain
        val atanSample1 = realDomain
        val atanSample2 = realDomain
        assertTrue(acos(asinAcosSample) == setOf(Real(acos(asinAcosSample.r))))
        assertTrue(asin(asinAcosSample) == Real(asin(asinAcosSample.r)))
        assertTrue(atan(atanSample1) == Real(atan(atanSample1.r)))
        assertTrue(atan2(atanSample1, atanSample2) == Real(atan2(atanSample1.r, atanSample2.r)))
    }
}
