package com.jeffbruma.stemutility.numbersystem

import com.jeffbruma.stemutility.miscellaneous.*
import com.jeffbruma.stemutility.numbersystem.miscellaneous.Accessory
import com.jeffbruma.stemutility.numbersystem.miscellaneous.PolarForm
import kotlin.math.max

abstract class NormedAlgebra(vararg val component: Number, val accessory: Accessory) {
    val norm: Number = frobenize(component)
    val vectorPart = component.drop(1).toTypedArray()
    val vectorPartNorm = frobenize(vectorPart)

    abstract val conjugate: NormedAlgebra
    abstract val inverse: NormedAlgebra
    abstract val normalized: NormedAlgebra
    open val polarForm: PolarForm by lazy {
        PolarForm(
            norm = norm,
            angle = atan2(vectorPartNorm, component[0]),
            unitVector = if (vectorPartNorm.isZero()) Array(vectorPart.size) { 0 } else vectorPart.map { it / vectorPartNorm }
                .toTypedArray()
        )
    }

    operator fun get(index: Int): Number {
        require(index < accessory.Dimension) { "Index out of bounds" }
        return component[index]
    }

    abstract operator fun unaryMinus(): NormedAlgebra

    abstract operator fun plus(other: Number): NormedAlgebra
    abstract operator fun plus(other: Real): NormedAlgebra
    abstract operator fun plus(other: Complex): NormedAlgebra
    abstract operator fun plus(other: Quaternion): NormedAlgebra
    abstract operator fun plus(other: Octonion): NormedAlgebra
    abstract operator fun plus(other: Sedenion): NormedAlgebra

    abstract operator fun minus(other: Number): NormedAlgebra
    abstract operator fun minus(other: Real): NormedAlgebra
    abstract operator fun minus(other: Complex): NormedAlgebra
    abstract operator fun minus(other: Quaternion): NormedAlgebra
    abstract operator fun minus(other: Octonion): NormedAlgebra
    abstract operator fun minus(other: Sedenion): NormedAlgebra

    abstract operator fun times(other: Number): NormedAlgebra
    abstract operator fun times(other: Real): NormedAlgebra
    abstract operator fun times(other: Complex): NormedAlgebra
    abstract operator fun times(other: Quaternion): NormedAlgebra
    abstract operator fun times(other: Octonion): NormedAlgebra
    abstract operator fun times(other: Sedenion): NormedAlgebra

    abstract operator fun div(other: Number): NormedAlgebra
    abstract operator fun div(other: Real): NormedAlgebra
    abstract operator fun div(other: Complex): NormedAlgebra
    abstract operator fun div(other: Quaternion): NormedAlgebra
    abstract operator fun div(other: Octonion): NormedAlgebra
    operator fun div(other: Sedenion): Sedenion {
        return if (other != 0.toSedenion() && other.component.takeLast(8).all { it == 0 }) this.toSedenion() * other.inverse
        else throw ArithmeticException("Division by sedenions generally are not allowed")
    }

    abstract fun pow(exponent: Number): NormedAlgebra
    abstract fun pow(exponent: Real): NormedAlgebra
    abstract fun pow(exponent: Complex): NormedAlgebra
    abstract fun pow(exponent: Quaternion): NormedAlgebra
    abstract fun pow(exponent: Octonion): NormedAlgebra
    abstract fun pow(exponent: Sedenion): NormedAlgebra

    abstract fun root(degree: Int): Set<NormedAlgebra>

    override fun hashCode(): Int {
        return component
            .map { it.toDouble().takeIf { v -> v != -0.0 } ?: 0.0 }
            .fold(17) { acc, num -> 31 * acc + num.hashCode() }
    }

//    override operator fun equals(other: Any?): Boolean {
//        if (this === other) return true
//
//        if (other is Number) {
//            return if (accessory.Dimension > 1) {
//                component.drop(1).all { abs(it.toDouble()) < ABSOLUTE_TOLERANCE } &&
//                        component[0].relativeEquals(other)
//            } else component[0].relativeEquals(other)
//        }
//
//        if (other is NormedAlgebra) {
//            val maxSize = kotlin.math.max(component.size, other.component.size)
//            val result = Array(maxSize) { index ->
//                val a = component.getOrElse(index) { 0 }
//                val b = other.component.getOrElse(index) { 0 }
//                a.relativeEquals(b)
//            }.all { it }
//            return result
//        }
//
//        return false
//    }

    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is Number) {
            return if (accessory.Dimension > 1) {
                component.drop(1).all { it.relativeEquals(0) } &&
                        component[0].relativeEquals(other)
            } else {
                component[0].relativeEquals(other)
            }
        }

        if (other is NormedAlgebra) {
            val maxSize = max(component.size, other.component.size)
            for (index in 0 until maxSize) {
                val a = component.getOrElse(index) { 0 }
                val b = other.component.getOrElse(index) { 0 }
                if (!a.relativeEquals(b)) return false
            }
            return true
        }

        return false
    }

    override fun toString(): String {
        var str = if (component[0].isZero()) "" else "${component[0]}${accessory.Basis[0]}"

        fun formatNumber(n: Number) = simplifyNumber(n).toString()
        fun formatComponent(component: Number, unitVector: String): String {
            return when {
                component.isZero() -> ""
                component.toDouble() == 1.0 -> "+$unitVector"
                component.toDouble() == -1.0 -> "-$unitVector"
                component.toDouble() < 0 -> "-${formatNumber(-component)}$unitVector"
                else -> "+${formatNumber(component)}$unitVector"
            }
        }

        for (i in 1 until accessory.Dimension) {
            str += formatComponent(component[i], accessory.Basis[i])
        }
        return str.trim().removePrefix("+").ifEmpty { "0" }
    }

    private fun <T: NormedAlgebra>toAlgebra(constructor: ((Int) -> Number) -> T): T =
        constructor { this.component.getOrElse(it) { 0 } }

    fun toComplex() = toAlgebra(::Complex)
    fun toQuaternion() = toAlgebra(::Quaternion)
    fun toOctonion() = toAlgebra(::Octonion)
    fun toSedenion() = toAlgebra(::Sedenion)

    fun isZero() = component.all { it.isZero() }
    fun isReal() = !component[0].isZero() && vectorPart.all { it.isZero() }
    fun hasZeroScalar() = component[0].isZero() && !vectorPart.all { it.isZero() }
    fun hasPositiveScalar() = component[0] > 0
    fun hasNegativeScalar() = component[0] < 0
}

abstract class DivisionAlgebra(vararg component: Number, accessory: Accessory) :
    NormedAlgebra(*component, accessory = accessory)

abstract class AssociativeAlgebra(vararg component: Number, accessory: Accessory) :
    DivisionAlgebra(*component, accessory = accessory)

abstract class CommutativeAlgebra(vararg component: Number, accessory: Accessory) :
    AssociativeAlgebra(*component, accessory = accessory)

abstract class OrderedAlgebra(vararg component: Number, accessory: Accessory) :
    CommutativeAlgebra(*component, accessory = accessory)

class InfiniteSetException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

inline operator fun <reified T : NormedAlgebra> Number.plus(other: T) = when (other) {
    is Real -> other + this.toReal()
    is Complex -> other + this.toComplex()
    is Quaternion -> other + this.toQuaternion()
    is Octonion -> other + this.toOctonion()
    is Sedenion -> other + this.toSedenion()
    else -> throw IllegalArgumentException()
} as T
inline operator fun <reified T : NormedAlgebra> Number.minus(other: T) = when (other) {
    is Real -> -other + this.toReal()
    is Complex -> -other + this.toComplex()
    is Quaternion -> -other + this.toQuaternion()
    is Octonion -> -other + this.toOctonion()
    is Sedenion -> -other + this.toSedenion()
    else -> throw IllegalArgumentException()
} as T
inline operator fun <reified T : NormedAlgebra> Number.times(other: T) = when (other) {
    is Real -> other * this.toReal()
    is Complex -> other * this.toComplex()
    is Quaternion -> other * this.toQuaternion()
    is Octonion -> other * this.toOctonion()
    is Sedenion -> other * this.toSedenion()
    else -> throw IllegalArgumentException()
} as T
inline operator fun <reified T : DivisionAlgebra> Number.div(other: T) = when (other) {
    is Real -> other * this.toReal()
    is Complex -> other * this.toComplex()
    is Quaternion -> other * this.toQuaternion()
    is Octonion -> other * this.toOctonion()
    else -> throw IllegalArgumentException()
} as T

fun Number.toReal() = Real(this)
fun Number.toComplex() = Complex(a = this)
fun Number.toQuaternion() = Quaternion(w = this)
fun Number.toOctonion() = Octonion(o0 = this)
fun Number.toSedenion() = Sedenion(s0 = this)

val Number.inverse: Number
    get() = if (!this.isZero()) simplifyNumber(1 / this) else throw ArithmeticException("Division by zero from Number extension")

fun <T: NormedAlgebra> Set<T>.contentEquals(other: Set<T>): Boolean {
    if (this === other) return true
    if (this.size != other.size) return false
    return this.all { a -> other.any { b -> a == b } }
}

fun <T: NormedAlgebra> Set<T>.contains(other: T): Boolean {
    return this.any { a -> a == other }
}
