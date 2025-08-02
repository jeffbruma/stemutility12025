package com.jeffbruma.stemutility.tensor

interface Tensor: Iterable<Double> {
    val shape: IntArray
    val norm: Double // Frobenius norm

    operator fun unaryMinus(): Tensor
    operator fun plus(other: Tensor): Tensor
    operator fun minus(other: Tensor): Tensor
    operator fun times(scalar: Number): Tensor
    operator fun times(other: Tensor): Tensor
    operator fun div(scalar: Number): Tensor
    operator fun div(other: Tensor): Tensor
    fun pow(exponent: Int): Tensor
}

operator fun Number.times(tensor: Tensor): Tensor = tensor * this
