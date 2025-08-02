package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Rotational inertia, also known as
 * Moment of inertia
 * Mass moment of inertia
 * Angular mass
 * Rotational mass
 * Second moment of mass
 *
 * @property inertia The numerical value representing the rotational inertia.
 */
class RotationalInertia(
    inertia: Scalar
) : Quantity(inertia, Unit) {
    constructor(inertia: Number) : this(Scalar(inertia.toDouble()))

    companion object {
        /**
         * The unit of measurement for rotational inertia, defined as kilogram metres squared.
         */
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2
        )
    }
}

val Number.kilogramMetresSquared: RotationalInertia
    get() = RotationalInertia(this.toDouble())
