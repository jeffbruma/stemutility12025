package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents a momentum in kilogram metres per second.
 *
 * @property momentum The numerical values representing the momentum in kilogram metres per second.
 */
class Momentum(
    momentum: Vector
) : Quantity(momentum, Unit) {
    constructor(momenta: DoubleArray) : this(Vector(momenta))
    constructor(vararg momenta: Number) : this(momenta.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 1,
            BaseSIUnit.Second to -1
        )
    }

    operator fun div(mass: Mass) = Velocity(
        this.components
            .map { it / mass.value }
            .toDoubleArray()
    )

    operator fun div(velocity: Velocity) = Mass(
        this.value / velocity.value
    )
}

val DoubleArray.kilogramMetresPerSecond: Momentum
    get() = Momentum(this)

val Number.kilogramMetresPerSecond: Momentum
    get() = Momentum(this)
