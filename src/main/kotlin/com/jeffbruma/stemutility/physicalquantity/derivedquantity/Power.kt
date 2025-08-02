package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a power in watts.
 *
 * @property power The numerical value representing the power in watts.
 */
class Power(
    power: Scalar
) : Quantity(power, Unit) {
    constructor(power: Number) : this(Scalar(power.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -3
        )
    }
}

val Number.watts: Power
    get() = Power(this.toDouble())
