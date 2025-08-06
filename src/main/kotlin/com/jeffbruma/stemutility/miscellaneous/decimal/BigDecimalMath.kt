package com.jeffbruma.stemutility.miscellaneous.decimal

import com.jeffbruma.stemutility.E
import com.jeffbruma.stemutility.LOG_2
import com.jeffbruma.stemutility.PI
import com.jeffbruma.stemutility.SQRT_2
import com.jeffbruma.stemutility.miscellaneous.SecureKotlinRandom
import com.jeffbruma.stemutility.miscellaneous.integer.*
import com.jeffbruma.stemutility.miscellaneous.isZero
import com.jeffbruma.stemutility.miscellaneous.toBigDecimal
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.measureTimeMillis

const val MAX_ROOT_DEGREE: Int = 100
const val MAX_EXPONENT: Int = 1_000
const val MAX_PRECISION = 1_024

val mathCtx = MathContext(32, RoundingMode.HALF_UP)

val BigDecimal.squared : BigDecimal
    get() = this.multiply(this)

val BigDecimal.cubed : BigDecimal
    get() = this.multiply(this).multiply(this)

val BigDecimal.sign : BigDecimal
    get() = this.signum().toBigDecimal()

// Predicate
fun BigDecimal.isInteger() : Boolean = this.stripTrailingZeros().scale() <= 0

fun BigDecimal.isNegative() = this.signum() < 0

fun BigDecimal.isNegativeInteger() = this.isInteger() && this.isNegative()

fun BigDecimal.isNonPositive() = this.signum() <= 0

fun BigDecimal.isZero() = this.compareTo(BigDecimal.ZERO) == 0

fun BigDecimal.isNotZero() = this.compareTo(BigDecimal.ZERO) != 0

fun BigDecimal.isNonNegative() = this.signum() >= 0

fun BigDecimal.isOne() = this.compareTo(BigDecimal.ONE) == 0

fun BigDecimal.isPositive() = this.signum() > 0

fun BigDecimal.isPositiveInteger() = this.isInteger() && this.isPositive()

fun BigDecimal.isEven() : Boolean = this.isInteger() && this.toBigIntegerExact().and(BigInteger.ONE) == BigInteger.ZERO

fun BigDecimal.isOdd() : Boolean = this.isInteger() && this.toBigIntegerExact().and(BigInteger.ONE) == BigInteger.ONE


// Arithmetic
operator fun BigDecimal.unaryMinus() : BigDecimal = this.negate()

operator fun BigDecimal.plus(other: BigDecimal) : BigDecimal = this.add(other, mathCtx)

operator fun BigDecimal.minus(other: BigDecimal) : BigDecimal = this.subtract(other, mathCtx)

operator fun BigDecimal.times(other: BigDecimal) : BigDecimal = this.multiply(other, mathCtx)

operator fun BigDecimal.div(other: BigDecimal) : BigDecimal = this.divide(other, mathCtx)

operator fun BigDecimal.rem(other: BigDecimal) : BigDecimal = this.remainder(other, mathCtx)

fun abs(x: BigDecimal): BigDecimal = x.abs()

fun max(a: BigDecimal, b: BigDecimal) : BigDecimal = if (a >= b) a else b

fun min(a: BigDecimal, b: BigDecimal) : BigDecimal = if (a <= b) a else b

fun BigDecimal.ceil(): BigDecimal = this.setScale(0, RoundingMode.CEILING)

fun BigDecimal.floor(): BigDecimal = this.setScale(0, RoundingMode.FLOOR)

fun BigDecimal.nextUp() : BigDecimal {
    val step = BigDecimal.ONE.scaleByPowerOfTen(-this.scale())
    return this + step
}

fun BigDecimal.nextDown() : BigDecimal {
    val step = BigDecimal.ONE.scaleByPowerOfTen(-this.scale())
    return this - step
}

fun BigDecimal.normalized(): BigDecimal = stripTrailingZeros().run { if (scale() < 0) setScale(0) else this }

fun BigDecimal.reciprocal(mathContext: MathContext = mathCtx): BigDecimal {
    if (this.isZero()) throw ArithmeticException("Cannot take reciprocal of zero")
    return BigDecimal.ONE.divide(this, mathContext)
}

private fun BigDecimal.powInt(n: BigInteger, mathContext: MathContext): BigDecimal {
    var result = BigDecimal.ONE
    var base = this
    var power = n

    while (power > BigInteger.ZERO) {
        if (power.testBit(0)) result = result.multiply(base, mathContext)
        base = base.multiply(base, mathContext)
        power = power.shiftRight(1)
    }

    return result
}

/**
 * Raises this [BigDecimal] to a real [x] using high-precision arithmetic.
 *
 * If [x] is an integer, this uses fast exponentiation (by squaring).
 * If [x] is non-integer, it uses the identity:
 * ```
 * x^y = exp(y * log(x))
 * ```
 * Negative exponents are supported.
 * Negative bases with non-integer exponents are rejected unless the exponent is rational with an odd denominator.
 *
 * Special cases:
 * - `x^0 = 1` for any x
 * - `0^y = 0` for positive y, error for negative y
 * - `1^y = 1` for any y
 * - `x^1 = x`
 * - `x^2 = x.squared` optimization
 *
 * @param x the exponent.
 * @param mathContext precision and rounding mode.
 * @param rounding whether to round the final result.
 * @return result of this raised to the power [x].
 * @throws ArithmeticException if the result is undefined in ℝ (e.g. negative base and invalid exponent).
 */
fun BigDecimal.pow(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {

    if (x == BigDecimal(3)) return if (rounding) this.cubed.roundAndStrip(mathContext) else this.cubed
    if (x == BigDecimal(2)) return if (rounding) this.squared.roundAndStrip(mathContext) else this.squared
    if (x.isOne()) return this
    if (x == BigDecimal(2).reciprocal(mathContext)) return squareRoot(this, mathContext, rounding)
    if (x == BigDecimal(3).reciprocal(mathContext)) return cubeRoot(this, mathContext, rounding)
    if (x.isZero() || this.isOne()) return BigDecimal.ONE
    if (this.isZero()) {
        if (x.isZero()) throw ArithmeticException("Zero cannot be raised to zero")
        if (x.isNegative()) throw ArithmeticException("Zero cannot be raised to a negative power")
        return BigDecimal.ZERO
    }

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)

    // Integer exponent
    if (x.isInteger()) {
        val expInt = x.toBigInteger()
        val base = if (expInt.isNegative()) this.reciprocal(internalMathContext) else this
        val expAbs = abs(expInt)
        val result = base.powInt(expAbs, internalMathContext)
        return if (rounding) result.roundAndStrip(mathContext) else result
    }

    // Rational exponent: x = numerator / denominator
    rationalize(x, mathContext).let { (numerator, denominator) ->
        if (numerator <= BigInteger.valueOf(MAX_EXPONENT.toLong()) &&
            denominator <= BigInteger.valueOf(MAX_ROOT_DEGREE.toLong())) {

            if (this.isNegative() && denominator.isEven())
                throw ArithmeticException("Even root of negative base is undefined in ℝ")

            val rooted = this.root(denominator.toInt(), internalMathContext, rounding = false)
            val result = rooted.powInt(numerator, internalMathContext)

            return if (rounding) result.roundAndStrip(mathContext) else result
        }
    }

    if (this.isNegative())
        throw ArithmeticException("Irrational power of negative base is undefined in ℝ")

    val logBase = log(this, internalMathContext, rounding = false)
    val result = exp(logBase.multiply(x, internalMathContext), internalMathContext, rounding = false)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun rationalize(x: BigDecimal, mathContext: MathContext, maxDurationMillis: Long = 300_000L): Pair<BigInteger, BigInteger> {
    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision - 1)
    val target = abs(x)

    var a = target.toBigInteger()
    var h1 = BigInteger.ONE
    var h0 = BigInteger.ZERO
    var k1 = BigInteger.ZERO
    var k0 = BigInteger.ONE

    var value = target
    val startTime = System.currentTimeMillis()

    while (System.currentTimeMillis() - startTime <= maxDurationMillis) {
        val h = a.multiply(h1).add(h0)
        val k = a.multiply(k1).add(k0)

        if (k.isNotZero()) {
            val approx = BigDecimal(h).divide(BigDecimal(k), internalMathContext)
            if (relativeEquals(approx, target, tolerance)) {
                return if (x.isNegative()) -h to k else h to k
            }
        }

        val remainder = value.remainder(BigDecimal.ONE)

        if (abs(remainder) <= tolerance) break

        value = remainder.reciprocal(internalMathContext)
        a = value.toBigInteger()


        h0 = h1
        h1 = h
        k0 = k1
        k1 = k
    }
    throw ArithmeticException("Failed to rationalize $x within time limit")
}

fun BigDecimal.root(degree: Int, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (degree <= 0) throw ArithmeticException("Degree must be positive")
    if (this.isNegative() && degree % 2 == 0) throw ArithmeticException("Even root of negative base is undefined in ℝ")
    if (this.isZero()) return BigDecimal.ZERO
    if (this.isOne()) return BigDecimal.ONE

    val maxIterations = 50

    val n = BigDecimal(degree)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(mc.precision)

    // Initial guess: x^(1/n) ≈ exp(log(x)/n) is fast but forbidden (depends on exp/log)
    // So use a cruder guess: x / n or 10^(log10(x)/n) if x > 1
    var x = if (this > BigDecimal.ONE) {
        val log10 = this.precision() - this.scale() - 1
        BigDecimal.TEN.pow(log10 / degree)
    } else {
        this.divide(n, mc)
    }

    val nMinus1 = n - BigDecimal.ONE

    repeat(maxIterations) {
        val xPrev = x

        // x = (1/n) * ((n - 1) * x + a / x^(n - 1))
        val xToPower = x.pow(degree - 1, mc)
        if (xToPower.compareTo(BigDecimal.ZERO) == 0)
            throw ArithmeticException("Division by zero during root iteration")

        val numerator = this.divide(xToPower, mc).add(x.multiply(nMinus1, mc), mc)
        x = numerator.divide(n, mc)

        if (x.subtract(xPrev).abs() <= tolerance) return if (rounding) x.roundAndStrip(mathContext) else x
    }

    throw ArithmeticException("Root($degree) did not converge after $maxIterations iterations")
}

fun squareRoot(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isNegative()) throw ArithmeticException("Even root of negative base is undefined in ℝ")

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)
    val result = x.sqrt(internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun cubeRoot(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)
    val result = when {
        x.isZero() -> BigDecimal.ZERO
        x.isOne() -> BigDecimal.ONE
        x.isNegative() -> -cubeRoot(-x, mathContext, rounding)
        else -> exp(log(x, internalMathContext, rounding = false).divide(BigDecimal(3), internalMathContext), internalMathContext, rounding = false)
    }

    return if (rounding) result.roundAndStrip(mathContext) else result
}

// Exponential and Logarithm
fun exp(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal    {
    require(mathContext != MathContext.UNLIMITED) { "Invalid MathContext" }

    val internalPrecision = (mathContext.precision * 1.34375).toInt()
    val internalMathContext = MathContext(internalPrecision, mathContext.roundingMode)

    if (x.isZero()) return BigDecimal.ONE
    if (x.isOne()) return getConstant("e", mathContext.precision)
    if (x.isNegative()) {
        val result = exp(-x, internalMathContext, false).reciprocal(mathContext)
        return if (rounding) result.roundAndStrip(mathContext) else result
    }

    var k = 0
    var reducedX = x

    while (abs(reducedX) > BigDecimal("0.1")) {
        reducedX = reducedX.divide(BigDecimal.TWO)
        k++
    }

    val base = recurrentPowerSeriesCore(
        argumentTerm = reducedX,
        firstTerm = BigDecimal.ONE,
        indexFunction = { n -> BigDecimal.valueOf(n.toLong()).reciprocal(internalMathContext) },
        mathContext = internalMathContext
    )

    var result = base
    repeat(k) {
        result = result.multiply(result, internalMathContext)
    }

    return if (rounding) result.roundAndStrip(mathContext) else result
}

@Suppress("Unused")
fun logNew(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (x.isNonPositive()) throw ArithmeticException("log(x) is undefined for x <= 0")
    if (x.isOne()) return BigDecimal.ZERO

    if (x == BigDecimal.TWO) return getConstant("log_2", mathContext.precision)

    val useAGM = mathContext.precision >= 32 || x.scale() <= -50 || x.precision() >= 50
    val near1 = abs(x.subtract(BigDecimal.ONE)) < BigDecimal("0.01")

    return when {
        useAGM -> logAGM(x, mathContext, rounding)
        near1 -> log1pTaylor(x.subtract(BigDecimal.ONE), mathContext, rounding)
        else -> log(x, mathContext, rounding)
    }
}

// logAtanh
fun log(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isNonPositive()) throw ArithmeticException("log(x) is undefined for x <= 0")
    if (x.isOne()) return BigDecimal.ZERO
    if (x == BigDecimal.TWO) return LOG_2.round(mathContext)

    val precision = max(x.precision(), mathContext.precision) + 2
    val internalMathContext = MathContext(precision, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision)

    val ln2 = getConstant("ln2", precision)

    // Reduce x into [1, 2)
    var n = 0
    var z = x


    while (z >= BigDecimal.TWO) {
        z = z.divide(BigDecimal.TWO, internalMathContext)
        n++
    }

    while (z < BigDecimal.ONE) {
        z = z.multiply(BigDecimal.TWO, internalMathContext)
        n--
    }

    // Transform to atanh-style series input
    val y = (z.subtract(BigDecimal.ONE)).divide(z.add(BigDecimal.ONE), internalMathContext)
    val y2 = y.multiply(y)

    var term = y
    var log = term
    var k = 1

    while (abs(term) > tolerance) {
        k += 2
        term = term.multiply(y2)
        log = log.add(term.divide(BigDecimal(k), internalMathContext))
    }

    log = log.multiply(BigDecimal.TWO)

    if (n != 0) {
        log = log.add(ln2.multiply(BigDecimal.valueOf(n.toLong()), internalMathContext))
    }

    // Final adjustment
    val result = log

    return if (rounding) result.roundAndStrip(mathContext) else result
}

private fun logAGM(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (x.isNonPositive()) throw ArithmeticException("log(x) is undefined for x <= 0")
    if (x.isOne()) return BigDecimal.ZERO

    val internalMathContext = MathContext(mathContext.precision + 5, mathContext.roundingMode)
    val ln2 = getConstant("ln2", internalMathContext.precision + 2)
    val pi = getConstant("pi", internalMathContext.precision + 2)

    // Reduce x into [1, 2)
    var z = x
    var k = 0
    while (z >= BigDecimal.TWO) {
        z = z.divide(BigDecimal.TWO, internalMathContext)
        k++
    }
    while (z < BigDecimal.ONE) {
        z = z.multiply(BigDecimal.TWO, internalMathContext)
        k--
    }

    // AGM part
    val four = BigDecimal.valueOf(4)
    val invZ = four.divide(z, internalMathContext)
    val agm = agm(BigDecimal.ONE, invZ, internalMathContext, rounding = false)

    val logZ = pi.divide(agm.multiply(BigDecimal.TWO, internalMathContext), internalMathContext)
    val scaledLn2 = ln2.multiply(BigDecimal.valueOf(k.toLong()), internalMathContext)

    val result = logZ.add(scaledLn2)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

private fun log1pTaylor(x: BigDecimal, mathContext: MathContext, rounding: Boolean = true): BigDecimal {
    if (x <= BigDecimal.valueOf(-1)) throw ArithmeticException("log1p(x) undefined for x ≤ -1")

    val internalCtx = MathContext(mathContext.precision + 5, mathContext.roundingMode)

    val log1p = recurrentPowerSeriesCore(
        argumentTerm = x,
        firstTerm = x,
        indexFunction = { k ->
            val denominator = BigDecimal.valueOf(k.toLong() + 1)
            if ((k and 1) == 0) denominator.reciprocal(internalCtx) else denominator.reciprocal(internalCtx).negate()
        },
        mathContext = internalCtx
    )

    return if (rounding) log1p.roundAndStrip(mathContext) else log1p
}

fun log(x: BigDecimal, base: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision + 5, mathContext.roundingMode)
    val result = log(x, internalMathContext, false).divide(log(base, internalMathContext, false), internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun logTwo(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal = log(x, BigDecimal.TWO, mathContext, rounding)

fun logTen(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal = log(x, BigDecimal.TEN, mathContext, rounding)

// Hyperbolic Functions
fun sinh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) return BigDecimal.ZERO
    if (x.isNegative()) return -sinh(-x, mathContext, rounding)

    val internalPrecision = mathContext.precision + 10
    val internalMathContext = MathContext(internalPrecision, mathContext.roundingMode)

    val expX = exp(x, internalMathContext, false)
    val result = expX.subtract(expX.reciprocal(internalMathContext), internalMathContext).divide(BigDecimal.TWO, internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun cosh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) return BigDecimal.ONE

    val internalPrecision = mathContext.precision + 10
    val internalMathContext = MathContext(internalPrecision, mathContext.roundingMode)

    val expX = exp(x, internalMathContext, false)
    val result = (expX.add(expX.reciprocal(internalMathContext), internalMathContext)).divide(BigDecimal.TWO, internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun tanh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) return BigDecimal.ZERO
    if (x.isNegative()) return -tanh(-x, mathContext, rounding)

    val internalPrecision = (mathContext.precision * 1.34375).toInt()
    val internalMathContext = MathContext(internalPrecision, mathContext.roundingMode)

    val expX = exp(x, internalMathContext, false)
    val expXReciprocal = expX.reciprocal(internalMathContext)
    val numerator = expX.subtract(expXReciprocal, internalMathContext)
    val denominator = expX.add(expXReciprocal, internalMathContext)
    val result = numerator.divide(denominator, internalMathContext)

    // this takes ~24 ms, as opposed to the current implementation, which takes ~8 ms
    //val result = sinh(x, internalMathContext, false).divide(cosh(x, internalMathContext, false), internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun csch(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) throw ArithmeticException("csch(0) is undefined")

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)

    val result = sinh(x, internalMathContext, false).reciprocal(internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun sech(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) return BigDecimal.ONE

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)

    val result = cosh(x, internalMathContext, false).reciprocal(internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun coth(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) throw ArithmeticException("coth(0) is undefined")
    if (x.isNegative()) return -coth(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)

    val result = tanh(x, internalMathContext, false).reciprocal(internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}


fun arsinh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    // arsinh(x) = ln(x + sqrt(x² + 1))

    if (x.isNegative()) return -arsinh(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 10, mathContext.roundingMode)

    val squareRootOfXSquaredPlusOne = squareRoot(x.squared.add(BigDecimal.ONE), internalMathContext, rounding = false)
    val result = log(x.add(squareRootOfXSquaredPlusOne), internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun arcosh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    // arcosh(x) = ln(x + sqrt(x² - 1))
    if (x < BigDecimal.ONE) throw ArithmeticException("The hyperbolic arccosine arcosh(x) is only defined for x >= 1")

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)

    val squareRootOfXSquaredMinusOne = squareRoot(x.squared.subtract(BigDecimal.ONE), internalMathContext, rounding = false)
    val result = log(x.add(squareRootOfXSquaredMinusOne), internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun artanh(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    // artanh(x) = ln((1 + x) / (1 - x)) / 2
    if (x <= - BigDecimal.ONE || x >= BigDecimal.ONE) throw ArithmeticException("The hyperbolic arctangent artanh(x) is only defined for -1 < x < 1")
    if (x.isNegative()) return -artanh(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)

    val numerator = BigDecimal.ONE.add(x, internalMathContext)
    val denominator = BigDecimal.ONE.subtract(x, internalMathContext)
    val ratio = numerator.divide(denominator, internalMathContext)
    val result = log(ratio, internalMathContext, false).divide(BigDecimal.TWO, internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun arcsch(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) throw ArithmeticException("acsch(x) is undefined for x = 0")
    if (x.isNegative()) return -arcsch(-x, mathContext, rounding)

    val reciprocal = x.reciprocal(mathContext)
    val result = arsinh(reciprocal, mathContext, false)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun arsech(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x <= BigDecimal.ZERO || x > BigDecimal.ONE) throw ArithmeticException("The hyperbolic arc-secant arsech(x) is only defined for 0 < x <= 1")

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)
    val reciprocal = x.reciprocal(internalMathContext)
    val result = arcosh(reciprocal, internalMathContext, false)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun arcoth(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (abs(x) <= BigDecimal.ONE) throw ArithmeticException("The hyperbolic arc-cotangent arcoth(x) is only defined for |x| > 1")
    if (x.isNegative()) return -arcoth(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)

    val reciprocal = x.reciprocal(internalMathContext)
    val result = artanh(reciprocal, internalMathContext, false)

    return if (rounding) result.roundAndStrip(mathContext) else result
}


// Trigonometric Functions

fun sin(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (x.isNegative()) return -sin(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision - 2)

    val pi = getConstant("pi", internalMathContext.precision + 2)
    val quarterPi = pi.multiply(BigDecimal(0.25))
    val halfPi = pi.multiply(BigDecimal(0.5))
    val twoPi = pi.multiply(BigDecimal.TWO)

    val baseAngle = x.remainder(twoPi, internalMathContext).apply {
        when {
            this < tolerance || relativeEquals(this, twoPi, tolerance) -> BigDecimal.ZERO
            this > pi -> this.subtract(twoPi)
            this > halfPi -> this.subtract(pi)
        }
    }
    val signBaseAngle = baseAngle.sign
    val absBaseAngle = abs(baseAngle)

    if (relativeEquals(absBaseAngle, halfPi, tolerance)) return BigDecimal.ONE.multiply(signBaseAngle)

    if (absBaseAngle.remainder(pi, mathContext) < tolerance) return BigDecimal.ZERO

    val result = if (absBaseAngle <= quarterPi) {
        sineCore(absBaseAngle, internalMathContext).multiply(signBaseAngle)
    } else {
        cosineCore(halfPi.subtract(absBaseAngle), internalMathContext).multiply(signBaseAngle)
    }
    return if (rounding) result.roundAndStrip(mathContext) else result
}

private fun sineCore(angle: BigDecimal, mathContext: MathContext): BigDecimal = recurrentPowerSeriesCore(
    argumentTerm = angle.squared,
    firstTerm = angle,
    indexFunction = { n -> -BigDecimal.valueOf(((2L * n) * (2L * n + 1L))).reciprocal(mathContext) },
    mathContext = mathContext
)

fun cos(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (x.isNegative()) return cos(-x, mathContext, rounding)

    println("x = $x")

    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision - 3)

    val pi = getConstant("pi", internalMathContext.precision + 2)
    val quarterPi = pi.multiply(BigDecimal(0.25))
    val halfPi = pi.multiply(BigDecimal(0.5))
    val threeHalvesPi = pi.multiply(BigDecimal(1.5))
    val twoPi = pi.multiply(BigDecimal.TWO)

    val baseAngle = x.remainder(twoPi, internalMathContext).apply {
        when {
            this < tolerance || relativeEquals(this, twoPi, tolerance) -> BigDecimal.TEN
            this > pi -> this.subtract(twoPi)
            this > halfPi -> this.subtract(pi)
        }
    }

    val signBaseAngle = baseAngle.sign
    val absBaseAngle = abs(baseAngle)

    if (absBaseAngle < tolerance) return BigDecimal.ONE

    if (relativeEquals(absBaseAngle, pi, tolerance)) return -BigDecimal.ONE

    if (relativeEquals(absBaseAngle, halfPi, tolerance) || relativeEquals(absBaseAngle, threeHalvesPi, tolerance)) return BigDecimal.ZERO

    val result = if (absBaseAngle <= quarterPi) {
        println("cosineCore")
        cosineCore(absBaseAngle, internalMathContext).multiply(signBaseAngle)
    } else {
        println("sineCore")
        sineCore(halfPi.subtract(absBaseAngle), internalMathContext).multiply(signBaseAngle)
    }

    return if (rounding) result.roundAndStrip(mathContext) else result
}

private fun cosineCore(angle: BigDecimal, mathContext: MathContext): BigDecimal = recurrentPowerSeriesCore(
    argumentTerm = angle.squared,
    firstTerm = BigDecimal.ONE,
    indexFunction = { n -> -BigDecimal.valueOf(((2L * n) * (2L * n - 1L))).reciprocal(mathContext) },
    mathContext = mathContext
)

fun tan(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isNegative()) return -tan(-x, mathContext, rounding)

    val internalMathContext = MathContext(mathContext.precision + 3, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision - 2)

    val pi = getConstant("pi", internalMathContext.precision + 1)
    val quarterPi = pi.multiply(BigDecimal("0.25"))
    val halfPi = pi.multiply(BigDecimal("0.5"))
    val twoPi = pi.multiply(BigDecimal.TWO)

    val baseAngle = x.remainder(twoPi, internalMathContext)

    if (baseAngle > pi) return tan(baseAngle.subtract(pi), mathContext, rounding)

    if (baseAngle < tolerance || relativeEquals(baseAngle, pi, tolerance)) return BigDecimal.ZERO

    if (relativeEquals(baseAngle, halfPi, tolerance)) throw ArithmeticException("Undefined: tangent of right angle $baseAngle")

    if (relativeEquals(baseAngle, quarterPi, tolerance)) return BigDecimal.ONE

    if (relativeEquals(baseAngle, quarterPi.multiply(BigDecimal(3)), tolerance)) return -BigDecimal.ONE

    val sinX = if (baseAngle <= quarterPi) {
        sineCore(baseAngle, internalMathContext)
    } else {
        cosineCore(halfPi.subtract(baseAngle), internalMathContext)
    }
    val cosX = if (baseAngle <= quarterPi) {
        cosineCore(baseAngle, internalMathContext)
    } else {
        sineCore(halfPi.subtract(baseAngle), internalMathContext)
    }
    val result = sinX.divide(cosX, internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun csc(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val sine = sin(x, mathContext, false)

    if (sine.isZero()) throw ArithmeticException("csc($x) is undefined")

    val result = sine.reciprocal(mathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun sec(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val cosine = cos(x, mathContext, false)

    if(cosine.isZero()) throw ArithmeticException("sec($x) is undefined")

    val result = cosine.reciprocal(mathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun cot(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision + 2, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(mathContext.precision - 2)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.divide(BigDecimal.TWO, mathContext)
    val threeHalvesPi = halfPi.multiply(BigDecimal("3"), mathContext)
    val twoPi = pi.multiply(BigDecimal.TWO, mathContext)
    val angle = abs(x.remainder(twoPi, mathContext))

    if (relativeEquals(angle, halfPi, tolerance)) return BigDecimal.ZERO
    if (relativeEquals(angle, threeHalvesPi, tolerance)) return BigDecimal.ZERO

    val tangent = tan(x, mathContext, false)

    if (tangent.isZero()) throw ArithmeticException("cot($x) is undefined")

    val result = tangent.reciprocal(internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun asin(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    if (abs(x) > BigDecimal.ONE) throw ArithmeticException("asin domain: |x| ≤ 1")

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.multiply(BigDecimal("0.5"), mathContext)

    if (x.isZero()) return BigDecimal.ZERO
    if (x.isOne()) return halfPi
    if (x == -BigDecimal.ONE) return -halfPi

    val result = if (abs(x) > BigDecimal("0.9")) {
        // asin(x) = atan(x / sqrt(1 - x^2))
        val oneMinusXSquared = BigDecimal.ONE.subtract(x.pow(2, internalMathContext), internalMathContext)
        val sqrt = squareRoot(oneMinusXSquared, internalMathContext, rounding = false)
        atan(x.divide(sqrt, internalMathContext), internalMathContext, rounding = false)
    } else {
        asinCore(x, internalMathContext, tolerance)
    }

    return if (rounding) result.roundAndStrip(mathContext) else result
}

private fun asinCore(x: BigDecimal, mathContext: MathContext, tolerance: BigDecimal): BigDecimal {
    val xSquared = x.squared

    var term = x
    var sum = term
    var k = 1

    while (abs(term) > tolerance) {
        val numerator = BigDecimal((2 * k - 1).toLong()).squared
        val denominator = BigDecimal((2 * k).toLong() * (2 * k + 1).toLong())

        term = term.multiply(xSquared, mathContext)
            .multiply(numerator, mathContext)
            .divide(denominator, mathContext)

        sum = sum.add(term, mathContext)
        k++
    }

    return sum.round(mathContext)
}

fun acos(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (abs(x) > BigDecimal.ONE) throw ArithmeticException("acos domain: |x| <= 1")

    val internalMathContext = MathContext(mathContext.precision + 1, mathContext.roundingMode)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.divide(BigDecimal.TWO, mathContext)

    if (x == -BigDecimal.ONE) return pi
    if (x.isZero()) return halfPi
    if (x.isOne()) return BigDecimal.ZERO

    val result = halfPi.subtract(asin(x, internalMathContext, false), internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun atan(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (x.isZero()) return BigDecimal.ZERO

    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(mathContext.precision)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.divide(BigDecimal.TWO, mathContext)

    x.round(MathContext(4, mathContext.roundingMode))

    val result = if (abs(x) <= BigDecimal.ONE) {
        var sum = x
        var term = x
        var n = 1
        val xSquared = x.multiply(x)

        measureTimeMillis {
            while (abs(term) > tolerance) {
                term = term.multiply(-xSquared, internalMathContext)
                val divisor = BigDecimal(2 * n + 1)
                sum = sum.add(term.divide(divisor, internalMathContext), internalMathContext)
                n++
                //println(abs(term) > tolerance)
            }
        }

        //println("$xForDebuggingPurpose : $time")

        sum
    } else {
        val reciprocal = x.reciprocal(internalMathContext)
        val correction = halfPi.multiply(if (x.isNonNegative()) BigDecimal.ONE else -BigDecimal.ONE)
        correction.subtract(atan(reciprocal), internalMathContext)
    }
    return if (rounding) result.roundAndStrip(mathContext) else result
}

/**
 * atan2(0, 0) returns 0 for compatibility with IEEE 754 / Java Math.atan2,
 * although the value is mathematically undefined.
 */
fun atan2(y: BigDecimal, x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.divide(BigDecimal.TWO, mathContext)

    val result = when {
        x.isZero() && y.isZero() -> BigDecimal.ZERO
        x == y -> pi.divide(BigDecimal("4"), internalMathContext).multiply(x.sign)
        x.isPositive() -> atan(y.divide(x, internalMathContext), internalMathContext)
        x.isNegative() && y.isNonNegative() -> atan(y.divide(x, internalMathContext), internalMathContext).add(pi, internalMathContext)
        x.isNegative() && y.isNegative() -> atan(y.divide(x, internalMathContext), internalMathContext).subtract(pi, internalMathContext)
        x.isZero() && y.isPositive() -> halfPi
        x.isZero() && y.isNegative() -> -halfPi
        else -> throw ArithmeticException("atan2($y, $x) is undefined")
    }

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun acsc(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val pi = getConstant("pi", mathContext.precision)
    val halfPi = pi.divide(BigDecimal.TWO, mathContext)

    if (abs(x) < BigDecimal.ONE) throw ArithmeticException("acsc(x) is only defined for |x| ≥ 1")
    if (abs(x).isOne()) return halfPi.multiply(x.sign)

    val result = asin(x.reciprocal(internalMathContext), internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun asec(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    if (abs(x) < BigDecimal.ONE) throw ArithmeticException("asec(x) is only defined for |x| ≥ 1")

    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val result = acos(x.reciprocal(internalMathContext), internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun acot(x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)

    val halfPi = getConstant("pi", internalMathContext.precision).divide(BigDecimal.TWO, internalMathContext)
    val result = if (x.isZero()) halfPi else atan(x.reciprocal(internalMathContext), internalMathContext)

    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun acot2(y: BigDecimal, x: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true) : BigDecimal {
    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val result = atan2(x, y, internalMathContext, false)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

// Miscellaneous
fun relativeEquals(x: BigDecimal, y: BigDecimal, tolerance: BigDecimal): Boolean {
    require(tolerance.isNonNegative()) { "Tolerance must be non-negative" }
    if (x == y) return true

    val min = min(x, y)
    val max = max(x, y)
    val tolerance = if (x.isZero() || y.isZero()) tolerance else tolerance.multiply(max)

    return max.subtract(min) <= tolerance
    // NOTE: abs could have been used, but min-max pair was more readable, personally
}

/**
 * Computes the Arithmetic-Geometric Mean (AGM) of two positive [BigDecimal] numbers using iterative refinement.
 *
 * The AGM of two numbers `a0` and `b0` is defined as the common limit of the sequences:
 * - aₙ₊₁ = (aₙ + bₙ) / 2
 * - bₙ₊₁ = sqrt(aₙ * bₙ)
 *
 * Iteration continues until the absolute difference between the updated values is less than the internal tolerance,
 * which is derived from the target [mathContext] precision.
 *
 * @param x the start non-negative input value
 * @param y the second non-negative input value
 * @param mathContext the desired output precision (default: [mathCtx])
 * @param rounding whether to round and strip trailing zeros from the result (default: `true`)
 * @return the computed arithmetic-geometric mean of [x] and [y]
 * @throws ArithmeticException if square root fails to converge or values are negative
 */
fun agm(x: BigDecimal, y: BigDecimal, mathContext: MathContext = mathCtx, rounding: Boolean = true): BigDecimal {
    val internalMathContext = MathContext(mathContext.precision * 2, mathContext.roundingMode)
    val tolerance = BigDecimal.ONE.movePointLeft(internalMathContext.precision)

    var a = x
    var b = y

    val maxIterations = 1_000_000
    var count = 0
    while (true) {
        val nextA = a.add(b).divide(BigDecimal.TWO, internalMathContext)
        val nextB = squareRoot(a.multiply(b), internalMathContext, rounding = false)

        if (relativeEquals(nextA, nextB, tolerance)) {
            a = nextA
            b = nextB
            break
        }
        if (++count > maxIterations) throw ArithmeticException("AGM did not converge within $maxIterations iterations")
        a = nextA
        b = nextB
    }

    val result = a.add(b).divide(BigDecimal.TWO, internalMathContext)
    return if (rounding) result.roundAndStrip(mathContext) else result
}

fun compareBigDecimals(x: BigDecimal, y: BigDecimal, mathContext: MathContext = mathCtx): Int {
    val p = mathContext.precision.coerceAtLeast(17).coerceAtMost(64)
    val tolerance = BigDecimal.ONE.movePointLeft(p)
    return if (relativeEquals(x, y, tolerance)) 0 else x.compareTo(y)
}

fun getConstant(name: String, precision: Int = MAX_PRECISION) : BigDecimal {
    require(precision >= 0) { "Negative extra precision are not allowed" }
    val internalMathContext = if (precision < MAX_PRECISION) MathContext(precision) else MathContext(MAX_PRECISION)

    val constant = when (name.lowercase()) {
        "pi" -> PI
        "log_2", "ln_2", "ln2" -> LOG_2
        "e" -> E
        "sqrt_2" -> SQRT_2
        else -> error("Constant is not in the list")
    }
    return constant.round(internalMathContext)
}

fun mathContextFromScaleAndValues(
    x: BigDecimal,
    y: BigDecimal,
    scale: Int,
    roundingMode: RoundingMode = RoundingMode.HALF_EVEN
) : MathContext {
    val maxAbs = max(abs(x), abs(y))
    val integerDigits = maxAbs.precision() - maxAbs.scale()
    val precision = (integerDigits + scale).coerceAtLeast(1)
    return MathContext(precision, roundingMode)
}

fun random(
    x: BigDecimal?,
    y: BigDecimal?,
    inclusiveMin: Boolean = true,
    inclusiveMax: Boolean = true,
    random: Random = SecureKotlinRandom.Instance
): BigDecimal {
    val scaleRange = 8..32
    val precisionRange = 8..32

    // Determine scale and precision heuristically or from inputs
    val scale = listOfNotNull(x?.scale(), y?.scale()).maxOrNull() ?: scaleRange.random(random)
    val precision = listOfNotNull(x?.precision(), y?.precision()).maxOrNull()?.coerceAtLeast(scale)
        ?: precisionRange.random(random).coerceAtLeast(scale)

    val mathContext = MathContext(precision, RoundingMode.HALF_EVEN)

    // Fallback bounds if unbounded
    val maxMagnitude = BigDecimal.TEN.pow(precision - scale)

    val minBound = x ?: maxMagnitude.negate()
    val maxBound = y ?: maxMagnitude

    val (min, max) = if (minBound <= maxBound) minBound to maxBound else maxBound to minBound

    val scaledMin = min.setScale(scale, RoundingMode.FLOOR)
    val scaledMax = max.setScale(scale, RoundingMode.CEILING)

    val adjustedMin = if (inclusiveMin) scaledMin else scaledMin.nextUp()
    val adjustedMax = if (inclusiveMax) scaledMax else scaledMax.nextDown()

    val multiplier = BigDecimal.TEN.pow(scale)
    val integerMin = (adjustedMin * multiplier).ceil().toBigInteger()
    val integerMax = (adjustedMax * multiplier).floor().toBigInteger()

    require(integerMin <= integerMax) {
        "No valid values to randomize after adjusting range for scale and inclusivity"
    }

    val randomInteger = random(
        x = integerMin,
        y = integerMax,
        random = random
    )

    return BigDecimal(randomInteger, scale).round(mathContext)
}

fun factorialBD(n: Int): BigDecimal = factorialBI(n).toBigDecimal()

fun BigDecimal.roundAndStrip(mathContext: MathContext = mathCtx): BigDecimal = this.round(mathContext).stripTrailingZeros()

fun BigDecimal.roundToNearestSimpleValue(
    mathContext: MathContext = mathCtx
) : BigDecimal {
    val maxTrailingDigits: Int = mathContext.precision / 2 - 2
    require(maxTrailingDigits <= mathContext.precision) { "maxTrailingApproxDigits cannot exceed MathContext precision" }

    val roundedByContext = this.round(mathContext)
    val s = roundedByContext.stripTrailingZeros().toPlainString()
    val dotIndex = s.indexOf('.')
    if (dotIndex == -1) return roundedByContext.stripTrailingZeros()
    val frac = s.substring(dotIndex + 1)

    val tailLength = maxTrailingDigits.coerceAtMost(frac.length)
    val tail = frac.takeLast(tailLength)

    fun isMostlyZerosOrNines(str: String) : Boolean {
        if (str.isEmpty()) return false
        val zeroCount = str.count { it == '0' }
        val nineCount = str.count { it == '9' }
        val length = str.length
        return zeroCount >= length * 0.85 || nineCount >= length * 0.85
    }

    // Use a looser epsilon to catch tiny floating error differences like in your case
    val looseEpsilon = BigDecimal.ONE.movePointLeft(maxTrailingDigits / 2 + 1)

    if (isMostlyZerosOrNines(tail)) {
        for (scale in 0..tailLength) {
            val candidate = roundedByContext.setScale(scale, mathContext.roundingMode)
            if (abs(roundedByContext.subtract(candidate)) <= looseEpsilon) {
                return candidate.stripTrailingZeros()
            }
        }
    }

    return roundedByContext.stripTrailingZeros()
}

private fun recurrentPowerSeriesCore(
    argumentTerm: BigDecimal,
    firstTerm: BigDecimal,
    indexFunction: (Int) -> BigDecimal,
    mathContext: MathContext
): BigDecimal {
    val tolerance = BigDecimal.ONE.movePointLeft(mathContext.precision)

    var term = firstTerm
    var sum = term
    var k = 1

    while (abs(term) >= tolerance) {
        term = term.multiply(indexFunction(k), mathContext).multiply(argumentTerm, mathContext)
        sum = sum.add(term, mathContext)
        k++
    }
    return sum
}