//@file:Suppress("unused")

package com.jeffbruma.stemutility.miscellaneous

import com.jeffbruma.stemutility.TOLERANCE
import com.jeffbruma.stemutility.miscellaneous.decimal.div
import com.jeffbruma.stemutility.miscellaneous.decimal.plus
import com.jeffbruma.stemutility.miscellaneous.decimal.pow
import com.jeffbruma.stemutility.miscellaneous.decimal.rem
import com.jeffbruma.stemutility.miscellaneous.decimal.root
import com.jeffbruma.stemutility.miscellaneous.decimal.times
import com.jeffbruma.stemutility.miscellaneous.integer.plus
import com.jeffbruma.stemutility.miscellaneous.integer.pow
import com.jeffbruma.stemutility.miscellaneous.integer.rangeTo
import com.jeffbruma.stemutility.miscellaneous.integer.times
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.*
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.log
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh
import kotlin.toBigInteger

val Double.Companion.ZERO: Double
    get() = 0.0

val Float.Companion.ZERO: Float
    get() = 0f

val Long.Companion.ZERO: Long
    get() = 0L

val Int.Companion.ZERO: Int
    get() = 0

val Short.Companion.ZERO: Short
    get() = 0

val Byte.Companion.ZERO: Byte
    get() = 0


/**
 * Converts this [Number] to a [BigInteger], truncating any fractional part toward zero.
 *
 * - `123.999` → `123`
 * - Negative values: `-123.999` → `-123`
 *
 * This is a lossy conversion for non-integer inputs.
 */
fun Number.toBigInteger(): BigInteger = when (this) {
    is BigInteger -> this
    is BigDecimal -> this.toBigInteger() // truncates toward zero
    is Long, is Int, is Short, is Byte -> BigInteger.valueOf(this.toLong())
    is Double, is Float -> {
        require(this.toDouble().isFinite()) { "Cannot convert NaN or Infinity to BigDecimal" }
        BigDecimal(this.toString()).toBigInteger()
    }
    else -> throw IllegalArgumentException("Unsupported Number subtype: ${this::class}")
}

/**
 * Converts this [Number] to a [BigInteger] precisely.
 *
 * This function handles various [Number] subtypes, ensuring an exact conversion to [BigInteger]
 * where possible.
 *
 * - If the number is already a [BigInteger], it's returned directly.
 * - If the number is a [BigDecimal], it's converted to a [BigInteger] using [BigDecimal.toBigIntegerExact].
 * - If the number is a [Long], [Int], [Short], or [Byte], it's converted to a [BigInteger]
 * using [BigInteger.valueOf].
 * - If the number is a [Double] or [Float], it's first checked to ensure it's a finite number
 * (not [Double.NaN] or [Double.POSITIVE_INFINITY]/[Double.NEGATIVE_INFINITY]).
 * It's then converted to a [BigDecimal] using its string representation to maintain precision,
 * and subsequently converted to a [BigInteger] using [BigDecimal.toBigIntegerExact].
 * An [IllegalArgumentException] is thrown if the [Double] or [Float] is not finite.
 * - For any other unsupported [Number] subtype, an [IllegalArgumentException] is thrown.
 *
 * @return The exact [BigInteger] representation of this [Number].
 * @throws IllegalArgumentException if the number is a non-finite [Double] or [Float],
 * or if the [Number] subtype is unsupported.
 * @see BigInteger
 * @see BigDecimal
 */
fun Number.toBigIntegerExact(): BigInteger = when (this) {
    is BigInteger -> this
    is BigDecimal -> this.toBigIntegerExact()
    is Long, is Int, is Short, is Byte -> BigInteger.valueOf(this.toLong())
    is Double, is Float -> {
        require(this.toDouble().isFinite()) { "Cannot convert NaN or Infinity to BigDecimal" }
        BigDecimal(this.toString()).toBigIntegerExact()
    }
    else -> throw IllegalArgumentException("Unsupported Number subtype: ${this::class}")
}

/**
 * Converts this [Number] to a [BigDecimal].
 *
 * This function handles various [Number] subtypes:
 * - If the number is already a [BigDecimal], it is returned directly.
 * - If the number is a [BigInteger], it is converted to a [BigDecimal].
 * - If the number is a [Long], [Int], [Short], or [Byte], it is converted to a [Long] first, then to a [BigDecimal].
 * - If the number is a [Double] or [Float], it is converted to a [BigDecimal] using its string representation.
 * Note that [NaN][Double.isNaN] or [Infinity][Double.isInfinite] values for [Double] or [Float] are not supported and will throw an [IllegalArgumentException].
 *
 * @return This [Number] as a [BigDecimal].
 * @throws IllegalArgumentException If the [Number] subtype is not supported or if a [Double] or [Float]
 * is [NaN][Double.isNaN] or [Infinity][Double.isInfinite].
 */
fun Number.toBigDecimal(): BigDecimal = when (this) {
    is BigDecimal -> this
    is BigInteger -> BigDecimal(this)
    is Long, is Int, is Short, is Byte -> BigDecimal(this.toLong())
    is Double, is Float -> {
        require(this.toDouble().isFinite()) { "Cannot convert NaN or Infinity to BigDecimal" }
        BigDecimal(this.toString())
    }
    else -> throw IllegalArgumentException("Unsupported Number subtype: ${this::class}")
}

/**
 * Converts this [Number] to a [BigDecimal], ensuring that the number represents an exact whole number.
 *
 * This function handles various [Number] subtypes:
 * - If the number is already a [BigDecimal], it is returned directly only if it represents a whole number
 * (i.e., its scale after stripping trailing zeros is less than or equal to 0). Otherwise, an [ArithmeticException] is thrown.
 * - If the number is a [BigInteger], it is converted directly to a [BigDecimal].
 * - If the number is a [Long], [Int], [Short], or [Byte], it is converted to a [Long] first, then to a [BigDecimal].
 * These types inherently represent whole numbers.
 * - If the number is a [Double] or [Float], it is first converted to a [BigDecimal] using its string representation.
 * It then checks if this [BigDecimal] represents a whole number (i.e., its scale after stripping trailing zeros
 * is less than or equal to 0). If not, an [ArithmeticException] is thrown.
 * [NaN][Double.isNaN] or [Infinity][Double.isInfinite] values for [Double] or [Float] are not supported
 * and will throw an [IllegalArgumentException].
 *
 * @return This [Number] as a [BigDecimal], representing an exact whole number.
 * @throws ArithmeticException If the number is a [BigDecimal], [Double], or [Float] and does not represent an exact whole number.
 * @throws IllegalArgumentException If the [Number] subtype is not supported or if a [Double] or [Float]
 * is [NaN][Double.isNaN] or [Infinity][Double.isInfinite].
 */
fun Number.toBigDecimalExact(): BigDecimal = when (this) {
    is BigDecimal -> {
        if (this.stripTrailingZeros().scale() <= 0) this
        else throw ArithmeticException("Not an exact whole number: $this")
    }

    is BigInteger -> BigDecimal(this)
    is Long, is Int, is Short, is Byte -> BigDecimal(this.toLong())
    is Double, is Float -> {
        require(this.toDouble().isFinite()) { "Cannot convert NaN or Infinity to BigDecimal" }
        val bd = BigDecimal(this.toString())
        if (bd.stripTrailingZeros().scale() <= 0) bd
        else throw ArithmeticException("Not an exact whole number: $this")
    }

    else -> throw IllegalArgumentException("Unsupported Number subtype: ${this::class}")
}

/**
 * Checks if this [Number] is equal to zero.
 *
 * This function handles various [Number] subtypes by comparing them to their respective zero values:
 * - [BigDecimal] is compared to [BigDecimal.ZERO].
 * - [BigInteger] is compared to [BigInteger.ZERO].
 * - [Double] is compared to [Double.Companion.ZERO].
 * - [Float] is compared to [Float.Companion.ZERO].
 * - [Long] is compared to [Long.Companion.ZERO].
 * - [Int] is compared to [Int.Companion.ZERO].
 * - [Short] is compared to [Short.Companion.ZERO].
 * - [Byte] is compared to [Byte.Companion.ZERO].
 *
 * For [Double] and [Float], [NaN][Double.isNaN] and [Infinity][Double.isInfinite] values are correctly handled
 * by their respective `compareTo` implementations, resulting in `false` for equality with zero.
 *
 * @return `true` if this [Number] is zero, `false` otherwise.
 * @throws IllegalArgumentException If the [Number] subtype is not supported for comparison to zero.
 */
fun Number.isZero(): Boolean = when (this) {
    is BigDecimal -> this.compareTo(BigDecimal.ZERO) == 0
    is BigInteger -> this.compareTo(BigInteger.ZERO) == 0
    is Double -> this.compareTo(Double.ZERO) == 0
    is Float -> this.compareTo(Float.ZERO) == 0
    is Long -> this.compareTo(Long.ZERO) == 0
    is Int -> this.compareTo(Int.ZERO) == 0
    is Short -> this.compareTo(Short.ZERO) == 0
    is Byte -> this.compareTo(Byte.ZERO) == 0
    else -> throw IllegalArgumentException("$this is not comparable to zero")
}

operator fun Number.compareTo(other: Number) = when {
    this is BigDecimal || other is BigDecimal -> this.toBigDecimal().compareTo(other.toBigDecimal())
    this is BigInteger || other is BigInteger -> this.toBigInteger().compareTo(other.toBigInteger())
    else -> {
        val a = this.toDouble()
        val b = other.toDouble()
        val absDiff = abs(a - b)
        val maxAbs = max(abs(a), abs(b))

        when {
            absDiff < TOLERANCE -> 0
            absDiff < TOLERANCE * maxAbs -> 0
            else -> a.compareTo(b)
        }
    }
}

operator fun Number.unaryMinus(): Number = when (this) {
    is BigInteger -> this.negate()
    is BigDecimal -> this.negate()
    is Double -> -this
    is Float -> -this
    is Long -> -this
    is Int -> -this
    is Short -> (-this.toInt()).toShort()
    is Byte -> (-this.toInt()).toByte()
    else -> -this.toDouble()
}

operator fun Number.plus(other: Number) = simplifyNumber(
    when {
        this.isIntegerType() && other.isIntegerType() -> this.toBigInteger() + other.toBigInteger()
        // HACK: Float-to-Double fix for test accuracy
        this is Float && other is Float -> BigDecimal(this.toString()) * BigDecimal(other.toString())
        this is Float && other !is Float -> BigDecimal(this.toString()) * other.toBigDecimal()
        other is Float -> this.toBigDecimal() * BigDecimal(other.toString())
        else -> this.toBigDecimal() + other.toBigDecimal()
    }
)

operator fun Number.minus(other: Number) = this.plus(-other)

operator fun Number.times(other: Number) = simplifyNumber(
    when {
        this.isIntegerType() && other.isIntegerType() -> this.toBigInteger() * other.toBigInteger()
        // HACK: Float-to-Double fix for test accuracy
        this is Float && other is Float -> this.toBigDecimal() * other.toBigDecimal()
        this is Float && other !is Float -> BigDecimal(this.toString()) * other.toBigDecimal()
        other is Float -> this.toBigDecimal() * BigDecimal(other.toString())
        else -> this.toBigDecimal() * other.toBigDecimal()
    }
)

operator fun Number.div(other: Number): Number {
    require(!other.isZero()) { "Division by zero not allowed" }
    return simplifyNumber(
        when {
            // HACK: Float-to-Double fix for test accuracy
            this is Float && other is Float -> BigDecimal(this.toString()) / BigDecimal(other.toString())
            this is Float && other !is Float -> BigDecimal(this.toString()) / other.toBigDecimal()
            other is Float -> this.toBigDecimal() / BigDecimal(other.toString())
            else -> this.toBigDecimal() / other.toBigDecimal()
        }
    )
}

operator fun Number.rem(modulus: Number): Number {
    require(!modulus.isZero()) { "Division by zero not allowed" }
    return simplifyNumber(
        when {
            // HACK: Float-to-Double fix for test accuracy
            this is Float && modulus is Float -> BigDecimal(this.toString()) % BigDecimal(modulus.toString())
            this is Float && modulus !is Float -> BigDecimal(this.toString()) % modulus.toBigDecimal()
            modulus is Float -> this.toBigDecimal() % BigDecimal(modulus.toString())
            else -> simplifyNumber(this.toBigDecimal() % modulus.toBigDecimal())
        }
    )
}

fun Number.pow(exponent: Number): Number {
    // HACK: Float-to-Double fix for test accuracy
    val base = if (this is Float) BigDecimal(this.toString()) else simplifyNumber(this)
    val exp = if (exponent is Float) BigDecimal(exponent.toString()) else simplifyNumber(exponent)

    return simplifyNumber(
        if (base.isIntegerType() && exp.isIntegerType()) {
//            println("both base and exp are whole")
            (base.toBigIntegerExact()).pow(exp.toBigIntegerExact())
        } else {
//            println("either base or exp is floating")
            (base.toBigDecimal()).pow(exp.toBigDecimal())
        }
    )
}

fun Number.root(degree: Int): Number {
    require(degree > 0) { "Root degree must be a positive integer" }

    val bigDecimal: BigDecimal = this.toBigDecimal()

    // Prevent even roots of negative numbers
    if (degree % 2 == 0 && bigDecimal < BigDecimal.ZERO) {
        throw IllegalArgumentException("Even root of negative number not allowed")
    }

    // Calculate the root using exponentiation
    return simplifyNumber(bigDecimal.root(degree))
}

fun exp(n: Number) = simplifyNumber(
    when (n) {
        is BigInteger -> com.jeffbruma.stemutility.miscellaneous.decimal.exp(n.toBigDecimal())
        is BigDecimal -> com.jeffbruma.stemutility.miscellaneous.decimal.exp(n)
        else -> exp(n.toDouble())
    }
)

fun log(n: Number): Number {
    require(n.toDouble() > 0.0) {
        "Logarithm argument is out of domain"
    }
    return simplifyNumber(
        ln(n.toDouble())
    )
}

fun log(n: Number, b: Number): Number {
    require(
        n.toDouble() in (Double.MIN_VALUE..Double.MAX_VALUE) &&
                b.toDouble() in (Double.MIN_VALUE..Double.MAX_VALUE) &&
                b.toDouble() != 1.0
    ) {
        "Logarithm argument and/or base is/are out of range"
    }

    return simplifyNumber(
        log(n.toDouble(), b.toDouble())
    )
}

fun sqr(n: Number) = simplifyNumber(
    n.toDouble() * n.toDouble()
)

fun sqrt(n: Number): Number {
    require(n >= 0) { "Radicand should be at least zero" }
    return simplifyNumber(sqrt(n.toDouble()))
}

fun sinh(n: Number) = simplifyNumber(
    sinh(n.toDouble())
)

fun cosh(n: Number) = simplifyNumber(
    cosh(n.toDouble())
)

fun tanh(n: Number) = simplifyNumber(
    tanh(n.toDouble())
)

fun arsinh(n: Number) = simplifyNumber(
    asinh(n.toDouble())
)

fun arcosh(n: Number): Number {
    require(n.toDouble() >= 1.0) { "Hyperbolic arccosine argument out of range" }
    return simplifyNumber(
        acosh(n.toDouble())
    )
}

fun artanh(n: Number): Number {
    require(n.toDouble() in (-1.0 + (-1.0).ulp..1.0)) { "Hyperbolic arctangent argument out of range" }
    return simplifyNumber(
        atanh(n.toDouble())
    )
}

fun sin(n: Number) = simplifyNumber(
    sin(n.toDouble())
)

fun cos(n: Number) = simplifyNumber(
    cos(n.toDouble())
)

fun tan(n: Number): Number = simplifyNumber(
    tan(n.toDouble())
)

fun asin(n: Number) = simplifyNumber(
    asin(n.toDouble())
)

fun acos(n: Number): Number {
    require(n.toDouble() in (-1.0..1.0 + 1.0.ulp)) { "Arccosine argument out of range" }
    return simplifyNumber(
        acos(n.toDouble())
    )
}

fun atan(n: Number) = simplifyNumber(
    atan(n.toDouble())
)

fun atan2(y: Number, x: Number) = simplifyNumber(
    atan2(y.toDouble(), x.toDouble())
)

fun Array<out Number>.sumOf(selector: (Number) -> Number): Number {
    var sum = 0.0
    for (element in this) {
        val selectedValue = selector(element)
        sum += selectedValue.toDouble()
    }
    return simplifyNumber(sum)
}

fun Array<out Number>.contentRelativeEquals(other: Array<out Number>?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this.size != other.size) return false

    for (i in this.indices) {
        if (!this[i].relativeEquals(other[i])) {
            return false
        }
    }
    return true
}

operator fun Number.times(other: Array<out Number>): Array<out Number> {
    return other.map { it * this }.toTypedArray()
}

operator fun Array<out Number>.times(other: Number) = other * this

operator fun Number.plus(other: Array<out Number>) = arrayOf(this) + other

fun frobenize(values: Array<out Number>): Number {
    if (values.size > 1 && values.drop(1).all { it.isZero() }) return abs(values[0].toDouble())
    return simplifyNumber(sqrt(
        values.sumOf {
            it.toDouble() * it.toDouble()
        }.toDouble()
    ))
}

//fun frobenize(list: List<Number>) = frobenize(list.toTypedArray())

fun Number.relativeEquals(other: Number): Boolean = this.compareTo(other) == 0

/** True if this value is exactly an Int (no fractional part, within Int range). */
fun Number.fitsInInt() = this is Byte || this is Short || this is Int

/** True if this value is exactly a Long (no fractional part, within Long range). */
fun Number.fitsInLong() = this is Byte || this is Short || this is Int || this is Long

fun Number.isIntegerType() = this is Byte || this is Short || this is Int || this is Long || this is BigInteger

fun Number.isFloatingType() = this is Float || this is Double || this is BigDecimal


/**
 * Checks if this [Number] has a fractional part.
 *
 * This function determines if a number contains digits after the decimal point.
 *
 * - For [Float] and [Double] types, it first ensures the number is a finite numerical value
 * (not [Float.NaN], [Float.POSITIVE_INFINITY], or [Float.NEGATIVE_INFINITY]).
 * Then, it checks if the remainder when divided by 1.0 is not equal to 0.0.
 * Throws an [IllegalArgumentException] if the floating-point number is not finite.
 * - For [BigDecimal] types, it checks if, after stripping trailing zeros, its scale
 * (the number of digits to the right of the decimal point) is greater than 0.
 * - For all other [Number] types (e.g., [Int], [Long]), it always returns `false` as they do not inherently have fractional parts.
 *
 * @return `true` if the number has a fractional part, `false` otherwise.
 * @throws IllegalArgumentException if a [Float] or [Double] input is not a finite number.
 */
fun Number.hasFractionalPart(): Boolean = when (this) {
    is Float, is Double -> {
        require(this.toDouble().isFinite()) { "Non-numerical floating-point number" }
        this % 1.0 != 0.0
    }
    is BigDecimal -> this.stripTrailingZeros().scale() > 0
    else -> false
}

/** True if this value is finite when converted to Double. */
fun Number.fitsInDouble(): Boolean =
    this.toDouble().isFinite()

/**
 * Simplifies a given number to the most appropriate type while maintaining its value.
 * The simplification may include converting large integers or decimals to their simplest forms
 * (e.g., BigInteger to Int or Long, BigDecimal to Double, etc.), based on specified tolerances.
 *
 * The method uses a base tolerance to handle decimal approximation and rounding. Specific logic
 * is applied depending on whether the input is a BigInteger, BigDecimal, or other number types.
 *
 * @param n The number to simplify. Accepts various implementations of the `Number` class, such as Int, Long, Double, BigInteger, or BigDecimal.
 * @param baseTolerance The tolerance value used to determine rounding precision for certain numbers. Defaults to `ABSOLUTE_TOLERANCE`.
 * @return The simplified number, which may be an Int, Long, Double, BigInteger, BigDecimal, or the original value if simplification is not applicable.
 */
fun simplifyNumber(
    n: Number,
    baseTolerance: Double = TOLERANCE
): Number {
    if (n is BigInteger) {
        //println("\"$n is BigInteger\" block has been called in simplifyNumber")
        return when (n) {
            in Int.MIN_VALUE.toBigInteger()..Int.MAX_VALUE.toBigInteger() -> n.toInt()
            in Long.MIN_VALUE.toBigInteger()..Long.MAX_VALUE.toBigInteger() -> n.toLong()
            else -> n
        }
    }

    if (n is BigDecimal) {
//        println("\"$n is BigDecimal\" block has been called in simplifyNumber")

        val rounded = n.setScale(15, RoundingMode.HALF_UP)
        val fractionalPart = (rounded % BigDecimal.ONE).abs()
        val tolerance = BigDecimal.valueOf(baseTolerance)

//        println("Original: $n")
//        println("Rounded: $rounded")
//        println("Fractional part: $fractionalPart")
//        println("Tolerance: $tolerance")

        return when {
            // Close enough to a whole number
            fractionalPart <= tolerance -> {
                val whole = rounded.setScale(0, RoundingMode.HALF_UP)
                val wholeBigInt = whole.toBigIntegerExact()
//                println("Rounded to whole: $wholeBigInt")

                when (wholeBigInt) {
                    in Int.MIN_VALUE.toBigInteger()..Int.MAX_VALUE.toBigInteger() -> wholeBigInt.toInt()
                    in Long.MIN_VALUE.toBigInteger()..Long.MAX_VALUE.toBigInteger() -> wholeBigInt.toLong()
                    else -> wholeBigInt
                }
            }

            // Not whole, but within Double range
            rounded.abs() <= BigDecimal.valueOf(Double.MAX_VALUE) -> {
                val d = rounded.toDouble()
//                println("Simplified to: $d (Double)")
                d
            }

            // Too large or precise — retain as BigDecimal
            else -> {
//                println("Value too large or precise for Double, returning BigDecimal")
                rounded
            }
        }
    }

    else {
//        println("\"$n is Double/Float/etc\" block has been called in simplifyNumber")

        val value = if (n is Float)
            BigDecimal(n.toString()).toDouble() // Strip float artifact
        else
            n.toDouble()

        if (value.isNaN() || value.isInfinite() || value !in -Double.MAX_VALUE..Double.MAX_VALUE) {
//            println("$value is NaN/Infinity/out of range — returning as-is")
            return value
        }

        // Round using BigDecimal to capture numeric closeness to whole numbers
        val rounded = BigDecimal(value).setScale(15, RoundingMode.HALF_UP)
        val fractionalPart = rounded.remainder(BigDecimal.ONE).abs()
        val tolerance = BigDecimal.valueOf(baseTolerance)

//        println("Original double: $value")
//        println("Rounded: $rounded")
//        println("Fractional part: $fractionalPart")
//        println("Tolerance: $tolerance")

        return when {
            fractionalPart <= tolerance -> {
                val whole = rounded.setScale(0, RoundingMode.HALF_UP)
                val wholeBigInt = whole.toBigIntegerExact()
//                println("Rounded to whole: $wholeBigInt")

                when (wholeBigInt) {
                    in Int.MIN_VALUE.toBigInteger()..Int.MAX_VALUE.toBigInteger() -> wholeBigInt.toInt()
                    in Long.MIN_VALUE.toBigInteger()..Long.MAX_VALUE.toBigInteger() -> wholeBigInt.toLong()
                    else -> wholeBigInt
                }
            }

            else -> {
                val d = rounded.toDouble()
//                println("Simplified to: $d (Double)")
                d
            }
        }
    }

}