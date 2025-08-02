package com.jeffbruma.stemutility.physicalquantity.basequantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Area
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Velocity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Volume
import com.jeffbruma.stemutility.tensor.Scalar

/**
 * Length or distance.
 *
 * @property length The numerical value representing the length.
 */
class Length(
    length: Scalar
) : Quantity(length, Unit) {
    constructor(length: Number) : this(Scalar(length.toDouble()))
    companion object {
        val Unit = mapOf(BaseSIUnit.Metre to 1)
    }

    /**
     * Multiplies this length by an angle to calculate the resulting length.
     *
     * @param angle The angle to multiply by.
     * @return The resulting length.
     */
    operator fun times(angle: Angle) = Length(this.value * angle.value)

    /**
     * Multiplies this length by another length to calculate the area.
     *
     * @param anotherLength The length to multiply by.
     * @return The resulting area.
     */
    operator fun times(anotherLength: Length): Area {
        return Area(this.value * anotherLength.value)
    }

    /**
     * Multiplies this length by an area to calculate the volume.
     *
     * @param area The area to multiply by.
     * @return The resulting volume.
     */
    operator fun times(area: Area): Volume {
        return Volume(this.value * area.value)
    }

    /**
     * Divides this length by time to calculate the speed.
     *
     * @param time The time to divide by.
     * @return The resulting speed.
     */
    operator fun div(time: Time): Velocity {
        return Velocity(this.value / time.value)
    }

    /**
     * Divides this length by speed to calculate the time.
     *
     * @param velocity The speed to divide by.
     * @return The resulting time.
     */
    operator fun div(velocity: Velocity): Time {
        return Time(this.value / velocity.value)
    }
}

val Number.metres: Length
    get() = Length(this.toDouble())