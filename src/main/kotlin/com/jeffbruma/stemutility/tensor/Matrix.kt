package com.jeffbruma.stemutility.tensor

import kotlin.collections.flatten
import kotlin.math.pow
import kotlin.math.sqrt

class Matrix(val components: Array<DoubleArray>) : Tensor {
    //constructor(vararg components: DoubleArray) : this(components.toList().toTypedArray()) // same JVM signature
    override val shape: IntArray = intArrayOf(components.size, components[0].size)
    override val norm: Double = sqrt(components.sumOf { row -> row.sumOf { it * it } })
    val identity: Matrix by lazy { Matrix(Array(shape[0]) { i -> DoubleArray(shape[1]) { j -> if (i == j) 1.0 else 0.0 } }) }
//    val eigenvalues: DoubleArray by lazy { TODO("Not yet implemented") }
//    val eigenvectors: Array<Vector> by lazy { TODO("Not yet implemented") }

    override fun iterator(): Iterator<Double> = components.asSequence().flatMap { it.asSequence() }.iterator()
    override fun unaryMinus(): Matrix = Matrix(components.map { row -> row.map { -it }.toDoubleArray() }.toTypedArray())
    override fun plus(other: Tensor): Tensor = when (other) {
        is Matrix -> {
            require(shape.contentEquals(other.shape)) { "Matrices must have the same dimensions for addition" }
            Matrix(components.zip(other.components) { r1, r2 -> r1.zip(r2) { a, b -> a + b }.toDoubleArray() }.toTypedArray())
        }
        else -> throw IllegalArgumentException("Addition not supported between Matrix and ${other::class.simpleName}")
    }
    override fun minus(other: Tensor): Tensor = when (other) {
        is Matrix -> {
            require(shape.contentEquals(other.shape)) { "Matrices must have the same dimensions for subtraction" }
            Matrix(components.zip(other.components) { r1, r2 -> r1.zip(r2) { a, b -> a - b }.toDoubleArray() }.toTypedArray())
        }
        else -> throw IllegalArgumentException("Subtraction not supported between Matrix and ${other::class.simpleName}")
    }
    override fun times(scalar: Number): Matrix = Matrix(components.map { row -> row.map { it * scalar.toDouble() }.toDoubleArray() }.toTypedArray())
    override fun times(other: Tensor): Tensor = when (other) {
        is Scalar -> Matrix(components.map { row -> row.map { it * other.value }.toDoubleArray() }.toTypedArray())
        is Vector -> {
            require(shape[1] == other.shape[0]) { "Matrix-Vector multiplication requires the matrix's columns to match the vector's size" }
            Vector(DoubleArray(shape[0]) { i -> components[i].indices.sumOf { j -> components[i][j] * other.components[j] } })
        }
        is Matrix -> {
            require(shape[1] == other.shape[0]) { "Matrix multiplication requires the start matrix's columns to match the second matrix's rows" }
            val result = Array(shape[0]) { DoubleArray(other.shape[1]) }
            for (i in components.indices) {
                for (j in other.components[0].indices) {
                    result[i][j] = (components[i].indices).sumOf { k -> components[i][k] * other.components[k][j] }
                }
            }
            Matrix(result)
        }
        else -> throw IllegalArgumentException("Multiplication not supported between Matrix and ${other::class.simpleName}")
    }
    override fun div(scalar: Number): Matrix = Matrix(components.map { row -> row.map { it / scalar.toDouble() }.toDoubleArray() }.toTypedArray())
    override fun div(other: Tensor): Tensor = when (other) {
        is Scalar -> Matrix(components.map { row -> row.map { it / other.value }.toDoubleArray() }.toTypedArray())
        else -> throw IllegalArgumentException("Division not supported between Matrix and ${other::class.simpleName}")
    }

    fun inverse(): Matrix {
        require(shape[0] == shape[1] && this.determinant() != 0.0) { "Inverse is only defined for square matrices with non-zero determinant" }
        val size = shape[0]
        val augmented = Array(size) { i -> components[i] + DoubleArray(size) { if (i == it) 1.0 else 0.0 } }

        for (i in 0 until size) {
            var maxRow = i
            for (k in i + 1 until size) {
                if (kotlin.math.abs(augmented[k][i]) > kotlin.math.abs(augmented[maxRow][i])) maxRow = k
            }
            val temp = augmented[i]
            augmented[i] = augmented[maxRow]
            augmented[maxRow] = temp

            val pivot = augmented[i][i]
            require(pivot != 0.0) { "Matrix is singular and cannot be inverted" }
            for (j in 0 until 2 * size) augmented[i][j] /= pivot

            for (k in 0 until size) {
                if (k != i) {
                    val factor = augmented[k][i]
                    for (j in 0 until 2 * size) augmented[k][j] -= factor * augmented[i][j]
                }
            }
        }
        return Matrix(Array(size) { i -> augmented[i].copyOfRange(size, 2 * size) })
    }

    fun transpose(): Matrix {
        val transposed = Array(shape[1]) { DoubleArray(shape[0]) }
        for (i in components.indices) {
            for (j in components[i].indices) {
                transposed[j][i] = components[i][j]
            }
        }
        return Matrix(transposed)
    }

    fun trace(): Double {
        require(shape[0] == shape[1]) { "Trace is only defined for square matrices" }
        return (0 until shape[0]).sumOf { components[it][it] }
    }

    fun determinant(): Double {
        require(shape[0] == shape[1]) { "Determinant is only defined for square matrices" }
        return computeDeterminant(components)
    }

    private fun computeDeterminant(matrix: Array<DoubleArray>): Double {
        if (matrix.size == 1) return matrix[0][0]
        if (matrix.size == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]

        var det = 0.0
        for (col in matrix.indices) {
            val subMatrix = matrix.drop(1).map { row -> row.filterIndexed { index, _ -> index != col }.toDoubleArray() }.toTypedArray()
            det += (if (col % 2 == 0) 1 else -1) * matrix[0][col] * computeDeterminant(subMatrix)
        }
        return det
    }

    override fun pow(exponent: Int) = Scalar(norm.pow(exponent))
}
