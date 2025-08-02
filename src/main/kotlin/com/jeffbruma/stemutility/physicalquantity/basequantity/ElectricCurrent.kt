package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.ElectricCharge
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the electric current in amperes.
 *
 * @property current The numerical value representing the electric current.
 */
class ElectricCurrent(
    current: Scalar
) : Quantity(current, Unit) {
    companion object {
        val Unit = mapOf(BaseSIUnit.Ampere to 1)
    }

    /**
     * Multiplies the electric current by a given time to calculate the electric charge.
     *
     * @param time The time to multiply by.
     * @return The resulting electric charge.
     */
    operator fun times(time: Time): ElectricCharge {
        return ElectricCharge(this.value * time.value)
    }
}

val Number.amperes: ElectricCurrent
    get() = ElectricCurrent(Scalar(this.toDouble()))
