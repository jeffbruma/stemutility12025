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

class Sedenion(
    val s0: Number = 0,
    val s1: Number = 0,
    val s2: Number = 0,
    val s3: Number = 0,
    val s4: Number = 0,
    val s5: Number = 0,
    val s6: Number = 0,
    val s7: Number = 0,
    val s8: Number = 0,
    val s9: Number = 0,
    val sA: Number = 0,
    val sB: Number = 0,
    val sC: Number = 0,
    val sD: Number = 0,
    val sE: Number = 0,
    val sF: Number = 0,
) : NormedAlgebra(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, sA, sB, sC, sD, sE, sF, accessory = Companion) {
    constructor(s: List<Number>) : this(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8], s[9], s[10], s[11], s[12], s[13], s[14], s[15])
    constructor(s: IntArray) : this(s.toList())
    constructor(s: Array<out Number>) : this(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8], s[9], s[10], s[11], s[12], s[13], s[14], s[15])
    constructor(scalar: Number, vector: Array<out Number>) : this(scalar, vector[0], vector[1], vector[2], vector[3], vector[4], vector[5], vector[6], vector[7], vector[8], vector[9], vector[10], vector[11], vector[12], vector[13], vector[14])
    constructor(init: (Int) -> Number) : this(
        (0 until Dimension).map { init(it) }
    )

    companion object : Accessory {
        override val Dimension = 16
        override val Basis = List(Dimension) { i -> if (i == 0) "" else "\u2147$i" }
        override val Zero = Sedenion()

        fun fromPolar(
            norm: Number,
            angle: Number,
            unitVector: Array<Number>
        ) = Sedenion(
            conjureRectangularCoordinates(norm, angle, unitVector)
        )

        fun fromPolar(polar: PolarForm) = Sedenion(
            conjureRectangularCoordinates(polar.norm, polar.angle, polar.unitVector)
        )

        fun random(componentRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = Sedenion { componentRange.random() }
        fun random(start: Double, end: Double) = Sedenion { Random.nextDouble(start, end) }
        fun random(list: List<Number>) = Sedenion { list.random() }
    }

    override val conjugate: Sedenion by lazy {
        Sedenion(component.mapIndexed { index, value ->
            if (index == 0) value else -value
        })
    }
    override val inverse: Sedenion by lazy {
        require(norm != 0) { "ZERO vector" }
        Sedenion(component.mapIndexed { index, value ->
            if (index == 0) value / sqr(norm) else -value / sqr(norm)
        })
    }
    override val normalized: Sedenion by lazy {
        if (norm == 0 || norm == 1) this
        else Sedenion(component.map { it / norm })
    }

    override fun unaryMinus() = Sedenion { -component[it] }
    
    override fun plus(other: Number) = this + other.toSedenion()
    override fun plus(other: Real) = this + other.toSedenion()
    override fun plus(other: Complex) = this + other.toSedenion()
    override fun plus(other: Quaternion) = this + other.toSedenion()
    override fun plus(other: Octonion) = this + other.toSedenion()
    override fun plus(other: Sedenion) = Sedenion { component[it] + other.component[it] }

    override fun minus(other: Number) = this - other.toSedenion()
    override fun minus(other: Real) = this - other.toSedenion()
    override fun minus(other: Complex) = this - other.toSedenion()
    override fun minus(other: Quaternion) = this - other.toSedenion()
    override fun minus(other: Octonion) = this - other.toSedenion()
    override fun minus(other: Sedenion) = Sedenion { component[it] - other.component[it] }

    override fun times(other: Number) = this * other.toSedenion()
    override fun times(other: Real) = this * other.toSedenion()
    override fun times(other: Complex) = this * other.toSedenion()
    override fun times(other: Quaternion) = this * other.toSedenion()
    override fun times(other: Octonion) = this * other.toSedenion()
    override fun times(other: Sedenion) = Sedenion(
        s0 * other.s0 - s1 * other.s1 - s2 * other.s2 - s3 * other.s3 - s4 * other.s4 - s5 * other.s5 - s6 * other.s6 - s7 * other.s7 - s8 * other.s8 - s9 * other.s9 - sA * other.sA - sB * other.sB - sC * other.sC - sD * other.sD - sE * other.sE - sF * other.sF,
        s0 * other.s1 + s1 * other.s0 + s2 * other.s3 - s3 * other.s2 + s4 * other.s5 - s5 * other.s4 - s6 * other.s7 + s7 * other.s6 + s8 * other.s9 - s9 * other.s8 - sA * other.sB + sB * other.sA - sC * other.sD + sD * other.sC + sE * other.sF - sF * other.sE,
        s0 * other.s2 - s1 * other.s3 + s2 * other.s0 + s3 * other.s1 + s4 * other.s6 + s5 * other.s7 - s6 * other.s4 - s7 * other.s5 + s8 * other.sA + s9 * other.sB - sA * other.s8 - sB * other.s9 - sC * other.sE - sD * other.sF + sE * other.sC + sF * other.sD,
        s0 * other.s3 + s1 * other.s2 - s2 * other.s1 + s3 * other.s0 + s4 * other.s7 - s5 * other.s6 + s6 * other.s5 - s7 * other.s4 + s8 * other.sB - s9 * other.sA + sA * other.s9 - sB * other.s8 - sC * other.sF + sD * other.sE - sE * other.sD + sF * other.sC,
        s0 * other.s4 - s1 * other.s5 - s2 * other.s6 - s3 * other.s7 + s4 * other.s0 + s5 * other.s1 + s6 * other.s2 + s7 * other.s3 + s8 * other.sC + s9 * other.sD + sA * other.sE + sB * other.sF - sC * other.s8 - sD * other.s9 - sE * other.sA - sF * other.sB,
        s0 * other.s5 + s1 * other.s4 - s2 * other.s7 + s3 * other.s6 - s4 * other.s1 + s5 * other.s0 - s6 * other.s3 + s7 * other.s2 + s8 * other.sD - s9 * other.sC + sA * other.sF - sB * other.sE + sC * other.s9 - sD * other.s8 + sE * other.sB - sF * other.sA,
        s0 * other.s6 + s1 * other.s7 + s2 * other.s4 - s3 * other.s5 - s4 * other.s2 + s5 * other.s3 + s6 * other.s0 - s7 * other.s1 + s8 * other.sE - s9 * other.sF - sA * other.sC + sB * other.sD + sC * other.sA - sD * other.sB - sE * other.s8 + sF * other.s9,
        s0 * other.s7 - s1 * other.s6 + s2 * other.s5 + s3 * other.s4 - s4 * other.s3 - s5 * other.s2 + s6 * other.s1 + s7 * other.s0 + s8 * other.sF + s9 * other.sE - sA * other.sD - sB * other.sC + sC * other.sB + sD * other.sA - sE * other.s9 - sF * other.s8,
        s0 * other.s8 - s1 * other.s9 - s2 * other.sA - s3 * other.sB - s4 * other.sC - s5 * other.sD - s6 * other.sE - s7 * other.sF + s8 * other.s0 + s9 * other.s1 + sA * other.s2 + sB * other.s3 + sC * other.s4 + sD * other.s5 + sE * other.s6 + sF * other.s7,
        s0 * other.s9 + s1 * other.s8 - s2 * other.sB + s3 * other.sA - s4 * other.sD + s5 * other.sC + s6 * other.sF - s7 * other.sE - s8 * other.s1 + s9 * other.s0 - sA * other.s3 + sB * other.s2 - sC * other.s5 + sD * other.s4 + sE * other.s7 - sF * other.s6,
        s0 * other.sA + s1 * other.sB + s2 * other.s8 - s3 * other.s9 - s4 * other.sE - s5 * other.sF + s6 * other.sC + s7 * other.sD - s8 * other.s2 + s9 * other.s3 + sA * other.s0 - sB * other.s1 - sC * other.s6 - sD * other.s7 + sE * other.s4 + sF * other.s5,
        s0 * other.sB - s1 * other.sA + s2 * other.s9 + s3 * other.s8 - s4 * other.sF + s5 * other.sE - s6 * other.sD + s7 * other.sC - s8 * other.s3 - s9 * other.s2 + sA * other.s1 + sB * other.s0 - sC * other.s7 + sD * other.s6 - sE * other.s5 + sF * other.s4,
        s0 * other.sC + s1 * other.sD + s2 * other.sE + s3 * other.sF + s4 * other.s8 - s5 * other.s9 - s6 * other.sA - s7 * other.sB - s8 * other.s4 + s9 * other.s5 + sA * other.s6 + sB * other.s7 + sC * other.s0 - sD * other.s1 - sE * other.s2 - sF * other.s3,
        s0 * other.sD - s1 * other.sC + s2 * other.sF - s3 * other.sE + s4 * other.s9 + s5 * other.s8 + s6 * other.sB - s7 * other.sA - s8 * other.s5 - s9 * other.s4 + sA * other.s7 - sB * other.s6 + sC * other.s1 + sD * other.s0 + sE * other.s3 - sF * other.s2,
        s0 * other.sE - s1 * other.sF - s2 * other.sC + s3 * other.sD + s4 * other.sA - s5 * other.sB + s6 * other.s8 + s7 * other.s9 - s8 * other.s6 - s9 * other.s7 - sA * other.s4 + sB * other.s5 + sC * other.s2 - sD * other.s3 + sE * other.s0 + sF * other.s1,
        s0 * other.sF + s1 * other.sE - s2 * other.sD - s3 * other.sC + s4 * other.sB + s5 * other.sA - s6 * other.s9 + s7 * other.s8 - s8 * other.s7 + s9 * other.s6 - sA * other.s5 - sB * other.s4 + sC * other.s3 + sD * other.s2 - sE * other.s1 + sF * other.s0,
    )

    override fun div(other: Number) = this * other.inverse

    /**
     * Sedenion divided by a Division Algebra:
     * S × D⁻¹
     */
    override fun div(other: Real) = this * other.inverse
    override fun div(other: Complex) = this * other.inverse
    override fun div(other: Quaternion) = this * other.inverse
    override fun div(other: Octonion) = this * other.inverse

//    override fun pow(exponent: Number): Sedenion {
//        if (exponent == 0) return 1.toSedenion()
//        val base = if (exponent < 0) inverse else this
//        val absExp = kotlin.math.abs(exponent.toDouble())
//        val polar = base.polarForm
//        return fromPolar(
//            polar.norm.pow(absExp),
//            polar.angle * absExp,
//            polar.unitVector
//        )
//    }

    override fun pow(exponent: Number): Sedenion {
        return if (exponent.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Real) = this.pow(exponent.r)
    override fun pow(exponent: Complex): Sedenion {
        return if (exponent.norm.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Quaternion): Sedenion {
        return if (exponent.norm.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Octonion): Sedenion {
        return if (exponent.norm.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Sedenion): Sedenion {
        return if (exponent.norm.isZero()) 1.toSedenion()
        else exp(exponent * log(this))
    }

    override fun root(degree: Int): Set<Sedenion> {
        require(degree > 1) { "Root degree must be an integer greater than one" }

        if (hasPositiveScalar() && isReal()) {
            val rootValue = s0.root(degree)
            return setOf(Sedenion { i -> if (i == 0) rootValue else 0})
        }

        if (component.drop(2).all { it.toDouble() == 0.0 }) {
            val polar = this.polarForm
            val newNorm = polar.norm.root(degree)
            val newArgument = polar.angle / degree
            return List(degree) { i ->
                fromPolar(
                    newNorm,
                    newArgument + 2 * PI * i / degree,
                    polar.unitVector
                )
            }.toSet()
        }

        throw InfiniteSetException(
            "${this::class.simpleName} nth roots are infinite for ${this::class.simpleName}s with non-zero vector parts. " +
                    "Restrict the ${this::class.simpleName} to a subalgebra isomorphic to the complex numbers to " +
                    "obtain a finite set."
        )
    }
}

fun exp(s: Sedenion): Sedenion {
    return if (s.isReal()) {
        Sedenion(s0 = exp(s.s0))
    } else {
        Sedenion(
            exp(s.s0) * (arrayOf(cos(s.vectorPartNorm)) + (sin(s.vectorPartNorm) / s.vectorPartNorm) * s.vectorPart)
        )
    }
}

fun log(s: Sedenion, branch: Int = 0): Sedenion {
    if (s.isZero()) throw ArithmeticException("Logarithm of zero is undefined")

    return if (s.isReal()) {
        if (s.hasPositiveScalar()) {
            Sedenion(s0 = log(s.norm))
        } else {
            Sedenion(s0 = log(s.norm), s1 = PI)
        }
    } else {
        Sedenion(
            scalar = log(s.norm), vector = (s.polarForm.angle + 2 * PI * branch) * s.polarForm.unitVector
        )
    }
}
fun log(s: Sedenion, base: Sedenion, sBranch: Int = 0, baseBranch: Int = 0) = log(s, sBranch) / log(base, baseBranch)