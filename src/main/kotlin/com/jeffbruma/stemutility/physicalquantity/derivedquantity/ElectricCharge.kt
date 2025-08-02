package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Constant
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents an electric charge in coulombs.
 *
 * @property charge The numerical value representing the electric charge in coulombs.
 */
class ElectricCharge(
    charge: Scalar
) : Quantity(charge, Unit) {
    constructor(charge: Number) : this(Scalar(charge.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Ampere to 1,
            BaseSIUnit.Second to 1
        )
    }
}

val Number.coulombs: ElectricCharge
    get() = ElectricCharge(this.toDouble())

val Number.elementaryCharge: ElectricCharge
    get() = ElectricCharge(this.toDouble() * Constant.ElementaryCharge.value)
