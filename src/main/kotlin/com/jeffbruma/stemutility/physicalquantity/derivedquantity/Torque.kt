package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Vector

/**
 * Represents a torque in newton-metres.
 *
 * @property torque The numerical values representing the torque in newton-metres.
 */
class Torque(
    torque: Vector
) : Quantity(torque, Unit) {
    constructor(torques: DoubleArray) : this(Vector(torques))
    constructor(vararg torques: Number) : this(torques.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -2
        )
    }
}

val DoubleArray.newtonMetres: Torque
    get() = Torque(this)

val Number.newtonMetres: Torque
    get() = Torque(this)