package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a capacitance in farads.
 *
 * @property capacitance The numerical value representing the capacitance in farads.
 */
class Capacitance(
    capacitance: Scalar
) : Quantity(capacitance, Unit){
    constructor(capacitance: Number) : this(Scalar(capacitance.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Ampere to 2,
            BaseSIUnit.Second to 4,
            BaseSIUnit.Kilogram to -1,
            BaseSIUnit.Metre to -2
        )
    }
}

val Number.farads: Capacitance
    get() = Capacitance(this.toDouble())


