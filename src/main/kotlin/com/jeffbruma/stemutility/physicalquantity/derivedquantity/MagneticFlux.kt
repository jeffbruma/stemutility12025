package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a magnetic flux in webers.
 *
 * @property magneticFlux The numerical value representing the magnetic flux in webers.
 */
class MagneticFlux(
    magneticFlux: Scalar
) : Quantity(magneticFlux, Unit) {
    constructor(magneticFlux: Number) : this(Scalar(magneticFlux.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Ampere to -1,
            BaseSIUnit.Second to -2
        )
    }
}

val Number.webers: MagneticFlux
    get() = MagneticFlux(this.toDouble())