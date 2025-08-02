package com.jeffbruma.stemutility.miscellaneous.integer

import com.jeffbruma.stemutility.miscellaneous.SecureKotlinRandom
import com.jeffbruma.stemutility.miscellaneous.isZero
import com.jeffbruma.stemutility.miscellaneous.toBigInteger
import java.math.BigInteger
import kotlin.random.Random

/**
 * Represents a sequence of arbitrary-precision integers with inclusive [start] and [end] bounds,
 * and an optional [step] size.
 *
 *  * Examples:
 *  * - `BigIntegerRange(1, 5)`
 *  * - `BigIntegerRange(-1, 10) step 2` → -1, 1, 3, 5, 7, 9
 *  * - `BigInteger("1e18")..BigInteger("2e18")` for massive numeric ranges
 *
 * ⚠️ This class is intentionally **not Iterable**.
 *
 * Iterating over large `BigInteger` ranges (e.g. billions of values with `step = 1`) is extremely slow
 * and memory-intensive due to the overhead of arbitrary-precision arithmetic.
 * To iterate explicitly, use [sequence()], which gives you a lazily-evaluated sequence.
 */
class BigIntegerRange private constructor(
    val start: BigInteger,
    val end: BigInteger,
    val step: BigInteger? = null,
) : Comparable<BigIntegerRange> {

    // For consistency
    val first: BigInteger = start

    /**
     * The last value in this range that can be reached by the defined [step]
     * starting from [start] and not exceeding [end].
     *
     * If the range has no explicit step, [last] is simply equal to [end]
     * (as a `null` step is interpreted as a step of `1` or `-1` for sequence generation).
     * This value might be different from [end] if the range's total span does not
     * perfectly align with its step (e.g., `(1..10) step 3` has `last` as `7`).
     */
    val last: BigInteger by lazy {
        if (step == null || step.isZero()) {
            end
        } else {
            val numSteps = (end - start) / step // Calculate how many full steps fit within the span
            start + step * numSteps // The last reachable value is start + (number of steps * step)
        }
    }

    // Internal calculations for range properties.
    private val span: BigInteger = last - first

    /**
     * Creates a new [BigIntegerRange] using standard numeric types (`Int`, `Long`, etc.)
     * for its boundaries and an optional step.
     *
     * This is a convenience constructor to easily define ranges without explicitly
     * converting numbers to `BigInteger` first.
     *
     * @param start The starting value of the range (inclusive).
     * @param end The ending value of the range (inclusive).
     * @param step An optional step size. If `null`, the range steps by `1` (or `-1` for descending ranges).
     */
    constructor(start: Number, end: Number) : this(start.toBigInteger(), end.toBigInteger(), null)

    /**
     * Creates a new [BigIntegerRange] from string representations of its boundaries
     * and an optional step.
     *
     * This is useful when dealing with very large numbers that might exceed
     * standard numeric type limits, or when values are read from text.
     *
     * @param start The string representing the starting value of the range (inclusive).
     * @param end The string representing the ending value of the range (inclusive).
     * @param step An optional string representing the step size. If `null`, the range steps by `1` (or `-1`).
     * @throws NumberFormatException if any of the provided strings are not valid integer representations.
     */
    constructor(start: String, end: String) : this(BigInteger(start), BigInteger(end), null)

    /**
     * Defines a step size for this range, creating a **new** [BigIntegerRange] instance.
     *
     * This `infix` function allows for a more natural, readable syntax like `myRange step 2`.
     * The original range remains unchanged, as ranges are immutable.
     *
     * @param value The step size to apply, provided as any [Number] type (e.g., `Int`, `Long`).
     * It will be converted to `BigInteger`.
     * @return A new [BigIntegerRange] identical to this one, but with the specified step.
     * @throws IllegalArgumentException if the step is zero, overextends, or has a direction
     * opposite to the range's start/end. These checks
     * are performed during the new range's construction.
     */
    infix fun step(value: Number): BigIntegerRange {
        val step = value.toBigInteger()
        if (span.isZero()) {
            require(step.isZero()) { "No step for singleton range." }
        } else {
            require(step.isNotZero()) { "Zero step is allowed only for a singleton range." }
        }
        require(abs(step) <= abs(span)) { "Step overextends start." } // this requirement can actually handle the preceding if-block, but the if-block's main purpose is emphasis on elements of range
        require(step.signum() == span.signum()) { "Misaligned step." }

        return BigIntegerRange(start, end, step)
    }

    /**
     * Defines a step size for this range using a [String] representation,
     * creating a **new** [BigIntegerRange] instance.
     *
     * This `infix` function provides a readable syntax like `myRange step "1000"`.
     * The original range remains unchanged.
     *
     * @param step The string representing the step size (e.g., "1", "-5", "1000000").
     * @return A new [BigIntegerRange] identical to this one, but with the specified step.
     * @throws NumberFormatException if the string cannot be parsed as a valid integer.
     * @throws IllegalArgumentException if the step is zero, overextends, or has a direction
     * opposite to the range's start/end.
     */
    infix fun step(step: String): BigIntegerRange = step(BigInteger(step))


    /**
     * Compares this range to another for **strict structural equality**.
     *
     * Two ranges are considered equal if and only if they have:
     * - The exact same [start] value.
     * - The exact same [end] value.
     * - The exact same [step] value (including both being `null`).
     *
     * This means ranges constructed with different `start`, `end`, or `step` values
     * will be considered unequal, even if they might produce a similar set of values.
     * For instance, `BigIntegerRange(1, 5)` (step=null) is not equal to `BigIntegerRange(1, 5, BigInteger.ONE)` (step=1),
     * even though both would iterate through 1, 2, 3, 4, 5.
     *
     * Examples:
     * - `BigIntegerRange(1, 5) == BigIntegerRange(1, 5)` -> `true`
     * - `BigIntegerRange(1, 5) == BigIntegerRange(1, 5, null)` -> `true` (since `step=null` matches `null`)
     * - `BigIntegerRange(1, 5, BigInteger.ONE) == BigIntegerRange(1, 5, BigInteger.ONE)` -> `true`
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigIntegerRange) return false

        return step == other.step && start == other.start && end == other.end
    }

    /**
     * Returns a hash code consistent with **strict structural equality**.
     *
     * The hash code is generated based on the exact [start], [end], and [step]
     * properties of the range.
     *
     * This ensures that any two ranges considered equal by the `equals` method
     * will produce the same hash code, which is crucial for correct behavior in
     * hash-based collections like `HashSet` or `HashMap`.
     */
    override fun hashCode(): Int = listOf(start, end, step).hashCode()

    /**
     * Returns a human-readable string representation of this range.
     *
     * The format always uses square brackets `[]` to denote that both [start] and [end]
     * are **inclusive**. The [step] is appended only if it is explicitly defined (not `null`).
     *
     * Format: `[[start]..[end]]` (for unstepped ranges) or `[[start]..[end]] step X` (for stepped ranges).
     *
     * Examples:
     * - `[1..10]` (for `BigIntegerRange(1, 10)` with `step = null`)
     * - `[0..9] step 3` (for `BigIntegerRange(0, 9) step 3`)
     * - `[5..5]` (for a singleton range like `BigIntegerRange(5, 5)`)
     */
    override fun toString(): String {
        val stepString = if (step == null) "" else " step $step"

        // Always use square brackets as start and end are inclusive.
        return "[$start..$end]$stepString"
    }

    /**
     * Compares this range to another using lexicographic ordering:
     * - First by start bound
     * - Then by end bound
     * - Then by step value (normalized)
     *
     * This is structural and does not imply range containment or size comparison.
     */
    override fun compareTo(other: BigIntegerRange): Int =
        start.compareTo(other.start).takeIf { it != 0 }
            ?: end.compareTo(other.end).takeIf { it != 0 }
            ?: compareSteps(step, other.step)

    /**
     * Returns `true` if [value] lies within the bounds and aligns with the step.
     */
    operator fun contains(value: BigInteger): Boolean {
        val s = step ?: return value >= min(start, end) && value <= max(start, end)

        if (this.span.isZero()) return value == start

        if (s.isPositive() && (value < start || value > end)) return false

        if (s.isNegative() && (value > start || value < end)) return false

        return (value - start).remainder(s).isZero()
    }

    /**
     * Returns `true` if [other] is entirely contained within this range.
     *
     * Containment is based on:
     * - Both start and end positions
     * - Inclusivity flags of both ranges
     *   - This range must extend at least as far as the [other] in both directions.
     */
    operator fun contains(other: BigIntegerRange): Boolean {
        // If either side is stepped, validate step direction compatibility
        if (this.step != null && this.step.isNotZero() && other.step != null && other.step.isNotZero()) {
            require(this.step.signum() == other.step.signum()) {
                "Cannot compare ranges with opposing step directions"
            }
        }

        // Core containment logic: must contain other's endpoints
        if (other.start !in this || other.end !in this) return false

        // If both stepped, check that other's step aligns with this range
        if (this.step != null && this.step.isNotZero() && other.step != null && other.step.isNotZero()) {
            return other.step.remainder(this.step).isZero()
        }

        // If either is unstepped, we’ve already confirmed bounds — that's enough
        return true
    }

    /**
     * Returns a lazily evaluated [Sequence] of values in this range, respecting the defined [step].
     *
     * Only safe for use when [step] is defined or the range is a singleton.
     *
     * @throws IllegalStateException if [step] is not defined for a non-singleton range.
     */
    fun sequence(): Sequence<BigInteger> = sequence {
        if (start == end) {
            yield(start)
            return@sequence
        }

        val s = step ?: throw IllegalStateException("Cannot iterate non-singleton range without a defined step")

        var current = start
        val ascending = s.signum() > 0

        while (if (ascending) current <= end else current >= end) {
            yield(current)
            current += s
        }
    }

    /**
     * Returns the number of values in this range as the most appropriate [Number] subtype:
     * - `Int` if the value fits in an `Int`
     * - `Long` if larger than `Int` but still within `Long`
     * - `BigInteger` otherwise
     *
     * @throws IllegalStateException if [step] is null and the range is not a singleton
     */
    fun count(): Number {
        val s = step ?: BigInteger.ONE
        val total = abs((end - start) / s) + BigInteger.ONE
        return toBestFitNumber(total)
    }

    /**
     * Returns a uniformly random [BigInteger] from the values in this range.
     *
     * @param random The [Random] instance to use (defaults to [SecureKotlinRandom.Instance]).
     * @return A randomly selected value from the range.
     */
    fun random(random: Random = SecureKotlinRandom.Instance): Number {
        if (span.isZero()) return start

        val s = step ?: BigInteger.ONE
        val steps = (end - start) / s
        val randomIndex = random(BigInteger.ZERO, steps, random = random)

        return start + s * randomIndex
    }

    /**
     * Returns a new [BigIntegerRange] but with an opposite direction.
     *
     * The start and end bounds are swapped and the step, if not null, is negated,
     * If the range is single-valued, the same instance is returned.
     *
     * @return A [BigIntegerRange] representing the same values of a range but in reverse order.
     */
    fun reversed(): BigIntegerRange {
        if (span.isZero()) return this

        return BigIntegerRange(
            start = end,
            end = start,
            step = step?.negate()
        )
    }

    /**
     * Compares two [BigInteger] step values that may be `null`.
     *
     * This function defines a comparison logic for nullable [BigInteger] values where:
     * - `null` is considered less than any non-null value.
     * - Two `null` values are considered equal.
     * - If both values are non-null, their natural ordering is used.
     *
     * @param a The first [BigInteger] value to compare, which may be `null`.
     * @param b The second [BigInteger] value to compare, which may be `null`.
     * @return -1 if [a] is less than [b], 1 if [a] is greater than [b], or 0 if they are equal.
     */
    private fun compareSteps(a: BigInteger?, b: BigInteger?): Int = when {
        a == null && b == null -> 0
        a == null -> -1
        b == null -> 1
        else -> a.compareTo(b)
    }

    /**
     * Converts a [BigInteger] to the most appropriate [Number] subtype.
     * - Returns `Int` if value fits in an `Int`
     * - Returns `Long` if value fits in a `Long`
     * - Returns `BigInteger` otherwise
     */
    private fun toBestFitNumber(value: BigInteger): Number = when {
        value <= BigInteger.valueOf(Int.MAX_VALUE.toLong()) -> value.toInt()
        value <= BigInteger.valueOf(Long.MAX_VALUE) -> value.toLong()
        else -> value
    }
}

/**
 * Creates a [BigIntegerRange] from this [BigInteger] to [that] (inclusive end).
 *
 * This is an operator overload for the `..` syntax, enabling idiomatic range expressions
 * like `BigInteger("1")..BigInteger("10")`.
 *
 * The resulting range:
 * - Starts at `this`
 * - Ends at `that`, inclusive
 *
 * Example:
 * ```
 * val range = BigInteger("3")..BigInteger("7")
 * println(range.toList()) // [3, 4, 5, 6, 7]
 * ```
 */
operator fun BigInteger.rangeTo(that: BigInteger): BigIntegerRange = BigIntegerRange(this, that)