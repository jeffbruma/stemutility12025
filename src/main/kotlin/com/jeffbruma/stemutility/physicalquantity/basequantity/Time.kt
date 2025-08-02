package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.ElectricCharge
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a time in seconds.
 *
 * @property time The numerical value representing the time.
 */
class Time(
    time: Scalar
) : Quantity(time, Unit) {
    constructor(time: Number) : this(Scalar(time.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Second to 1)
    }

    operator fun times(electricCurrent: ElectricCurrent) = ElectricCharge(this.value * electricCurrent.value)
}

val Number.seconds: Time
    get() = Time(this.toDouble())
