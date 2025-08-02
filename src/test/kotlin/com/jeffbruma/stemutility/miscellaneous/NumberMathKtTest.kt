@file:Suppress("unused")

package com.jeffbruma.stemutility.miscellaneous

import com.jeffbruma.stemutility.miscellaneous.decimal.exp
import com.jeffbruma.stemutility.miscellaneous.decimal.isZero
import com.jeffbruma.stemutility.miscellaneous.decimal.log
import com.jeffbruma.stemutility.miscellaneous.decimal.times
import com.jeffbruma.stemutility.miscellaneous.integer.isZero
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.ulp
import kotlin.random.Random

class NumberMathKtTest {
    private val all: Number
        get() = Random.nextDouble(-Double.MAX_VALUE, Double.MAX_VALUE)

    private val naturalLogInputRange: Number
        get() = Random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)

    private val logBaseInputRange: Number
        get() {
            fun randomize(): Number {
                val x = Random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)
                return if (x == 1.0) randomize() else x
            }
            return randomize()
        }

    private val acoshInputRange: Number
        get() = Random.nextDouble(1.0, Double.MAX_VALUE)

    private val atanhInputRange: Number
        get() = Random.nextDouble(-1.0 + (-1.0).ulp, 1.0)

    private val acosInputRange: Number
        get() = Random.nextDouble(-1.0, 1.0 + 1.0.ulp)

    private val unsupportedNumber: Number = object : Number() {
        override fun toByte(): Byte = 0
        override fun toShort(): Short = 0
        override fun toInt() = 0
        override fun toLong() = 0L
        override fun toFloat() = 0f
        override fun toDouble() = 0.0
    }

    @Test
    fun toBigDecimalConversions() {
        // Test with BigDecimal input
        val bigDecimalInput: Number = BigDecimal("123.456")
        assertEquals(bigDecimalInput, bigDecimalInput.toBigDecimal())

        // Test with BigInteger input
        val bigIntegerInput: Number = BigInteger("123456")
        assertEquals(BigDecimal("123456"), bigIntegerInput.toBigDecimal())

        // Test with Long input
        val longInput: Number = 123456789L
        assertEquals(BigDecimal("123456789"), longInput.toBigDecimal())

        // Test with Int input
        val intInput: Number = 12345
        assertEquals(BigDecimal("12345"), intInput.toBigDecimal())

        // Test with Double input
        val doubleInput: Number = 12345.6789
        assertEquals(BigDecimal.valueOf(doubleInput.toDouble()), doubleInput.toBigDecimal())

        // Test with Float input
        val floatInput: Number = 12345.678f
        assertEquals(BigDecimal.valueOf(floatInput.toDouble()), floatInput.toBigDecimal())

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber.toBigDecimal()
        }
    }

    @Test
    fun toBigDecimalExactTest() {
        // Test with exact BigDecimal input
        assertEquals(BigDecimal("123"), BigDecimal("123").toBigDecimalExact())
        assertThrows<ArithmeticException> { BigDecimal("123.456").toBigDecimalExact() }

        // Test with BigInteger input
        assertEquals(BigDecimal("123456"), BigInteger("123456").toBigDecimalExact())

        // Test with Long input
        assertEquals(BigDecimal("123456789"), 123456789L.toBigDecimalExact())

        // Test with Int input
        assertEquals(BigDecimal("12345"), 12345.toBigDecimalExact())

        // Test with exact Double input
        assertEquals(BigDecimal("123.0"), 123.0.toBigDecimalExact())
        assertThrows<ArithmeticException> { 123.456.toBigDecimalExact() }

        // Test with Float input
        assertEquals(BigDecimal("123.0"), 123.0f.toBigDecimalExact())
        assertThrows<ArithmeticException> {
            123.456f.toBigDecimalExact()
        }

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber.toBigDecimalExact()
        }
    }

    @Test
    fun toBigIntegerConversions() {
        // Test with BigInteger input
        val bigIntegerInput: Number = BigInteger("123456")
        assertEquals(BigInteger("123456"), bigIntegerInput.toBigInteger())

        // Test with BigDecimal input
        val bigDecimalInput: Number = BigDecimal("123.456")
        assertEquals(BigInteger("123"), bigDecimalInput.toBigInteger())

        // Test with Long input
        val longInput: Number = 123456789L
        assertEquals(BigInteger.valueOf(123456789L), longInput.toBigInteger())

        // Test with Int input
        val intInput: Number = 12345
        assertEquals(BigInteger.valueOf(12345L), intInput.toBigInteger())

        // Test with Double input
        val doubleInput: Number = 12345.6789
        assertEquals(BigInteger.valueOf(12345L), doubleInput.toBigInteger())

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber.toBigInteger()
        }
    }

    @Test
    fun toBigIntegerExactTest() {
        // Test with exact BigInteger input
        val bigIntegerInput: Number = BigInteger("123456")
        assertEquals(BigInteger("123456"), bigIntegerInput.toBigIntegerExact())

        // Test with exact BigDecimal input
        val bigDecimalInput: Number = BigDecimal("123")
        assertEquals(BigInteger("123"), bigDecimalInput.toBigIntegerExact())

        // Test with non-whole number BigDecimal input
        val bigDecimalNonExactInput: Number = BigDecimal("123.456")
        assertThrows<ArithmeticException> {
            bigDecimalNonExactInput.toBigIntegerExact()
        }

        // Test with exact Long value
        val longInput: Number = 123456789L
        assertEquals(BigInteger.valueOf(123456789L), longInput.toBigIntegerExact())

        // Test with non-whole Double input
        val doubleNonExactInput: Number = 12345.6789
        assertThrows<ArithmeticException> {
            doubleNonExactInput.toBigIntegerExact()
        }

        // Test with exact Double input
        val doubleExactInput: Number = 123.0
        assertEquals(BigInteger.valueOf(123L), doubleExactInput.toBigIntegerExact())

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber.toBigIntegerExact()
        }
    }

    @Test
    fun isZeroTest() {
        // Test with zero values for various supported numeric types
        assertTrue(BigDecimal.ZERO.isZero())
        assertTrue(BigInteger.ZERO.isZero())
        assertTrue(0L.isZero())
        assertTrue(0.isZero())
        assertTrue(0.toShort().isZero())
        assertTrue(0.toByte().isZero())
        assertTrue(0.0.isZero())
        assertTrue(0.0f.isZero())

        // Test with non-zero values
        assertFalse(1.isZero())
        assertFalse((-1).isZero())
        assertFalse(0.1.isZero())
        assertFalse((-0.1).isZero())
        assertFalse(BigDecimal("0.0001").isZero())
        assertFalse(BigInteger.ONE.isZero())

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber.isZero()
        }
    }

    @Test
    fun compareToTest() {
        // BigDecimal comparisons
        assertTrue(BigDecimal("1.00") == (BigDecimal("1.000")))
        assertTrue(BigDecimal("1.00") > 2)
        assertTrue(BigDecimal("2.00") > 1)

        // BigInteger comparisons
        assertTrue(BigInteger("1").compareTo(BigInteger("1")) == 0)
        assertTrue(BigInteger("1") < BigInteger("2"))
        assertTrue(BigInteger("2") > BigInteger("1"))

        // Mixed type comparisons
        assertTrue(BigDecimal("1.0").compareTo(1) == 0)
        assertTrue(BigInteger("2") > 1.0)

        // Tolerance testing
        val closeValue1: Number = 1.000000000000001
        val closeValue2: Number = 1.0
        assertEquals(0, closeValue1.compareTo(closeValue2)) // Within ABSOLUTE_TOLERANCE
        val notCloseValue: Number = 1.1
        assertTrue(closeValue2 < notCloseValue) // Outside RELATIVE_TOLERANCE
    }

    @Test
    fun unaryMinusTest() {
        // Test with BigDecimal
        val bigDecimal: Number = BigDecimal("123.45")
        assertEquals(BigDecimal("-123.45"), (-bigDecimal).toBigDecimal())

        // Test with BigInteger
        val bigInteger: Number = BigInteger("123456")
        assertEquals(BigInteger("-123456"), (-bigInteger).toBigInteger())

        // Test with Long
        val longNumber: Number = 123456789L
        assertEquals(-123456789L, (-longNumber).toLong())

        // Test with Int
        val intNumber: Number = 12345
        assertEquals(-12345, (-intNumber).toInt())

        // Test with Short
        val shortNumber: Number = 123.toShort()
        assertEquals((-123).toShort(), (-shortNumber).toShort())

        // Test with Byte
        val byteNumber: Number = 123.toByte()
        assertEquals((-123).toByte(), (-byteNumber).toByte())

        // Test with Double
        val doubleNumber: Number = 123.45
        assertEquals(-123.45, (-doubleNumber).toDouble())

        // Test with Float
        val floatNumber: Number = 123.45f
        assertEquals(-123.45f, (-floatNumber).toFloat())

        // Test with unsupported type
        assertEquals(0.0, (-unsupportedNumber).toDouble(), 0.0)
    }

    @Test
    fun plusOperatorTest() {
        // Test with BigDecimal
        val bigDecimal1: Number = BigDecimal("123.45")
        val bigDecimal2: Number = BigDecimal("876.55")
        val bigDecimalSum = bigDecimal1 + bigDecimal2
        assertEquals(1_000, bigDecimalSum)
        assertTrue(bigDecimalSum is Int)

        // Test with BigInteger
        val bigInteger1: Number = BigInteger("123456")
        val bigInteger2: Number = BigInteger("654321")
        val bigIntegerSum = bigInteger1 + bigInteger2
        assertEquals(777_777, bigIntegerSum)
        assertTrue(bigIntegerSum is Int)

        // Test with Long
        val longNumber1: Number = 123456789L
        val longNumber2: Number = 987654321L
        assertEquals(1111111110L, (longNumber1 + longNumber2).toLong())

        // Test with Int
        val intNumber1: Number = 12345
        val intNumber2: Number = 67890
        assertEquals(80235, (intNumber1 + intNumber2).toInt())

        // Test with Double
        val doubleNumber1: Number = 123.45
        val doubleNumber2: Number = 876.55
        assertEquals(1000.0, (doubleNumber1 + doubleNumber2).toDouble(), 0.0)

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber + 1
        }
    }

    @Test
    fun minusOperatorTest() {
        // Test with BigDecimal
        val bigDecimal1: Number = BigDecimal("876.55")
        val bigDecimal2: Number = BigDecimal("1000.0")
        val bigDecimalDifference: Number = bigDecimal2 - bigDecimal1
        assertEquals(123.45, bigDecimalDifference)
        println(bigDecimalDifference.javaClass.simpleName)
        assertTrue(bigDecimalDifference is Double) // Fail

        // Test with BigInteger
        val bigInteger1: Number = BigInteger("123456")
        val bigInteger2: Number = BigInteger("654321")
        val bigIntegerDifference = bigInteger2 - bigInteger1
        assertEquals(530_865, bigIntegerDifference)
        assertTrue(bigIntegerDifference is Int)

        // Test with Long
        val longNumber1: Number = 123456789L
        val longNumber2: Number = 987654321L
        val longNumberDifference = longNumber2 - longNumber1
        assertEquals(864197532, longNumberDifference)
        assertTrue(longNumberDifference is Int)

        // Test with Int
        val intNumber1: Number = 12345
        val intNumber2: Number = 67890
        val intNumberDifference = intNumber2 - intNumber1
        assertEquals(55_545, intNumberDifference)
        assertTrue(intNumberDifference is Int)

        // Test with Double
        val doubleNumber1: Number = 123.45
        val doubleNumber2: Number = 876.55
        val doubleNumberDifference = doubleNumber2 - doubleNumber1
        assertEquals(753.1, doubleNumberDifference)
        assertTrue(doubleNumberDifference is Double)

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber + 1
        }
    }

    @Test
    fun timesOperatorTest() {
        // Test with BigDecimal
        val bigDecimal1: Number = BigDecimal("123.45")
        val bigDecimal2: Number = BigDecimal("2.0")
        val bigDecimalProduct: Number = bigDecimal1 * bigDecimal2
        assertEquals(246.90, bigDecimalProduct)
        assertTrue(bigDecimalProduct is Double)

        // Test with BigInteger
        val bigInteger1: Number = BigInteger("123456")
        val bigInteger2: Number = BigInteger("2")
        val bigIntegerProduct = bigInteger1 * bigInteger2
        assertEquals(246912, bigIntegerProduct)
        assertTrue(bigIntegerProduct is Int)

        // Test with Long
        val longNumber1: Number = 123456789L
        val longNumber2: Number = 2L
        assertEquals(246913578L, (longNumber1 * longNumber2).toLong())

        // Test with Int
        val intNumber1: Number = 12345
        val intNumber2: Number = 2
        assertEquals(24690, (intNumber1 * intNumber2).toInt())

        // Test with Double
        val doubleNumber1: Number = 123.45
        val doubleNumber2: Number = 2.0
        assertEquals(246.9, (doubleNumber1 * doubleNumber2).toDouble(), 0.0)

        // Test with Float
        val floatNumber1: Number = 12.34f
        val floatNumber2: Number = 2.0f
        assertEquals(24.68f, (floatNumber1 * floatNumber2).toFloat(), 0.0f)

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber * 2
        }
    }

    @Test
    fun divOperatorTest() {
        // Test with BigDecimal
        val bigDecimal1: Number = BigDecimal("246.90")
        val bigDecimal2: Number = BigDecimal("2.0")
        val bigDecimalQuotient = bigDecimal1 / bigDecimal2
        assertEquals(123.45, bigDecimalQuotient)
        assertTrue(bigDecimalQuotient is Double)

        // Test with BigInteger
        val bigInteger1: Number = BigInteger("246912")
        val bigInteger2: Number = BigInteger("2")
        val bigIntegerQuotient = bigInteger1 / bigInteger2
        assertEquals(123456, bigIntegerQuotient)
        assertTrue(bigIntegerQuotient is Int)

        // Test with Long
        val longNumber1: Number = 246913578L
        val longNumber2: Number = 2L
        val longQuotient = longNumber1 / longNumber2
        assertEquals(123456789, longQuotient)
        assertTrue(longQuotient is Int)

        // Test with Int
        val intNumber1: Number = 24690
        val intNumber2: Number = 2
        val intQuotient = intNumber1 / intNumber2
        assertEquals(12345, intQuotient)
        assertTrue(intQuotient is Int)

        // Test with Double
        val doubleNumber1: Number = 246.9
        val doubleNumber2: Number = 2.0
        val doubleQuotient = doubleNumber1 / doubleNumber2
        assertEquals(123.45, doubleQuotient)
        assertTrue(doubleQuotient is Double)

        // Test with Float
        val floatNumber1: Number = 24.68f
        val floatNumber2: Number = 2.0f
        val floatQuotient = floatNumber1 / floatNumber2
        assertEquals(12.34, floatQuotient)
        assertTrue(floatQuotient is Double)

        // Test division by zero
        val decimalZero: Number = BigDecimal.ZERO
        assertThrows<IllegalArgumentException> { BigDecimal("123.45") / decimalZero }
        assertThrows<IllegalArgumentException> { (123 as Number) / (0 as Number) }

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber / 2
        }
    }

    @Test
    fun remOperatorTest() {
        // Test with BigDecimal
        val bigDecimal1: Number = BigDecimal("246.90")
        val bigDecimal2: Number = BigDecimal("2.0")
        val bigDecimalRemainder: Number = bigDecimal1 % bigDecimal2
        assertEquals(0.9, bigDecimalRemainder.toDouble(), 0.0)

        // Test with BigInteger
        val bigInteger1: Number = BigInteger("246")
        val bigInteger2: Number = BigInteger("5")
        val bigIntegerRemainder = bigInteger1 % bigInteger2
        assertEquals(1, bigIntegerRemainder)
        assertTrue(bigIntegerRemainder is Int)

        // Test with Long
        val longNumber1: Number = 12345L
        val longNumber2: Number = 4L
        assertEquals(1L, (longNumber1 % longNumber2).toLong())

        // Test with Int
        val intNumber1: Number = 14
        val intNumber2: Number = 5
        assertEquals(4, (intNumber1 % intNumber2).toInt())

        // Test with Double
        val doubleNumber1: Number = 123.456
        val doubleNumber2: Number = 10.0
        assertEquals(3.456, (doubleNumber1 % doubleNumber2).toDouble(), 0.0)

        // Test with Float
        val floatNumber1: Number = 12.34f
        val floatNumber2: Number = 5.0f
        assertEquals(2.34f, (floatNumber1 % floatNumber2).toFloat(), 1E-6f)

        // Test division by zero
        val decimalZero: Number = BigDecimal.ZERO
        assertThrows<IllegalArgumentException> { BigDecimal("123.45") % decimalZero }
        assertThrows<IllegalArgumentException> { (123 as Number) % (0 as Number) }

        // Test with unsupported type
        assertThrows<IllegalArgumentException> {
            unsupportedNumber % 2
        }
    }

    @Test
    fun powFunctionTest() {
        // Positive integer base with positive integer exponent
        assertEquals(8, 2.pow(3))
        assertEquals(1, 2.pow(0))
        assertEquals(1, 0.pow(0)) // By convention
        assertEquals(0, 0.pow(2))

        // Negative integer base with positive integer exponent
        assertEquals(-8, (-2).pow(3))
        assertEquals(1, (-2).pow(0))

        // Positive integer base with negative integer exponent
        assertEquals(0.25, 2.pow(-2))

        // Negative integer base with negative integer exponent
        assertEquals(-0.125, (-2).pow(-3))

        // Double base with double exponent
        assertEquals(27, 3.0.pow(3.0))
        assertEquals(0.25, 2.0.pow(-2.0))
        assertEquals(1, 5.0.pow(0.0))

        // BigInteger base with BigInteger exponent
        assertEquals(BigInteger("125"), BigInteger("5").pow(3))
        assertEquals(BigInteger.ONE, BigInteger("5").pow(0))
        assertEquals(BigInteger("85070591730234615847396907784232501249"), Long.MAX_VALUE.pow(2))
        assertTrue(Long.MAX_VALUE.pow(2) is BigInteger)
        assertEquals(BigInteger("85070591730234615865843651857942052864"), BigInteger("9223372036854775808").pow(2))

        // BigDecimal base with BigDecimal exponent
        assertEquals(BigDecimal("8"), BigDecimal("2").pow(3))
        assertEquals(BigDecimal("1"), BigDecimal("2").pow(0))

        assertEquals(BigDecimal("1E618"), BigDecimal("1E309").pow(2))

        // Unsupported number type
        assertEquals(0, unsupportedNumber.pow(2))
    }

    @Test
    fun `square root with positive integers`() {
        // square root with positive integers
        assertEquals(3, 9.root(2))
        assertEquals(4, 16.root(2))

        // cube root with positive integers
        assertEquals(2, 8.root(3))
        assertEquals(3, 27.root(3))

        // cube root with negative integers
        assertEquals(-2, (-8).root(3))
        assertEquals(-3, (-27).root(3))

        // square root with floating-point numbers
        assertEquals(1.732, 3.root(2).toDouble(), 1E-3)
        assertEquals(2.236, 5.root(2).toDouble(), 1E-3)

        // root with BigDecimal numbers
        assertEquals(2, BigDecimal("4.0").root(2))
        assertEquals(2, BigDecimal("8.0").root(3))

        // invalid root
        assertThrows<IllegalArgumentException> {
            8.root(0)
        }

        // Test even root of a negative number
        assertThrows<IllegalArgumentException> {
            (-16).root(2)
        }
    }


    @Test
    fun `isolated test for BigDecimal power and root`() {
        val num: Number = Double.MAX_VALUE // 1.7976931348623157E308
        val pow: Number = 2.0
        val numPow: Number = num.pow(pow)

        println("exp(log($num) × $pow) = ${exp(log(num.toBigDecimal()) * pow.toBigDecimal())}")
        println("($num)^($pow) = $numPow")
        assertEquals(exp(log(num.toBigDecimal()) * pow.toBigDecimal()), numPow) // Not equal
        assertTrue(Double.MAX_VALUE.pow(2) is BigInteger) // True
    }
}