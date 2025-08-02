package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Acceleration
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Density
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Force
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.MolarMass
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Volume
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a mass in kilograms.
 */
class Mass(
    mass: Scalar
) : Quantity(mass, Unit) {
    constructor(mass: Number) : this(Scalar(mass.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Kilogram to 1)
    }

    /**
     * Multiplies the mass by the acceleration to get the force.
     *
     * @param acceleration The acceleration.
     * @return The force corresponding to the given acceleration.
     */
    operator fun times(acceleration: Acceleration) = Force(
        acceleration.components
        .map { it * this.value }
        .toDoubleArray()
    )

    /**
     * Divides the mass by the volume to get the density.
     *
     * @param volume The volume.
     * @return The density corresponding to the given volume.
     */
    operator fun div(volume: Volume) = Density(
        this.value / volume.value
    )

    /**
     * Divides the mass by the density to get the volume.
     *
     * @param density The density.
     * @return The volume corresponding to the given density.
     */
    operator fun div(density: Density) = Volume(
        this.value / density.value
    )

    /**
     * Divides the mass by the amount of substance to get the molar mass.
     *
     * @param substanceAmount The amount of substance.
     * @return The molar mass corresponding to the given amount of substance.
     */
    operator fun div(substanceAmount: SubstanceAmount) = MolarMass(
        value / substanceAmount.value
    )
}

val Number.kilograms: Mass
    get() = Mass(this.toDouble())
