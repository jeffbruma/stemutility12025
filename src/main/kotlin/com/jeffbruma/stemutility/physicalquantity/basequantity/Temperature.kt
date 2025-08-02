package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents the temperature in kelvins.
 *
 * @property temperature The numerical value representing the temperature.
 */
class Temperature(
    temperature: Scalar
) : Quantity(temperature, Unit) {
    constructor(temperature: Number) : this(Scalar(temperature.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Kelvin to 1)
    }
}

val Number.kelvins: Temperature
    get() = Temperature(this.toDouble())
