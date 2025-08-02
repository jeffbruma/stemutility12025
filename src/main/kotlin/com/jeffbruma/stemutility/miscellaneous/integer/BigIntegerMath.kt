package com.jeffbruma.stemutility.miscellaneous.integer

import com.jeffbruma.stemutility.miscellaneous.SecureKotlinRandom
import com.jeffbruma.stemutility.miscellaneous.decimal.*
import com.jeffbruma.stemutility.miscellaneous.toBigInteger
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

const val FACTORIAL_CAP = 65_535

private val factorialCache = ConcurrentHashMap<Int, BigInteger>().apply {
    this[0] = BigInteger.ONE
    this[1] = BigInteger.ONE
}

operator fun BigInteger.unaryMinus(): BigInteger = this.negate()

operator fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)

operator fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)

operator fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)

operator fun BigInteger.div(other: BigInteger): BigInteger = this.divide(other)

operator fun BigInteger.rem(other: BigInteger): BigInteger = this.remainder(other)

fun BigInteger.isNegative() = this.signum() < 0

fun BigInteger.isZeroOrLesser() = this.signum() <= 0

fun BigInteger.isZero() = this.compareTo(BigInteger.ZERO) == 0

fun BigInteger.isNotZero() = this.compareTo(BigInteger.ZERO) != 0

fun BigInteger.isZeroOrGreater() = this.signum() >= 0

fun BigInteger.isOne(): Boolean = this.compareTo(BigInteger.ONE) == 0

fun BigInteger.isPositive(): Boolean = this.signum() > 0

fun BigInteger.isEven(): Boolean = this.and(BigInteger.ONE) == BigInteger.ZERO

fun BigInteger.isOdd(): Boolean = this.and(BigInteger.ONE) == BigInteger.ONE


fun BigInteger.pow(exponent: BigInteger): BigInteger {
    require(exponent.isZeroOrGreater()) {
        "Negative exponents yield fractional results, which truncate to 0 in integer division."
    }

    if (exponent.isZero()) return BigInteger.ONE
    if (exponent.isOne()) return this

    val halfExponent = exponent / BigInteger.TWO
    val halfPow = this.pow(halfExponent)

    return if (exponent % BigInteger.TWO == BigInteger.ZERO) {
        halfPow * halfPow
    } else {
        this * halfPow * halfPow
    }
}

fun BigInteger.root(degree: Int): BigInteger = BigDecimal(this).root(degree).toBigInteger()

fun abs(a: BigInteger): BigInteger = a.abs()

fun max(a: BigInteger, b: BigInteger): BigInteger = if (a >= b) a else b

fun min(a: BigInteger, b: BigInteger): BigInteger = if (a <= b) a else b

fun gcf(a: BigInteger, b: BigInteger): BigInteger = when {
    a == BigInteger.ZERO -> b
    b == BigInteger.ZERO -> a
    else -> gcf(b, a % b)
}

fun lcm(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b).divide(gcf(a, b))

fun sqrt(n: BigInteger): BigInteger = n.sqrt()

// Exponential and Logarithm
fun exp(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return exp(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun log(n: BigInteger): BigInteger {
    if (n < BigInteger.ONE) throw ArithmeticException("log(n) is undefined for n < 1")
    return log(BigDecimal(n), rounding = false).toBigInteger()
}

fun log(n: BigInteger, base: BigInteger): BigInteger = log(BigDecimal(n), BigDecimal(base), rounding = false).toBigInteger()

// Hyperbolic Functions
fun sinh(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return sinh(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun cosh(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return cosh(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun tanh(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return tanh(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun csch(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return csch(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun sech(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return sech(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun coth(n: BigInteger): BigInteger {
    val context = determineMathContext(n)
    return coth(BigDecimal(n), context, rounding = false).toBigInteger()
}

fun arsinh(n: BigInteger): BigInteger = arsinh(BigDecimal(n), rounding = false).toBigInteger()

fun arcosh(n: BigInteger): BigInteger = arcosh(BigDecimal(n),  rounding = false).toBigInteger()

fun artanh(n: BigInteger): BigInteger = artanh(BigDecimal(n), rounding = false).toBigInteger()

fun arcsch(n: BigInteger): BigInteger = arcsch(BigDecimal(n), rounding = false).toBigInteger()

fun arsech(n: BigInteger): BigInteger = arsech(BigDecimal(n), rounding = false).toBigInteger()

fun arcoth(n: BigInteger): BigInteger = arcoth(BigDecimal(n), rounding = false).toBigInteger()

// Trigonometric Functions
fun sin(n: BigInteger): BigInteger = sin(BigDecimal(n), rounding = false).toBigInteger()

fun cos(n: BigInteger): BigInteger = cos(BigDecimal(n), rounding = false).toBigInteger()

fun tan(n: BigInteger): BigInteger = tan(BigDecimal(n), rounding = false).toBigInteger()

fun csc(n: BigInteger): BigInteger = csc(BigDecimal(n), rounding = false).toBigInteger()

fun sec(n: BigInteger): BigInteger = sec(BigDecimal(n), rounding = false).toBigInteger()

fun cot(n: BigInteger): BigInteger = cot(BigDecimal(n), rounding = false).toBigInteger()

fun asin(n: BigInteger): BigInteger = asin(BigDecimal(n), rounding = false).toBigInteger()

fun acos(n: BigInteger): BigInteger = acos(BigDecimal(n), rounding = false).toBigInteger()

fun atan(n: BigInteger): BigInteger = atan(BigDecimal(n), rounding = false).toBigInteger()

fun atan2(a: BigInteger, b: BigInteger): BigInteger = atan2(BigDecimal(a), BigDecimal(b), rounding = false).toBigInteger()

fun acsc(n: BigInteger): BigInteger = acsc(BigDecimal(n), rounding = false).toBigInteger()

fun asec(n: BigInteger): BigInteger = asec(BigDecimal(n), rounding = false).toBigInteger()

fun acot(n: BigInteger): BigInteger = acot(BigDecimal(n), rounding = false).toBigInteger()

fun acot2(a: BigInteger, b: BigInteger): BigInteger = acot2(BigDecimal(a), BigDecimal(b), rounding = false).toBigInteger()

fun random(
    x: BigInteger?,
    y: BigInteger?,
    random: Random = SecureKotlinRandom.Companion.Instance
): BigInteger {
    // Synthetic bounding: if both are null, fallback to a symmetric range
    val defaultBitLength = 128

    val min = x ?: BigInteger.ONE.negate().shiftLeft(defaultBitLength - 1)
    val max = y ?: BigInteger.ONE.shiftLeft(defaultBitLength - 1).subtract(BigInteger.ONE)

    require(min <= max) { "Invalid bounds: min must be <= max" }

    val (actualMin, actualMax) = if (min <= max) min to max else max to min

    require(actualMin <= actualMax) { "No valid values to randomize after inclusivity adjustment" }

    val difference = actualMax - actualMin

    // Fast path: only one value possible
    if (difference == BigInteger.ZERO) return actualMin

    // Generate a uniformly distributed value in [0, difference]
    val bitLength = difference.bitLength()
    val byteLength = (bitLength + 7) / 8
    val buffer = ByteArray(byteLength)

    var result: BigInteger
    do {
        random.nextBytes(buffer)

        // Mask excess high bits
        val excessBits = byteLength * 8 - bitLength
        buffer[0] = (buffer[0].toInt() and (0xFF ushr excessBits)).toByte()

        result = BigInteger(1, buffer)
    } while (result > difference)

    return actualMin + result
}

fun factorialBI(n: Int): BigInteger {
    require(n in 0..FACTORIAL_CAP) { "Factorial input must be between 0 and $FACTORIAL_CAP inclusive" }
    return factorialCache.computeIfAbsent(n) {
        (2..it).fold(BigInteger.ONE) { acc, i -> acc * i.toBigInteger() }
    }
}

private fun determineMathContext(n: BigInteger): MathContext {
    val maxSafePrecision = 1_000_000  // Tune this as you see fit
    val x = BigDecimal(n.abs())
    val log10e = BigDecimal("0.43429448190325182765")

    val estimatedPrecision = try {
        x.multiply(log10e)
            .setScale(0, RoundingMode.CEILING)
            .toBigInteger()
            .min(BigInteger.valueOf(maxSafePrecision.toLong()))
            .toInt() + 20
    } catch (_: ArithmeticException) {
        // Fallback to max safe precision if even .toInt() fails
        maxSafePrecision
    }

    return MathContext(estimatedPrecision, RoundingMode.HALF_EVEN)
}