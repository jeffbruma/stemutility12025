package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a frequency in hertz.
 *
 * @property frequency The numerical value representing the frequency in hertz.
 */
class Frequency(
    frequency: Scalar
) : Quantity(frequency, Unit) {
    constructor(frequency: Number) : this(Scalar(frequency.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Second to -1)
    }
}

val Number.hertz: Frequency
    get() = Frequency(this.toDouble())
