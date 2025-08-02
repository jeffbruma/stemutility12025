package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents an angular acceleration in radians per second squared.
 *
 * @property acceleration The numerical values representing the angular acceleration in radians per second squared.
 */
class AngularAcceleration(
    acceleration: Vector
) : Quantity(acceleration, Unit) {
    constructor(accelerations: DoubleArray) : this(Vector(accelerations))
    constructor(vararg accelerations: Number) : this(accelerations.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Radian to 1,
            BaseSIUnit.Second to -2
        )
    }
}

val DoubleArray.radiansPerSecondSquared: AngularAcceleration
    get() = AngularAcceleration(this)

val Number.radiansPerSecondSquared: AngularAcceleration
    get() = AngularAcceleration(this)