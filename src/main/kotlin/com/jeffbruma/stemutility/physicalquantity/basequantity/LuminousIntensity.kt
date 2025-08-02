package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the luminous intensity in candelas.
 *
 * @property luminousIntensity The numerical value representing the luminous intensity.
 */
class LuminousIntensity(
    luminousIntensity: Scalar,
) : Quantity(luminousIntensity, Unit) {
    constructor(luminousIntensity: Number) : this(Scalar(luminousIntensity.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Candela to 1)
    }
}

val Number.candelas: LuminousIntensity
    get() = LuminousIntensity(this.toDouble())
