package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.physicalquantity.basequantity.SubstanceAmount
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the molar mass, which is a derived quantity.
 *
 * @property mass The magnitude of the molar mass.
 */
class MolarMass(
    mass: Scalar
) : Quantity(mass, Unit) {
    constructor(mass: Number) : this(Scalar(mass.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Mole to -1
        )
    }

    /**
     * Multiplies the molar mass by the amount of substance to get the mass.
     *
     * @param moles The amount of substance in moles.
     * @return The mass corresponding to the given amount of substance.
     */
    operator fun times(moles: SubstanceAmount) = Mass(
        value * moles.value
    )
}

val Number.kilogramsPerMole: MolarMass
    get() = MolarMass(this.toDouble())