package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.*
import com.jeffbruma.stemutility.numbersystem.miscellaneous.Accessory
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import kotlin.math.PI
import kotlin.random.Random

class Complex(
    val a: Number = 0,
    val b: Number = 0
) : CommutativeAlgebra(a, b, accessory = Companion) {
    constructor(c: List<Number>) : this(c[0], c[1])
    constructor(c: IntArray) : this(c.toList())
    constructor(c: DoubleArray) : this(c.toList())
    constructor(init: (Int) -> Number) : this(
        (0 until Dimension).map { init(it) }
    )

    companion object : Accessory {
        override val Dimension = 2
        override val Basis = listOf("", "\uD835\uDC56")
        override val Zero = Complex()

        fun fromPolar(
            norm: Number,
            angle: Number
        ) = Complex(
            conjureRectangularCoordinates(norm, angle, arrayOf(1))
        )

        fun fromPolar(polar: PolarForm) = Complex(
            conjureRectangularCoordinates(polar.norm, polar.angle, polar.unitVector)
        )

        fun random(componentRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = Complex { componentRange.random() }
        fun random(start: Double, end: Double) = Complex { Random.nextDouble(start, end) }
        fun random(list: List<Number>) = Complex { list.random() }
    }

    override val conjugate: Complex by lazy {
        Complex(component.mapIndexed { index, value ->
            if (index == 0) value else -value
        })
    }
    override val inverse: Complex by lazy {
        require(!norm.isZero()) { "Complex has zero norm" }
        Complex(component.mapIndexed { index, value ->
            if (index == 0) value / sqr(norm) else -value / sqr(norm)
        })
    }
    override val normalized: Complex by lazy {
        if (norm.isZero() || norm.toDouble() == 1.0) this
        else this / norm
    }
    override val polarForm: PolarForm by lazy {
        PolarForm(
            norm = norm,
            angle = atan2(b, a),
            unitVector = arrayOf(1)
        )
    }

    override fun unaryMinus() = Complex { -component[it] }
    override fun plus(other: Number) = this + other.toComplex()
    override fun plus(other: Real) = this + other.toComplex()
    override fun plus(other: Complex) = Complex(a + other.a, b + other.b)
    override fun plus(other: Quaternion) = this.toQuaternion() + other
    override fun plus(other: Octonion) = this.toOctonion() + other
    override fun plus(other: Sedenion) = this.toSedenion() + other
    override fun minus(other: Number) = this - other.toComplex()
    override fun minus(other: Real) = this - other.toComplex()
    override fun minus(other: Complex) = Complex(a - other.a, b - other.b)
    override fun minus(other: Quaternion) = this.toQuaternion() - other
    override fun minus(other: Octonion) = this.toOctonion() - other
    override fun minus(other: Sedenion) = this.toSedenion() - other
    override fun times(other: Number) = this * other.toComplex()
    override fun times(other: Real) = this * other.toComplex()
    override fun times(other: Complex) = Complex(
        a * other.a - b * other.b,
        a * other.b + b * other.a
    )

    override fun times(other: Quaternion) = this.toQuaternion() * other
    override fun times(other: Octonion) = this.toOctonion() * other
    override fun times(other: Sedenion) = this.toSedenion() * other
    override fun div(other: Number) = this * other.inverse
    override fun div(other: Real) = this * other.inverse
    override fun div(other: Complex) = this * other.inverse
    override fun div(other: Quaternion) = this * other.inverse
    override fun div(other: Octonion) = this * other.inverse

    //    override fun pow(exponent: Number): Complex {
//        if (exponent == 0) return 1.toComplex()
//        val base = if (exponent < 0) inverse else this
//        val absExp = kotlin.math.abs(exponent.toDouble())
//        val polar = base.polarForm
//        return fromPolar(
//            polar.norm.pow(absExp),
//            polar.angle * absExp
//        )
//    }
    override fun pow(exponent: Number): Complex {
        return if (exponent.isZero()) 1.toComplex()
        else exp(exponent * log(this))
    }
    override fun pow(exponent: Real) = this.pow(exponent.r)
    override fun pow(exponent: Complex): Complex {
        return if (exponent.norm.isZero()) 1.toComplex()
        else exp(exponent * log(this))
    }

    override fun pow(exponent: Quaternion): Quaternion {
        return if (exponent.norm.isZero()) 1.toQuaternion()
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

    override fun root(degree: Int): Set<Complex> {
        require(degree > 1) { "Degree should be an integer greater than one" }
        val polar = this.polarForm
        val newNorm = polar.norm.root(degree)
        val newArgument = polar.angle / degree
        return List(degree) { i -> fromPolar(newNorm, newArgument + 2 * PI * i / degree) }.toSet()
    }
}

infix fun Number.cis(argument: Number) = Complex.fromPolar(this, argument)

fun Pair<Number, Number>.toComplexRec() = Complex(this.first, this.second)
fun Pair<Number, Number>.toComplexPol() = Complex.fromPolar(this.first, this.second)

val Number.i: Complex
    get() = Complex(0, this)

fun exp(c: Complex): Complex = exp(c.a) * Complex(cos(c.b), sin(c.b))
fun log(c: Complex, branch: Int = 0): Complex {
    if (c.norm.isZero()) throw ArithmeticException("Logarithm of zero is undefined")

    return Complex(log(c.norm), c.polarForm.angle + 2 * PI * branch)
}
fun log(c: Complex, base: Complex, cBranch: Int = 0, baseBranch: Int = 0): Complex = log(c, cBranch) / log(base, baseBranch)
fun sinh(c: Complex): Complex = (exp(c) - exp(-c)) / 2
fun cosh(c: Complex): Complex = (exp(c) + exp(-c)) / 2
fun tanh(c: Complex): Complex {
    require(!cosh(c).isZero()) { "Cannot divide by zero" }
    return (exp(2 * c) - 1) / (exp(2 * c) + 1)
}

fun asinh(c: Complex): Set<CommutativeAlgebra> = (c * c + 1).root(2).map { log(c + it) }.toSet()
fun acosh(c: Complex): Set<CommutativeAlgebra> = (c * c - 1).root(2).map { log(c + it) }.toSet()
fun atanh(c: Complex): Complex = log((1 + c) / (1 - c)) / 2
fun sin(c: Complex): Complex = Complex(
    sin(c.a) * cosh(c.b),
    cos(c.a) * sinh(c.b)
)

fun cos(c: Complex): Complex = Complex(
    cos(c.a) * cosh(c.b),
    -sin(c.a) * sinh(c.b)
)

fun tan(c: Complex): Complex {
    require(cos(c).norm.toDouble() != 0.0) { "Cannot divide by zero" }
    return sin(c) / cos(c)
}

fun asin(c: Complex): Set<CommutativeAlgebra> = (1 - c * c).root(2).map { (-1).i * log(1.i * c + it) }.toSet()
fun acos(c: Complex): Set<CommutativeAlgebra> = (1 - c * c).root(2).map { (-1).i * log(c + 1.i * it) }.toSet()
fun atan(c: Complex): CommutativeAlgebra = 0.5 * 1.i * (log(1 - 1.i * c) - log(1 + 1.i * c))
