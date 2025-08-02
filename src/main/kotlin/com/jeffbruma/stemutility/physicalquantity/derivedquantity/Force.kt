package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.tensor.Vector

class Force(
    force: Vector
) : Quantity(force, Unit) {
    constructor(forces: DoubleArray) : this(Vector(forces))
    constructor(vararg forces: Number) : this(forces.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 1,
            BaseSIUnit.Second to -2
        )
    }

    operator fun div(mass: Mass) = Acceleration(
        this.components
        .map { it / mass.value }
        .toDoubleArray()
    )
}

val DoubleArray.newtons: Force
    get() = Force(this)

val Number.newtons: Force
    get() = Force(this)
