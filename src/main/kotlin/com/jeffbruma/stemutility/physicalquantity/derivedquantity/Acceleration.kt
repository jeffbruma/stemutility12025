package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.physicalquantity.basequantity.Time
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents the acceleration in metres per second squared.
 *
 * @property acceleration The numerical value representing the acceleration.
 */
class Acceleration(
    acceleration: Vector
) : Quantity(acceleration,Unit) {
    constructor(accelerations: DoubleArray) : this(Vector(accelerations))
    constructor(vararg accelerations: Number) : this(accelerations.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Metre to 1,
            BaseSIUnit.Second to -2
        )
    }

    operator fun times(time: Time) = Velocity(
        this.components
        .map { it * time.value }
        .toDoubleArray()
    )

    operator fun times(mass: Mass) = Force(
        this.components
        .map { it * mass.value }
        .toDoubleArray()
    )
}

val DoubleArray.metresPerSecondSquared: Acceleration
    get() = Acceleration(this)

val Number.metresPerSecondSquared: Acceleration
    get() = Acceleration(this)
