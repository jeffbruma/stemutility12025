package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents an electrical resistance in ohms.
 *
 * @property resistance The numerical value representing the electrical resistance in ohms.
 */
class ElectricalResistance(
    resistance: Scalar
) : Quantity(resistance, Unit) {
    constructor(resistance: Number) : this(Scalar(resistance.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Ampere to -2,
            BaseSIUnit.Second to -3
        )
    }
}

val Number.ohms: ElectricalResistance
    get() = ElectricalResistance(this.toDouble())
