package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the solid angle in steradians.
 *
 * @property solidAngle The numerical value representing the solid angle.
 */
class SolidAngle(
    solidAngle: Scalar
) : Quantity(solidAngle, Unit) {
    constructor(solidAngle: Number) : this(Scalar(solidAngle.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Steradian to 1)
    }
}

val Number.steradians: SolidAngle
    get() = SolidAngle(this.toDouble())
