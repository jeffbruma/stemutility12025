package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.*
import com.jeffbruma.stemutility.numbersystem.miscellaneous.Accessory
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import kotlin.math.PI
import kotlin.random.Random

class Quaternion(
    val w: Number = 0,
    val x: Number = 0,
    val y: Number = 0,
    val z: Number = 0
) : AssociativeAlgebra(w, x, y, z, accessory = Companion) {
    constructor(h: List<Number>) : this(h[0], h[1], h[2], h[3])
    constructor(h: Array<out Number>) : this(h[0], h[1], h[2], h[3])
    constructor(scalar: Number, vector: Array<out Number>) : this(scalar, vector[0], vector[1], vector[2])
    constructor(init: (Int) -> Number) : this(
        (0 until Dimension).map { init(it) }
    )

    companion object : Accessory {
        override val Dimension = 4
        override val Basis = listOf("", "\uD835\uDC22", "\uD835\uDC23", "\uD835\uDC24") // bold
            //listOf("", "\uD835\uDD5A", "\uD835\uDD5B", "\uD835\uDD5C"), // double-struck / blackboard bold
            //listOf("", "\uD835\uDC56", "\uD835\uDC57", "\uD835\uDC56"), // italics
            //listOf("", "i\u0302", "j\u0302", "k\u0302"), // hats / circumflex
        override val Zero = Quaternion()

        fun fromPolar(
            norm: Number,
            angle: Number,
            unitVector: Array<Number>
        ) = Quaternion(
            conjureRectangularCoordinates(norm, angle, unitVector)
        )

        fun fromPolar(polar: PolarForm) = Quaternion(
            conjureRectangularCoordinates(polar.norm, polar.angle, polar.unitVector)
        )

        fun random(componentRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = Quaternion { componentRange.random() }
        fun random(start: Double, end: Double) = Quaternion { Random.nextDouble(start, end) }
        fun random(list: List<Number>) = Quaternion { list.random() }
    }

    override val conjugate: Quaternion by lazy {
        Quaternion(component.mapIndexed { index, value ->
            if (index == 0) value else -value
        })
    }

    /**
     * The reciprocal
     */
    override val inverse: Quaternion by lazy {
        require(!isZero()) { "Quaternion has zero norm" }
        Quaternion(component.mapIndexed { index, value ->
            if (index == 0) value / sqr(norm) else -value / sqr(norm)
        })
    }

    /**
     * The versor
     */
    override val normalized: Quaternion by lazy {
        if (norm == 0 || norm == 1) this
        else this / norm
    }

    override fun unaryMinus() = Quaternion { -component[it] }

    override fun plus(other: Number) = this + other.toQuaternion()
    override fun plus(other: Real) = this + other.toQuaternion()
    override fun plus(other: Complex) = this + other.toQuaternion()
    override fun plus(other: Quaternion) = Quaternion(
        w + other.w,
        x + other.x,
        y + other.y,
        z + other.z
    )

    override fun plus(other: Octonion) = this.toOctonion() + other
    override fun plus(other: Sedenion) = this.toSedenion() + other

    override fun minus(other: Number) = this - other.toQuaternion()
    override fun minus(other: Real) = this - other.toQuaternion()
    override fun minus(other: Complex) = this - other.toQuaternion()
    override fun minus(other: Quaternion) = Quaternion(
        w - other.w,
        x - other.x,
        y - other.y,
        z - other.z
    )

    override fun minus(other: Octonion) = this.toOctonion() - other
    override fun minus(other: Sedenion) = this.toSedenion() - other

    override fun times(other: Number) = this * other.toQuaternion()
    override fun times(other: Real) = this * other.toQuaternion()
    override fun times(other: Complex) = this * other.toQuaternion()
    override fun times(other: Quaternion) = Quaternion(
        w * other.w - x * other.x - y * other.y - z * other.z,
        w * other.x + x * other.w + y * other.z - z * other.y,
        w * other.y - x * other.z + y * other.w + z * other.x,
        w * other.z + x * other.y - y * other.x + z * other.w
    )

    override fun times(other: Octonion) = this.toOctonion() * other
    override fun times(other: Sedenion) = this.toSedenion() * other

    override fun div(other: Number) = this * other.inverse
    override fun div(other: Real) = this * other.inverse
    override fun div(other: Complex) = this * other.inverse
    override fun div(other: Quaternion) = this * other.inverse
    override fun div(other: Octonion) = this * other.inverse

    //    override fun pow(exponent: Number): Quaternion {
//        if (exponent == 0) return 1.toQuaternion()
//        val base = if (exponent < 0) inverse else this
//        val absExp = kotlin.math.abs(exponent.toDouble())
//        val polar = base.polarForm
//        return fromPolar(
//            polar.norm.pow(absExp),
//            polar.angle * absExp,
//            polar.unitVector
//        )
//    }
    override fun pow(exponent: Number): Quaternion {
        return if (exponent.isZero()) return 1.toQuaternion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Real) = this.pow(exponent.r)
    override fun pow(exponent: Complex): Quaternion {
        return if (exponent.norm.isZero()) return 1.toQuaternion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Quaternion): Quaternion {
        return if (exponent.norm.isZero()) return 1.toQuaternion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Octonion): Octonion {
        return if (exponent.norm.isZero()) return 1.toOctonion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Sedenion): Sedenion {
        return if (exponent.norm.isZero()) return 1.toSedenion()
        else exp(exponent * log(this))
    }

    override fun root(degree: Int): Set<Quaternion> {
        require(degree > 1) { "Root degree must be an integer greater than one" }

        // Check for the ambiguous case: root of a non-zero real quaternion (degree > 1)
        if (isReal()) { // If the quaternion is a non-zero real number
            if (degree == 2 && hasNegativeScalar()) {
                // Infinite roots: Square root (degree 2) of a *negative* non-zero real
                throw InfiniteSetException(
                    "The square root of a negative non-zero real quaternion is an in infinite set of non-real roots"
                )
            } else if (degree > 2) {
                // Infinite roots: nth root (degree > 2) of *any* non-zero real
                throw InfiniteSetException(
                    "Taking root $degree of a non-zero real quaternion results in infinite set of non-real roots"
                )
            }
        }

        // Handle the root of zero quaternions
        if (isZero()) return setOf(Quaternion { 0 })

        val newNorm = norm.root(degree)
        val principalAngle = polarForm.angle / degree // This is the angle in [0, pi]
        val roots = mutableSetOf<Quaternion>()

        for (i in 0 until degree) {
            val newScalarPart = newNorm * cos(principalAngle + (2 * PI * i) / degree)
            val newVectorPartMagnitude = newNorm * sin(principalAngle + (2 * PI * i) / degree)

            if (isReal()) {
                roots.add(
                    Quaternion(w = newScalarPart)
                )
            } else {
                val newVectorPart = polarForm.unitVector * newVectorPartMagnitude
                roots.add(
                    Quaternion(newScalarPart, newVectorPart)
                )
            }
        }

        return roots
    }

    fun distance(other: Quaternion) = (this - other).norm

    /**
     *
     * Calculates geodesic norm of two quaternions.
     * Quaternions are normalized prior to the calculation.
     *
     * */
    fun geodesicDistance(other: Quaternion): Number { // I don't understand what this is for. Yet.
        require(!isZero() && !other.isZero()) { "At least one of the quaternions has zero norm" }
        return (log(inverse.normalized * other.normalized)).norm
    }
}

val Number.j: Quaternion
    get() = Quaternion(0, 0, this, 0)
val Number.k: Quaternion
    get() = Quaternion(0, 0, 0, this)

fun Pair<Complex, Complex>.toQuaternion() = Quaternion(first.a, first.b, second.a, second.b)

fun exp(h: Quaternion): Quaternion {
    return if (h.isReal()) {
        Quaternion(w = exp(h.w))
    } else {
        Quaternion(
            exp(h.w) * (arrayOf(cos(h.vectorPartNorm)) + (sin(h.vectorPartNorm) / h.vectorPartNorm) * h.vectorPart)
        )
    }
}

fun log(h: Quaternion, branch: Int = 0): Quaternion {
    if (h.isZero()) throw ArithmeticException("Logarithm of zero is undefined")

    return if (h.isReal()) {
        if (h.hasPositiveScalar()) {
            Quaternion(w = log(h.norm))
        } else {
            Quaternion(w = log(h.norm), x = PI)
        }
    } else {
        Quaternion(
            scalar = log(h.norm), vector = (h.polarForm.angle + 2 * PI * branch) * h.polarForm.unitVector
        )
    }
}

fun log(h: Quaternion, base: Quaternion, hBranch: Int = 0, baseBranch: Int = 0) =
    log(h, hBranch) / log(base, baseBranch)

fun sinh(h: Quaternion) = (exp(h) - exp(-h)) / 2
fun cosh(h: Quaternion) = (exp(h) + exp(-h)) / 2
fun tanh(h: Quaternion) = sinh(h) / cosh(h) //(exp(h * 2) - 1) / (exp(h * 2) + 1)
fun acosh(h: Quaternion): Set<Quaternion> {
    // asinh(h) = log(h + sqrt(h^2 - 1))
    // Get the set of square roots of (h^2 - 1).
    // This call can throw AmbiguousResultException if (h^2 - 1) is a negative real.
    val sqrtResults: Set<Quaternion>

    try {
        sqrtResults = (h * h - 1).root(2)
    } catch (e: InfiniteSetException) {
        // If sqrt(h^2 - 1) is already ambiguous (infinite), the acosh will also be ambiguous.
        throw InfiniteSetException("Ambiguous result during square root calculation in acosh.", e)
    }

    val finalResults = mutableSetOf<Quaternion>()

    // The formula involves Log(h + sqrt_value) and Log(h - sqrt_value)
    // We need to consider both branches of the sqrt result (r and -r if sqrt returns +/-)
    // and the two terms in the acosh formula (h + sqrt and h - sqrt)

    for (sqrtValue in sqrtResults) {
        val arg1 = h + sqrtValue
        val arg2 = h - sqrtValue

        // Check for ambiguity for arg1: Log(arg1) is ambiguous if arg1 is a non-zero real.
        if (arg1.isReal()) {
            throw InfiniteSetException("Argument (h + sqrt(h^2 - 1)) to logarithm in acosh is a non-zero real, resulting in infinite acosh values.")
        }
        // Check for ambiguity for arg2: Log(arg2) is ambiguous if arg2 is a non-zero real.
        if (arg2.isReal()) {
            throw InfiniteSetException("Argument (h - sqrt(h^2 - 1)) to logarithm in acosh is a non-zero real, resulting in infinite acosh values.")
        }

        // If arguments are not ambiguous (not non-zero real) and not zero, take the principal log.
        if (!arg1.isZero()) {
            try {
                finalResults.add(log(arg1))
            } catch (e: ArithmeticException) {
                // principalLog throws for zero argument. This case should ideally be handled
                // as undefined acosh, or potentially part of the infinite set depending on definition.
                // For finite set approach, throwing is safer if principalLog(0) is undefined.
                throw e // ArithmeticException("Argument (h + sqrt(h^2 - 1)) is zero, acosh undefined.")
            }
        }

        if (!arg2.isZero()) {
            // Ensure the same principal log result is not added twice if arg1 == arg2
            if (arg1 != arg2) {
                try {
                    finalResults.add(log(arg2))
                } catch (e: ArithmeticException) {
                    throw e // ArithmeticException("Argument (h - sqrt(h^2 - 1)) is zero, acosh undefined.")
                }
            }
        }
    }

    return finalResults
}

fun asinh(h: Quaternion): Set<Quaternion> {
    // asinh(h) = log(h + sqrt(h^2 + 1))
    // Get the set of square roots of (h^2 + 1).
    // This call can throw AmbiguousResultException if (h^2 + 1) is a negative real number.
    val sqrtResults: Set<Quaternion>

    try {
        sqrtResults = (h * h + 1).root(2)
    } catch (e: InfiniteSetException) {
        // If sqrt(q^2 + 1) is already ambiguous (infinite), the asinh will also be ambiguous.
        throw InfiniteSetException("Ambiguous result during square root calculation in asinh.", e)
    }

    val finalResults = mutableSetOf<Quaternion>()

    // The formula involves Log(q + sqrt_value) for each sqrt_value
    for (sqrtValue in sqrtResults) {
        val arg = h + sqrtValue

        // Check for ambiguity for arg: Log(arg) is ambiguous if arg is a non-zero real.
        if (arg.isReal()) {
            throw InfiniteSetException("Argument (q + sqrt(q^2+1)) to logarithm in asinh is a non-zero real, resulting in infinite asinh values.")
        }

        // If the argument is not ambiguous and not zero, take the principal log.
        if (!arg.isZero()) {
            try {
                finalResults.add(log(arg))
            } catch (e: ArithmeticException) {
                // principalLog throws for zero argument.
                throw e // ArithmeticException("Argument (q + sqrt(q^2+1)) is zero, asinh undefined.")
            }
        }
    }

    return finalResults
}

fun atanh(h: Quaternion): Set<Quaternion> {
    // Formula: atanh(q) = 0.5 * Log((1+q)(1-q)^(-1))

    // Handle q = 1: (1-q) is zero, inverse is undefined. atanh(1) is undefined/infinite.
    if ((1 - h).isZero()) {
        // This is a point where atanh is undefined.
        throw ArithmeticException("atanh(1) is undefined.")
    }

    // Calculate the argument for the logarithm: (1+q)(1-q)^(-1)
    val arg = (1 + h) / (1 - h)

    // Check for ambiguity for arg: Log(arg) is ambiguous if arg is a non-zero real.
    if (arg.isReal()) {
        throw InfiniteSetException("Argument to logarithm in atanh is a non-zero real, resulting in infinite atanh values.")
    }

    // Handle arg = 0: (1+q) is zero, which means q = -1. Log(0) is undefined. atanh(-1) is undefined.
    if (arg.isZero()) {
        // This is a point where atanh is undefined.
        throw ArithmeticException("atanh(-1) is undefined.")
    }

    // If the argument is not ambiguous and not zero, take the principal log.
    val principalLogArg: Quaternion

    try {
        principalLogArg = log(arg)
    } catch (e: ArithmeticException) {
        // Should not happen if arg is not zero, but as a safeguard.
        throw e //ArithmeticException("Unexpected error taking principal log in atanh.")
    }

    val finalResults = mutableSetOf<Quaternion>()
    // The atanh formula includes the 0.5 factor
    finalResults.add(0.5 * principalLogArg) // Multiply scalar 0.5 by the quaternion

    return finalResults
}

fun sin(h: Quaternion) = Quaternion(
    sin(h.w) * cosh(h.vectorPartNorm) + h.vectorPart * (cos(h.w) * sinh(h.vectorPartNorm) / h.vectorPartNorm)
)

fun cos(h: Quaternion) = Quaternion(
    cos(h.w) * cosh(h.vectorPartNorm) + h.vectorPart * (sin(h.w) * sinh(h.vectorPartNorm) / h.vectorPartNorm)
)

fun tan(h: Quaternion) = sin(h) / cos(h)
fun asin(h: Quaternion): Set<Quaternion> {
    val negI = Quaternion(x = -1)
    val roots = (1 - h * h).root(2)
    val asinResult = mutableSetOf<Quaternion>()
    for (root in roots) {
        asinResult.add(negI * log(-negI * h + root))
    }
    return asinResult
}

fun acos(h: Quaternion): Set<Quaternion> {
    val negI = Quaternion(x = -1)
    val roots = (1 - h * h).root(2)
    val acosResult = mutableSetOf<Quaternion>()
    for (root in roots) {
        acosResult.add(negI * log(h + -negI * root))
    }
    return acosResult
}

fun atan(h: Quaternion): Quaternion {
    val i = Quaternion(x = 1)
    return 0.5 * i * (log(1 - i * h) - log(1 + i * h))
}
