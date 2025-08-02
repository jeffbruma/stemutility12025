package com.jeffbruma.stemutility.miscellaneous.integer
import com.jeffbruma.stemutility.miscellaneous.decimal.sinh
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.minus
import kotlin.plus
import kotlin.time.measureTime
import kotlin.times
import kotlin.unaryMinus

class BigIntegerMathTest {
    @Nested
    inner class ArithmeticOperatorTest {
        @Test
        fun `unaryMinus operator returns opposite signed BigInteger`() {
            assertEquals(BigInteger("-1"), -BigInteger.ONE)
            assertEquals(BigInteger.ONE, -BigInteger("-1"))
        }

        @Test
        fun `plus operator returns sum of the BigInteger addends`() {
            assertEquals(BigInteger.TWO, BigInteger.ONE + BigInteger.ONE)
            assertEquals(BigInteger.valueOf(-1L), BigInteger.valueOf(-3L) + BigInteger.TWO)
        }

        @Test
        fun `minus operator returns the difference of the BigInteger minuend and subtrahend`() {
            assertEquals(BigInteger.ONE, BigInteger.TWO - BigInteger.ONE)
            assertEquals(BigInteger.valueOf(-9L), BigInteger.ONE - BigInteger.TEN)
        }

        @Test
        fun `times operator returns the product of BigInteger factors`() {
            assertEquals(BigInteger.TEN, BigInteger.TWO * BigInteger.valueOf(5L))
            assertEquals(-BigInteger.TWO, -BigInteger.ONE * BigInteger.TWO)
        }
    }

    @Nested
    inner class PredicateTest {
        @Test
        fun `isNegative returns true when BigInteger is less than zero`() {
            assertTrue(BigInteger.valueOf(-2L).isNegative())
            assertFalse(BigInteger.TWO.isNegative())
        }

        @Test
        fun `isZeroOrNegative returns true when BigInteger is zero or lesser`() {
            assertTrue(BigInteger.valueOf(-2L).isNegative())
            assertTrue(BigInteger.ZERO.isZeroOrLesser())
            assertFalse(BigInteger.TWO.isNegative())
        }

        @Test
        fun `isZero returns true when BigInteger is zero`() {
            assertTrue(BigInteger.ZERO.isZero())
            assertTrue(BigInteger("0").isZero())
            assertTrue(BigInteger.valueOf(0L).isZero())
        }

        @Test
        fun `isNotZero returns true when BigInteger is greater than or lesser than zero`() {
            assertTrue(BigInteger.ONE.isNotZero())
            assertTrue(BigInteger.TWO.isNotZero())
            assertFalse(BigInteger.ZERO.isNotZero())

        }

        @Test
        fun `isZeroOrPositive returns true when BigInteger is zero or greater`() {
            assertTrue(BigInteger.ZERO.isZeroOrGreater())
            assertTrue(BigInteger.ONE.isZeroOrGreater())
            assertFalse(BigInteger("-2").isZeroOrGreater())
        }

        @Test
        fun `isOne returns true when BigInteger is one`() {
            assertTrue(BigInteger.ONE.isOne())
            assertTrue(BigInteger.valueOf(1L).isOne())
            assertTrue(BigInteger("1").isOne())
            assertFalse(BigInteger.ZERO.isOne())
        }

        @Test
        fun `isPositive returns true when BigInteger is greater than zero`() {
            assertTrue(BigInteger.ONE.isPositive())
            assertTrue(BigInteger.TWO.isPositive())
            assertFalse(BigInteger.ZERO.isPositive())
        }

        @Test
        fun `isEven returns true when BigInteger is divisible by two`() {
            assertTrue(BigInteger.ZERO.isEven())
            assertTrue(BigInteger.TWO.isEven())
            assertTrue(BigInteger("-2").isEven())
            assertFalse(BigInteger("3").isEven())
        }

        @Test
        fun `isOdd returns true when BigInteger is not divisible by two`() {
            assertTrue(BigInteger.ONE.isOdd())
            assertTrue(BigInteger("-1").isOdd())
        }
    }

    @Nested
    inner class NonOperatorArithmeticTest {
        @Test
        fun `pow returns one when any BigInteger is raised to zero`() {
            val anyBigInteger = random(BigInteger("-100"), BigInteger("100"))
            assertEquals(BigInteger.ONE, anyBigInteger.pow(BigInteger.ZERO))
        }

        @Test
        fun `pow returns the same number when raised to one`() {
            val anyBigInteger = random(BigInteger("-100"), BigInteger("100"))
            assertEquals(anyBigInteger, anyBigInteger.pow(BigInteger.ONE))
        }

        @Test
        fun `pow throws when power is negative`() {
            val anyBigInteger = random(BigInteger("-100"), BigInteger("100"))
            val exception = assertThrows<IllegalArgumentException> {
                anyBigInteger.pow(BigInteger("-2"))
            }
            assertEquals(
                "Negative exponents yield fractional results, which truncate to 0 in integer division.",
                exception.message
            )
        }

        @Test
        fun `root returns integer part of BigInteger rooted by another integer`() {
            assertEquals(BigInteger.TWO, BigInteger("4").root(2))
            assertEquals(-BigInteger.TWO, BigInteger("-8").root(3))
            assertEquals(BigInteger("3"), BigInteger.TEN.root(2))
            assertEquals(BigInteger("-3"), BigInteger("-30").root(3))
        }

        @Test
        fun `root throws when BigInteger is negative and degree is even`() {
            val exception = assertThrows<ArithmeticException> {
                BigInteger("-11").root(4)
            }
            assertEquals("(-11).root(4) is a complex number", exception.message)
        }

        @Test
        fun `abs returns absolute value of BigInteger`() {
            assertEquals(BigInteger.ONE, abs(BigInteger("-1")))
            assertEquals(BigInteger.ONE, abs(BigInteger.ONE))
        }

        @Test
        fun `max returns the larger of two BigIntegers`() {
            assertEquals(BigInteger.ONE, max(BigInteger.ONE, BigInteger.ZERO))
            assertEquals(BigInteger.ONE, max(-BigInteger.ONE, BigInteger.ONE))
        }

        @Test
        fun `min returns the smaller of two BigIntegers`() {
            assertEquals(BigInteger.ZERO, min(BigInteger.ONE, BigInteger.ZERO))
            assertEquals(-BigInteger.ONE, min(-BigInteger.ONE, BigInteger.ONE))
        }

        @Test
        fun `gcf returns greatest common factor of two BigIntegers`() {
            assertEquals(BigInteger.TEN, gcf(BigInteger.TEN, BigInteger("30")))
            assertEquals(BigInteger.TWO, gcf(BigInteger.TWO, BigInteger("4")))
        }

        @Test
        fun `lcm returns least common multiple of two BigIntegers`() {
            assertEquals(BigInteger("6"), lcm(BigInteger.TWO, BigInteger("3")))
            assertEquals(BigInteger("4"), lcm(BigInteger.TWO, BigInteger("4")))
        }

        @Test
        fun `sqrt returns the integer part of the square root of a BigInteger`() {
            assertEquals(BigInteger.TWO, sqrt(BigInteger("4")))
            assertEquals(BigInteger.TWO, sqrt(BigInteger("7")))
            assertEquals(BigInteger.TWO, sqrt(BigInteger("8")))
        }

        @Test
        fun `sqrt throws when BigInteger is negative and degree is even`() {
            val exception = assertThrows<ArithmeticException> {
                sqrt(BigInteger("-11"))
            }
            assertEquals("Negative BigInteger", exception.message)
        }
    }

    @Nested
    inner class ExponentialTest {
        @Test
        fun `exp returns the integer part of the exponential of a BigInteger`() {
            assertEquals(BigInteger.TWO, exp(BigInteger.ONE))
            assertEquals(BigInteger("7"), exp(BigInteger.TWO))
        }

        @Test
        fun `exp returns one when n is zero`() {
            assertEquals(BigInteger.ONE, exp(BigInteger.ZERO))
        }

        @Test
        fun `exp returns zero for negative BigIntegers`() {
            assertEquals(BigInteger.ZERO, exp(-BigInteger.ONE))
        }

        @Test
        fun `input that takes approximately one second to return`() {
            val time = measureTime { exp(BigInteger("6500")) }
            println("Time: $time")
        }
    }

    @Nested
    inner class LogarithmicTest {
        @Test
        fun `log returns zero when BigInteger is one`() {
            assertEquals(BigInteger.ZERO, log(BigInteger.ONE))
        }

        @Test
        fun `log throws when BigInteger is less than one`() {
            val exception = assertThrows<ArithmeticException> { (log(BigInteger.ZERO)) }
            assertEquals("log(n) is undefined for n < 1", exception.message)
        }
    }

    @Nested
    inner class HyperbolicTest {
        @Test
        fun `sinh returns the integer part of the hyperbolic sine of a BigInteger`() {
            assertEquals(BigInteger("13440585709080677242063127757900067936805559"), sinh(BigInteger("100")))
        }

        @Test
        fun `cosh returns the integer part of the hyperbolic cosine of a BigInteger`() {
            assertEquals(BigInteger("13440585709080677242063127757900067936805559"), cosh(BigInteger("100")))
        }

        @Test
        fun `tanh returns the integer part of the hyperbolic tangent of a BigInteger`() {
            assertEquals(BigInteger.ZERO, tanh(BigInteger("100")))
        }

        @Test
        fun `csch returns the integer part of the hyperbolic cosecant of a BigInteger`() {
            assertEquals(BigInteger.ZERO, csch(BigInteger("100")))
        }

        @Test
        fun `sech returns the integer part of the hyperbolic secant of a BigInteger`() {
            assertEquals(BigInteger.ZERO, sech(BigInteger("100")))
        }

        @Test
        fun `coth returns the integer part of the hyperbolic cotangent of a BigInteger`() {
            assertEquals(BigInteger.ONE, coth(BigInteger("100")))
        }

        @Test
        fun `arsinh returns the integer part of the hyperbolic arcsine of a BigInteger`() {
            assertEquals(BigInteger(""), arsinh(BigInteger("100")))
        }

        @Test
        fun `arcosh returns the integer part of the hyperbolic arccosine of a BigInteger`() {
            assertEquals(BigInteger(""), arcosh(BigInteger("100")))
        }

        @Test
        fun `artanh returns the integer part of the hyperbolic arctangent of a BigInteger`() {
            assertEquals(BigInteger(""), artanh(BigInteger("100")))
        }

        @Test
        fun `arcsch returns the integer part of the hyperbolic arccosecant of a BigInteger`() {
            assertEquals(BigInteger(""), arcsch(BigInteger("100")))
        }

        @Test
        fun `arsech returns the integer part of the hyperbolic arcsecant of a BigInteger`() {
            assertEquals(BigInteger(""), arsech(BigInteger("100")))
        }

        @Test
        fun `arcoth returns the integer part of the hyperbolic arccotangent of a BigInteger`() {
            assertEquals(BigInteger(""), arcoth(BigInteger("100")))
        }

        @Test
        fun `input that takes approximately one second to return`() {
            val time = measureTime { sinh(BigDecimal("6500")) }
            println("Time: $time")
        }
    }

    @Nested
    inner class TrigonometricTest

    @Nested
    inner class MiscellaneousFunctionsTest
}