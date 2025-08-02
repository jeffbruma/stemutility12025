package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents an energy or work in joules.
 *
 * @property energy The numerical value representing the energy in joules.
 */
class Energy(
    energy: Scalar
) : Quantity(energy, unit = Unit) {
    constructor(energy: Double) : this (Scalar(energy))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -2
        )
    }
}

val Number.joules: Energy
    get() = Energy(this.toDouble())
