package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents an angle in radians.
 *
 * @property angle The numerical value representing the angle.
 */
class Angle(
    angle: Scalar,
) : Quantity(angle, Unit) {
    constructor(angle: Number) : this(Scalar(angle.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Radian to 1)
    }

    operator fun times(length: Length) = Length(
        this.value * length.value
    )
}

val Number.radians: Angle
    get() = Angle(this.toDouble())
