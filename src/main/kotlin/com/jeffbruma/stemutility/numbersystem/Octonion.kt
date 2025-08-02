package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.cos
import com.jeffbruma.stemutility.miscellaneous.div
import com.jeffbruma.stemutility.miscellaneous.exp
import com.jeffbruma.stemutility.miscellaneous.isZero
import com.jeffbruma.stemutility.miscellaneous.log
import com.jeffbruma.stemutility.miscellaneous.minus
import com.jeffbruma.stemutility.miscellaneous.plus
import com.jeffbruma.stemutility.miscellaneous.root
import com.jeffbruma.stemutility.miscellaneous.sin
import com.jeffbruma.stemutility.miscellaneous.sqr
import com.jeffbruma.stemutility.miscellaneous.times
import com.jeffbruma.stemutility.miscellaneous.unaryMinus
import com.jeffbruma.stemutility.numbersystem.miscellaneous.Accessory
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import kotlin.math.PI
import kotlin.random.Random

class Octonion(
    val o0: Number = 0,
    val o1: Number = 0,
    val o2: Number = 0,
    val o3: Number = 0,
    val o4: Number = 0,
    val o5: Number = 0,
    val o6: Number = 0,
    val o7: Number = 0,
) : DivisionAlgebra(o0, o1, o2, o3, o4, o5, o6, o7, accessory = Companion) {
    constructor(o: List<Number>) : this(o[0], o[1], o[2], o[3], o[4], o[5], o[6], o[7])
    constructor(o: IntArray) : this(o[0], o[1], o[2], o[3], o[4], o[5], o[6], o[7])
    constructor(o: Array<out Number>) : this(o[0], o[1], o[2], o[3], o[4], o[5], o[6], o[7])
    constructor(scalar: Number, vector: Array<out Number>) : this(scalar, vector[0], vector[1], vector[2], vector[3], vector[4], vector[5], vector[6])
    constructor(init: (Int) -> Number) : this(
        (0 until Dimension).map { init(it) }
    )

    companion object : Accessory {
        override val Dimension = 8
        override val Basis = List(Dimension) { i -> if (i == 0) "" else "\u2147$i" }
        override val Zero = Octonion()

        fun fromPolar(
            norm: Number,
            angle: Number,
            unitVector: Array<Number>
        ) = Octonion(
            conjureRectangularCoordinates(norm, angle, unitVector)
        )

        fun fromPolar(polar: PolarForm) = Octonion(
            conjureRectangularCoordinates(polar.norm, polar.angle, polar.unitVector)
        )

        fun random(componentRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = Octonion { componentRange.random() }
        fun random(start: Double, end: Double) = Octonion { Random.nextDouble(start, end) }
        fun random(list: List<Number>) = Octonion { list.random() }
    }

    override val conjugate: Octonion by lazy {
        Octonion(component.mapIndexed { index, value ->
            if (index == 0) value else -value
        })
    }
    override val inverse: Octonion by lazy {
        require(norm != 0) { "Octonion norm is zero" }
        Octonion(component.mapIndexed { index, value ->
            if (index == 0) value / sqr(norm) else -value / sqr(norm)
        })
    }
    override val normalized: Octonion by lazy {
        if (norm == 0 || norm == 1) this
        else this / norm
    }

    override fun unaryMinus() = Octonion { -component[it] }
    override fun plus(other: Number) = this + other.toOctonion()
    override fun plus(other: Real) = this + other.toOctonion()
    override fun plus(other: Complex) = this + other.toOctonion()
    override fun plus(other: Quaternion) = this + other.toOctonion()
    override fun plus(other: Octonion) = Octonion { component[it] + other.component[it] }
    override fun plus(other: Sedenion) = this.toSedenion() + other

    override fun minus(other: Number) = this - other.toOctonion()
    override fun minus(other: Real) = this - other.toOctonion()
    override fun minus(other: Complex) = this - other.toOctonion()
    override fun minus(other: Quaternion) = this - other.toOctonion()
    override fun minus(other: Octonion) = Octonion { component[it] - other.component[it] }
    override fun minus(other: Sedenion) = this.toSedenion() - other

    override fun times(other: Number) = this * other.toOctonion()
    override fun times(other: Real) = this * other.toOctonion()
    override fun times(other: Complex) = this * other.toOctonion()
    override fun times(other: Quaternion) = this * other.toOctonion()
    override fun times(other: Octonion) = Octonion(
        o0 * other.o0 - o1 * other.o1 - o2 * other.o2 - o3 * other.o3 - o4 * other.o4 - o5 * other.o5 - o6 * other.o6 - o7 * other.o7,
        o0 * other.o1 + o1 * other.o0 + o2 * other.o3 - o3 * other.o2 + o4 * other.o5 - o5 * other.o4 - o6 * other.o7 + o7 * other.o6,
        o0 * other.o2 - o1 * other.o3 + o2 * other.o0 + o3 * other.o1 + o4 * other.o6 + o5 * other.o7 - o6 * other.o4 - o7 * other.o5,
        o0 * other.o3 + o1 * other.o2 - o2 * other.o1 + o3 * other.o0 + o4 * other.o7 - o5 * other.o6 + o6 * other.o5 - o7 * other.o4,
        o0 * other.o4 - o1 * other.o5 - o2 * other.o6 - o3 * other.o7 + o4 * other.o0 + o5 * other.o1 + o6 * other.o2 + o7 * other.o3,
        o0 * other.o5 + o1 * other.o4 - o2 * other.o7 + o3 * other.o6 - o4 * other.o1 + o5 * other.o0 - o6 * other.o3 + o7 * other.o2,
        o0 * other.o6 + o1 * other.o7 + o2 * other.o4 - o3 * other.o5 - o4 * other.o2 + o5 * other.o3 + o6 * other.o0 - o7 * other.o1,
        o0 * other.o7 - o1 * other.o6 + o2 * other.o5 + o3 * other.o4 - o4 * other.o3 - o5 * other.o2 + o6 * other.o1 + o7 * other.o0
    )
    override fun times(other: Sedenion) = this.toSedenion() * other

    override fun div(other: Number) = this * other.inverse
    override fun div(other: Real) = this * other.inverse
    override fun div(other: Complex) = this * other.inverse
    override fun div(other: Quaternion) = this * other.inverse
    override fun div(other: Octonion) = this * other.inverse

//    override fun pow(exponent: Number): Octonion {
//        if (exponent == 0) return 1.toOctonion()
//        val base = if (exponent < 0) inverse else this
//        val absExp = kotlin.math.abs(exponent.toDouble())
//        val polar = base.polarForm
//        return fromPolar(
//            polar.norm.pow(absExp),
//            polar.angle * absExp,
//            polar.unitVector
//        )
//    }

    override fun pow(exponent: Number): Octonion {
        return if (exponent.isZero()) 1.toOctonion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Real) = this.pow(exponent.r)
    override fun pow(exponent: Complex): Octonion {
        return if (exponent.norm.isZero()) 1.toOctonion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Quaternion): Octonion {
        return if (exponent.norm.isZero()) 1.toOctonion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Octonion): Octonion {
        return if (exponent.norm.isZero()) 1.toOctonion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Sedenion): Sedenion {
        return if (exponent.norm.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }



    override fun root(degree: Int): Set<Octonion> {
        // 1. Validate that the root degree is positive
        require(degree > 1) { "Root degree must be an integer greater than 1" }

        // 2. Handle the root of the zero octonion
        if (isZero()) return setOf(Octonion())

        // 3. Handle ambiguous cases for non-zero real octonions (vector part is zero)
        if (isReal()) throw InfiniteSetException(
            "Any root of a non-zero real octonion is an infinite set of non-real roots"
        )

        // 4. Handle the general case for all other octonions:
        //    - Non-real octonions (vector part is non-zero)
        //    - Non-ambiguous real octonions (positive real with degree 2, or any non-zero real with degree 1)
        // Calculate the nth root of the norm
        val newNorm = norm.root(degree)
        val roots = mutableSetOf<Octonion>()

        // The loop generates the 'degree' distinct roots using the polar form formula
        for (k in 0 until degree) {
            // Calculate the angle for the current root
            val currentAngle = (polarForm.angle + (2 * PI * k)) / degree

            // Calculate the scalar part of the root using the new norm and the current angle
            val newScalarPart = newNorm * cos(currentAngle)

            // Calculate the magnitude of the vector part of the root
            val newVectorPartMagnitude = newNorm * sin(currentAngle)

            // Construct the 7-dimensional vector part of the root
            val newVectorPartComponents: Array<out Number> = if (isReal()) {
                // If the original octonion was real, the only finite roots are real roots.
                // In the non-ambiguous real cases handled here (pos real degree 2, any real degree 1),
                // sin(currentAngle) will be zero for the real roots to find.
                // So the vector part is zero.
                Array(7) { 0 }
            } else {
                // If the original octonion had a non-zero vector part, the direction
                // is preserved, and the magnitude is calculated above.
                // Scale the original unit vector by the new vector part magnitude.
                polarForm.unitVector * newVectorPartMagnitude
            }
            roots.add(Octonion(newScalarPart, newVectorPartComponents))
        }
        return roots
    }

    fun commutator(other: Octonion) = this * other - other * this
}

fun exp(o: Octonion): Octonion {
    return if (o.isReal()) {
        Octonion(o0 = exp(o.o0))
    } else {
        Octonion(
            exp(o.o0) * (arrayOf(cos(o.vectorPartNorm)) + (sin(o.vectorPartNorm) / o.vectorPartNorm) * o.vectorPart)
        )
    }
}

fun log(o: Octonion, branch: Int = 0): Octonion {
    if (o.isZero()) throw ArithmeticException("Logarithm of zero is undefined")

    return if (o.isReal()) {
        if (o.hasPositiveScalar()) {
            Octonion(o0 = log(o.norm))
        } else {
            Octonion(o0 = log(o.norm), o1 = PI)
        }
    } else {
        Octonion(
            scalar = log(o.norm), vector = (o.polarForm.angle + 2 * PI * branch) * o.polarForm.unitVector
        )
    }
}
fun log(o: Octonion, base: Octonion, oBranch: Int = 0, baseBranch: Int = 0) = log(o, oBranch) / log(base, baseBranch)