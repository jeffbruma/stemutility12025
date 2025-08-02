package com.jeffbruma.stemutility.physicalquantity

import com.jeffbruma.stemutility.tensor.Scalar

class Dimensionless(
    dimensionless: Scalar
) : Quantity(dimensionless, Unit) {
    constructor(dimensionless: Number) : this(Scalar(dimensionless.toDouble()))

    companion object {
        val Unit = emptyMap<BaseSIUnit, Int>()
    }

    fun toDouble() = value
}
