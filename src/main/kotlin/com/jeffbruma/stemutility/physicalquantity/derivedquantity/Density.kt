package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Density commonly refers to mass per unit three-dimensional volume.
 */
class Density(
    density: Scalar
) : Quantity(density, Unit) {
    constructor(density: Number) : this(Scalar(density.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to -3
        )
    }

    operator fun times(volume: Volume) = Mass(
        this.value * volume.value
    )
}

val Number.kilogramsPerMetreCubed: Density
    get() = Density(this.toDouble())
