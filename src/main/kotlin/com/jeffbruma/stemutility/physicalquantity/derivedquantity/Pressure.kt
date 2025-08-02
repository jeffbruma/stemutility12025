package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a pressure in pascals.
 *
 * @property pressure The numerical value representing the pressure in pascals.
 */
class Pressure(
    pressure: Scalar
) : Quantity(pressure, Unit) {
    constructor(pressure: Number) : this(Scalar(pressure.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to -1,
            BaseSIUnit.Second to -2
        )
    }
}

val Number.pascals: Pressure
    get() = Pressure(this.toDouble())
