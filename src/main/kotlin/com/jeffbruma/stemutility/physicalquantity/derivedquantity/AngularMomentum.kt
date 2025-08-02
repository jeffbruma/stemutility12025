package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents an angular momentum in kilogram metres squared per second.
 *
 * @property momentum The numerical values representing the angular momentum in kilogram metres squared per second.
 */
class AngularMomentum(
    momentum: Vector
) : Quantity(momentum, Unit) {
    constructor(momenta: DoubleArray) : this(Vector(momenta))
    constructor(vararg momenta: Number) : this(momenta.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -1
        )
    }
}

val DoubleArray.kilogramMetresSquaredPerSecond: AngularMomentum
    get() = AngularMomentum(this)

val Number.kilogramMetresSquaredPerSecond: AngularMomentum
    get() = AngularMomentum(this)
