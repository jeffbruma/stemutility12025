package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a voltage in volts.
 *
 * @property voltage The numerical value representing the voltage in volts.
 */
class Voltage(
    voltage: Scalar
) : Quantity(voltage, Unit) {
    constructor(voltage: Number) : this(Scalar(voltage.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Ampere to -1,
            BaseSIUnit.Second to -3
        )
    }
}

val Number.volts: Voltage
    get() = Voltage(this.toDouble())
