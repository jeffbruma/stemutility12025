package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents the angular speed in radians per second.
 *
 * @property velocity The numerical value representing the angular speed.
 */
class AngularVelocity(
    velocity: Vector
) : Quantity(velocity, Unit) {
    constructor(velocities: DoubleArray) : this(Vector(velocities))
    constructor(vararg velocities: Number) : this(velocities.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Radian to 1,
            BaseSIUnit.Second to -1
        )
    }
}

val DoubleArray.radiansPerSecond: AngularVelocity
    get() = AngularVelocity(this)

val Number.radiansPerSecond: AngularVelocity
    get() = AngularVelocity(this)