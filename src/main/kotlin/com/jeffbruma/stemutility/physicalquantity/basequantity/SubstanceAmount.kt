package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.MolarMass
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the amount of substance in moles.
 *
 * @property substanceAmount The numerical value representing the amount of substance.
 */
class SubstanceAmount(
    substanceAmount: Scalar
) : Quantity(substanceAmount, Unit) {
    constructor(substanceAmount: Number) : this(Scalar(substanceAmount.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Mole to 1)
    }

    /**
     * Multiplies the amount of substance by a given molar mass to calculate the mass.
     *
     * @param molarMass The molar mass to multiply by.
     * @return The resulting mass.
     */
    operator fun times(molarMass: MolarMass) = Mass(this.value * molarMass.value)
}

val Number.moles: SubstanceAmount
    get() = SubstanceAmount(this.toDouble())
