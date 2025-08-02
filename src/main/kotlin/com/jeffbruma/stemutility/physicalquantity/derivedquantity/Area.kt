package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Length
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents an area in square metres.
 *
 * @property area The numerical value representing the area in square metres.
 */
class Area(
    area: Scalar
) : Quantity(area, Unit) {
    constructor(area: Number) : this(Scalar(area.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Metre to 2)
    }

    operator fun times(length: Length) = Volume(
        this.value * length.value
    )
}

val Number.metresSquared: Area
    get() = Area(this.toDouble())
