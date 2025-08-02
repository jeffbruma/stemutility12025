package com.jeffbruma.stemutility.miscellaneous.decimal

import com.jeffbruma.stemutility.miscellaneous.SecureKotlinRandom
import com.jeffbruma.stemutility.miscellaneous.toBigDecimal
import java.math.BigDecimal
import java.math.MathContext
import kotlin.random.Random

class BigDecimalRange private constructor(
    val start: BigDecimal,
    val end: BigDecimal,
    val inclusiveStart: Boolean = true,
    val inclusiveEnd: Boolean = true,
    val mathContext: MathContext = mathCtx,
    val step: BigDecimal? = null
) : Comparable<BigDecimalRange> {

    val first: BigDecimal by lazy {
        if (inclusiveStart) {
            start
        } else {
            if (step == null) {
                start.nextUp()
            } else start + step
        }
    }

    val last: BigDecimal by lazy {
        if (inclusiveEnd) end
        else {
            if (step == null) {
                end.nextDown()
            } else {
                val numStep = ((end.subtract(first)).abs().divide(step, mathContext)).floor()
                val tentativeLast = first + step * numStep
                if (tentativeLast == end) {
                    tentativeLast - step
                } else {
                    tentativeLast
                }
            }
        }
    }

    // Internal calculations for range properties.
    private val span: BigDecimal = last - first

    init {
        if (span.isZero()) {
            require(inclusiveStart && inclusiveEnd)
        }

        if (span.abs().isOne()) {
            require(inclusiveStart || inclusiveEnd)
        }
    }

    constructor(start: Number, end: Number, inclusiveStart: Boolean = true, inclusiveEnd: Boolean = true, mathContext: MathContext = mathCtx
    ) : this(start.toBigDecimal(), end.toBigDecimal(), inclusiveStart, inclusiveEnd, mathContext, null)

    constructor(start: String, end: String, inclusiveStart: Boolean = true, inclusiveEnd: Boolean = true, mathContext: MathContext = mathCtx
    ) : this(start.toBigDecimal(), end.toBigDecimal(), inclusiveStart, inclusiveEnd, mathContext, null)


    infix fun step(value: Number): BigDecimalRange {
        val newStep = value.toBigDecimal()
        if (span.isZero()) {
            require(newStep.isZero()) { "No step for singleton range." }
        } else {
            require(newStep.isNotZero()) { "Zero step is allowed only for a singleton range." }
        }
        require(newStep.signum() == span.signum()) { "Misaligned step." }
        require(newStep.abs() <= span.abs()) { "Step overextends start." }

        return BigDecimalRange(
            start = start,
            end = end,
            inclusiveStart = inclusiveStart,
            inclusiveEnd = inclusiveEnd,
            step = newStep
        )
    }

    infix fun step(step: String): BigDecimalRange = step(BigDecimal(step))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigDecimalRange) return false

        return start == other.start && end == other.end &&
                step?.compareTo(other.step) == 0 &&
                inclusiveStart == other.inclusiveStart &&
                inclusiveEnd == other.inclusiveEnd
    }

    override fun hashCode(): Int = listOf(start, end, inclusiveStart, inclusiveEnd, step).hashCode()

    override fun toString(): String {
        val left = if (inclusiveStart) "[" else "("

        val right = if (inclusiveEnd) "]" else ")"

        val stepPart = if (step == null || step.isZero()) "" else " step $step"

        return "$left$start..$end$right$stepPart"
    }

    override fun compareTo(other: BigDecimalRange): Int =
        start.compareTo(other.start).takeIf { it != 0 }
            ?: end.compareTo(other.end).takeIf { it != 0 }
            ?: inclusiveStart.compareTo(other.inclusiveStart).takeIf { it != 0 }
            ?: inclusiveEnd.compareTo(other.inclusiveEnd).takeIf { it != 0 }
            ?: compareSteps(step, other.step)

    operator fun contains(value: BigDecimal): Boolean {
        TODO()
    }

    operator fun contains(other: BigDecimalRange): Boolean {
        TODO()
    }

    fun sequence(): Sequence<BigDecimal> = sequence {
        TODO()
    }

    fun count(): Int {
        TODO()
    }

    fun random(random: Random = SecureKotlinRandom.Companion.Instance): BigDecimal {
        return random(start, end, inclusiveStart, inclusiveEnd, random)
    }

    fun reversed(): BigDecimalRange {
        if (span.isZero()) return this

        return BigDecimalRange(
            start = end,
            end = start,
            inclusiveStart = inclusiveEnd,
            inclusiveEnd = inclusiveStart,
            step = step?.negate()
        )
    }

    private fun compareSteps(a: BigDecimal?, b: BigDecimal?): Int = when {
        a == null && b == null -> 0
        a == null -> -1
        b == null -> 1
        else -> a.compareTo(b)
    }
}

/**
 * Creates an inclusive, unstepped [BigDecimalRange] from this value to [that].
 *
 * The resulting range has:
 * - Start = this value (inclusive)
 * - End = [that] value (exclusive)
 * - Step = 0 (unstepped, meaning not intended for iteration)
 *
 * To create a stepped range, use `.step(...)` on the result.
 *
 * Example:
 *     val range = BigDecimal("1")..BigDecimal("5")
 *     val stepped = range step 0.5
 */
operator fun BigDecimal.rangeTo(that: BigDecimal): BigDecimalRange =
    BigDecimalRange(this, that)

/**
 * Creates a half-open range from this value up to but not including [that].
 *
 * The resulting range is end-exclusive and unstepped (step = 0).
 * Throws [IllegalArgumentException] if the receiver is greater than [that].
 */
infix fun BigDecimal.until(that: BigDecimal): BigDecimalRange {
    require(this <= that) { "Cannot create a forward range with start > end: $this until $that" }
    return BigDecimalRange(this, that)
}

/**
 * Creates a descending range from this value down to [that], end-inclusive.
 *
 * The resulting range is unstepped (step = 0). It does not imply a default step of -1.
 * Throws [IllegalArgumentException] if the receiver is less than [that].
 */
infix fun BigDecimal.downTo(that: BigDecimal): BigDecimalRange {
    require(this >= that) { "Cannot create a descending range with start < end: $this downTo $that" }
    return BigDecimalRange(this, that, inclusiveEnd = true)
}

/**
 * Removes numerically duplicate [BigDecimal] values from the list,
 * treating values as equal if they are equal after stripping trailing zeros.
 *
 * For example, `1.0`, `1.00`, and `1` are considered the same.
 *
 * @receiver The list of [BigDecimal] values to deduplicate.
 * @return A list containing only the start occurrence of each numerically distinct value.
 */
fun List<BigDecimal>.distinctNormalized(): List<BigDecimal> {
    val seen = mutableListOf<BigDecimal>()
    return filter { value ->
        val norm = value.stripTrailingZeros()
        if (seen.any { it.compareTo(norm) == 0 }) false else {
            seen.add(norm)
            true
        }
    }
}