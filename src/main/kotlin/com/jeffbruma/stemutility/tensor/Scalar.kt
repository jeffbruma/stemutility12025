package com.jeffbruma.stemutility.tensor

import kotlin.math.pow

class Scalar(val value: Double) : Tensor {
    override val shape: IntArray = intArrayOf(1)
    override val norm: Double = kotlin.math.abs(value)

    // Workaround for the fact that Scalars are not iterable
    override fun iterator(): Iterator<Double> = doubleArrayOf(value).iterator()

    override fun unaryMinus() = Scalar(-value)
    override fun plus(other: Tensor) = when (other) {
        is Scalar -> Scalar(value + other.value)
        else -> throw IllegalArgumentException("Addition not supported between Scalar and ${other::class.simpleName}")
    }
    override fun minus(other: Tensor) = when (other) {
        is Scalar -> Scalar(value - other.value)
        else -> throw IllegalArgumentException("Subtraction not supported between Scalar and ${other::class.simpleName}")
    }
    override fun times(scalar: Number) = Scalar(value * scalar.toDouble())
    override fun times(other: Tensor) = when (other) {
        is Scalar -> Scalar(value * other.value)
        else -> throw IllegalArgumentException("Multiplication not supported between Scalar and ${other::class.simpleName}")
    }
    override fun div(scalar: Number) = Scalar(value / scalar.toDouble())
    override fun div(other: Tensor) = when (other) {
        is Scalar -> Scalar(value / other.value)
        else -> throw IllegalArgumentException("Division not supported between Scalar and ${other::class.simpleName}")
    }

    override fun pow(exponent: Int) = Scalar(value.pow(exponent))
}