package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Length
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Represents a volume in cubic metres.
 *
 * @property volume The numerical value representing the volume in cubic metres.
 */
class Volume(
    volume: Scalar
) : Quantity(volume, Unit) {
    constructor(volume: Number) : this(Scalar(volume.toDouble()))

    companion object {
        val Unit = mapOf(BaseSIUnit.Metre to 3)
    }

    operator fun div(length: Length) = Area(
        this.value / length.value
    )
    operator fun div(area: Area) = Length(
        this.value / area.value
    )
}

val Number.metresCubed: Volume
    get() = Volume(this.toDouble())
