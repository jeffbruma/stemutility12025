package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a magnetic flux density in teslas.
 *
 * @property fluxDensity The numerical value representing the magnetic flux density in teslas.
 */
class MagneticFluxDensity(
    fluxDensity: Scalar
) : Quantity(fluxDensity, Unit) {
    constructor(fluxDensity: Number) : this(Scalar(fluxDensity.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Ampere to -1,
            BaseSIUnit.Second to -2
        )
    }
}

val Number.teslas: MagneticFluxDensity
    get() = MagneticFluxDensity(this.toDouble())