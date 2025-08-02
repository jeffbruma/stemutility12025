package com.jeffbruma.stemutility.tensor

import kotlin.math.pow
import kotlin.math.sqrt

class Vector(val components: DoubleArray) : Tensor {
    constructor(vararg components: Number) : this(components.map { it.toDouble() }.toDoubleArray())
    override val shape: IntArray = intArrayOf(components.size)
    override val norm: Double = sqrt(components.sumOf { it * it })

    override fun iterator(): Iterator<Double> = components.iterator()
    override fun unaryMinus(): Vector {
        println("unaryMinus called on Vector(${components.joinToString()})")
        return Vector(components.map { -it }.toDoubleArray())
    }
    override fun plus(other: Tensor) = when (other) {
        is Vector -> {
            require(shape.contentEquals(other.shape)) { "Vectors must have the same size for addition" }
            Vector(components.zip(other.components) { a, b -> a + b }.toDoubleArray())
        }
        else -> throw IllegalArgumentException("Addition not supported between Vector and ${other::class.simpleName}")
    }
    override fun minus(other: Tensor) = when (other) {
        is Vector -> {
            require(shape.contentEquals(other.shape)) { "Vectors must have the same size for subtraction" }
            Vector(components.zip(other.components) { a, b -> a - b }.toDoubleArray())
        }
        else -> throw IllegalArgumentException("Subtraction not supported between Vector and ${other::class.simpleName}")
    }
    override fun times(scalar: Number) = Vector(components.map { it * scalar.toDouble() }.toDoubleArray())
    override fun times(other: Tensor) = when (other) {
        is Scalar -> Vector(components.map { it * other.value }.toDoubleArray())
        is Vector -> { // Element-wise multiplication
            require(shape.contentEquals(other.shape)) { "Vectors must have the same size for element-wise multiplication" }
            Vector(components.zip(other.components) { a, b -> a * b }.toDoubleArray())
        }
        else -> throw IllegalArgumentException("Multiplication not supported between Vector and ${other::class.simpleName}")
    }

    infix fun dot(other: Vector): Scalar {
        require(shape.contentEquals(other.shape)) { "Vectors must have the same size for dot product" }
        return Scalar(components.zip(other.components) { a, b -> a * b }.sum())
    }

    infix fun cross(other: Vector): Vector {
        require(components.size == 3 && other.components.size == 3) { "Cross product is only defined for 3D vectors" }
        return Vector(
            doubleArrayOf(
                components[1] * other.components[2] - components[2] * other.components[1],
                components[2] * other.components[0] - components[0] * other.components[2],
                components[0] * other.components[1] - components[1] * other.components[0]
            )
        )
    }
    override fun div(scalar: Number) = Vector(components.map { it / scalar.toDouble() }.toDoubleArray())
    override fun div(other: Tensor) = when (other) {
        is Scalar -> Vector(components.map { it / other.value }.toDoubleArray())
        else -> throw IllegalArgumentException("Division not supported between Vector and ${other::class.simpleName}")
    }

    override fun pow(exponent: Int) = Scalar(norm.pow(exponent))
}