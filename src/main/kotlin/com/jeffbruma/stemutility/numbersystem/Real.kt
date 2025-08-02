package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.*
import com.jeffbruma.stemutility.numbersystem.miscellaneous.Accessory
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import kotlin.math.PI
import kotlin.random.Random

class Real(val r: Number = 0) : OrderedAlgebra(r, accessory = Companion), Comparable<Real> {
//    constructor(r: List<Number>) : this(r[0])
//    constructor(init: (Int) -> Number) : this(
//        (0 until Dimension).map { init(it) }
//    )

    companion object : Accessory {
        override val Dimension = 1
        override val Basis = listOf("")
        override val Zero = Real()

        fun random(componentRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = Real(componentRange.random())
        fun random(start: Double, end: Double) = Real(Random.nextDouble(start, end))
        fun random(list: List<Number>) = Real(list.random())
    }

    override val conjugate: Real by lazy { this }
    override val inverse: Real by lazy {
        require(!norm.isZero()) { "Norm cannot be zero" }
        Real(r.inverse)
    }
    override val normalized: Real by lazy { this }
    override val polarForm: PolarForm by lazy {
        PolarForm(
            norm = norm,
            angle = if (r >= 0) 0 else PI
        )
    }

    override fun unaryMinus() = Real(-r)

    override fun plus(other: Number) = this + other.toReal()
    override fun plus(other: Real) = Real(r + other.r)
    override fun plus(other: Complex) = this.toComplex() + other
    override fun plus(other: Quaternion) = this.toQuaternion() + other
    override fun plus(other: Octonion) = this.toOctonion() + other
    override fun plus(other: Sedenion) = this.toSedenion() + other

    override fun minus(other: Number) = this - other.toReal()
    override fun minus(other: Real) = Real(r - other.r)
    override fun minus(other: Complex) = this.toComplex() - other
    override fun minus(other: Quaternion) = this.toQuaternion() - other
    override fun minus(other: Octonion) = this.toOctonion() - other
    override fun minus(other: Sedenion) = this.toSedenion() - other

    override fun times(other: Number) = this * other.toReal()
    override fun times(other: Real) = Real(r * other.r)
    override fun times(other: Complex) = this.toComplex() * other
    override fun times(other: Quaternion) = this.toQuaternion() * other
    override fun times(other: Octonion) = this.toOctonion() * other
    override fun times(other: Sedenion) = this.toSedenion() * other

    override fun div(other: Number) = this * other.inverse
    override fun div(other: Real) = this * other.inverse
    override fun div(other: Complex) = this * other.inverse
    override fun div(other: Quaternion) = this * other.inverse
    override fun div(other: Octonion) = this * other.inverse

    override fun pow(exponent: Number) = Real(r.pow(exponent))
    override fun pow(exponent: Real) = Real(r.pow(exponent.r))
    override fun pow(exponent: Complex) = this.toComplex().pow(exponent)
    override fun pow(exponent: Quaternion) = this.toQuaternion().pow(exponent)
    override fun pow(exponent: Octonion) = this.toOctonion().pow(exponent)
    override fun pow(exponent: Sedenion) = this.toSedenion().pow(exponent)

    override fun root(degree: Int): Set<CommutativeAlgebra> {
        require(degree > 1) { "Degree should be an integer greater than one" }
        return if (degree % 2 == 0) {
            if (r >= 0) {
                println("Degree is even")
                val rooted = r.root(degree)
                setOf(Real(rooted), Real(-rooted))
            } else {
                val c = this.toComplex()
                c.root(degree)
            }
        } else {
            val root = this.toComplex().root(degree).filter { root ->
                (root.b == 0)
            }.toSet()
            root.map { Real(it.a) }.toSet()
        }
    }

    override operator fun compareTo(other: Real) = r.compareTo(other.r)

    operator fun rangeTo(other: Real): ClosedRange<Real> {
        return object : ClosedRange<Real> {
            override val start: Real = this@Real.coerceAtMost(other)
            override val endInclusive: Real = this@Real.coerceAtLeast(other)

            override fun contains(value: Real): Boolean {
                return value >= start && value <= endInclusive
            }

            override fun isEmpty(): Boolean {
                return start > endInclusive
            }
        }
    }
}

fun exp(r: Real) = Real(exp(r.r))
fun log(r: Real): CommutativeAlgebra {
    if (r.isZero()) throw ArithmeticException("Logarithm of 0 to any base is undefined")
    return if (r > Real(0)) Real(log(r.r)) else log(r.toComplex())
}
fun log(r: Real, b: Real): CommutativeAlgebra {
    if (b.r.toDouble() == 1.0) throw ArithmeticException("Logarithm of any number to base 1 is undefined")
    return if (r > Real(0) && b > Real(0)) Real(log(r.r, b.r)) else {
        log(r.toComplex()) / log(b.toComplex())
    }
}
fun sinh(r: Real) = Real(sinh(r.r))
fun cosh(r: Real) = Real(cosh(r.r))
fun tanh(r: Real) = Real(tanh(r.r))
fun asinh(r: Real) = Real(arsinh(r.r))
fun acosh(r: Real): Set<CommutativeAlgebra> {
    return if (r >= Real(1)) setOf(Real(arcosh(r.r))) else {
        acosh(r.toComplex())
    }
}
fun atanh(r: Real): CommutativeAlgebra {
    return if (r in Real(-1)..Real(1)) Real(artanh(r.r)) else {
        atanh(r.toComplex())
    }
}
fun sin(r: Real) = Real(sin(r.r))
fun cos(r: Real) = Real(cos(r.r))
fun tan(r: Real) = Real(tan(r.r))
fun asin(r: Real) = Real(asin(r.r))
fun acos(r: Real): Set<CommutativeAlgebra> {
    return if (r in Real(-1)..Real(1)) setOf(Real(acos(r.r))) else {
        acos(r.toComplex())
    }
}
fun atan(r: Real) = Real(atan(r.r))
fun atan2(opp: Real, adj: Real) = Real(atan2(opp.r, adj.r))
