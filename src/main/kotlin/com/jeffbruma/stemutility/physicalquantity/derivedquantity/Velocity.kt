package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Length
import com.jeffbruma.stemutility.physicalquantity.basequantity.Time
import com.jeffbruma.stemutility.tensor.Scalar
import com.jeffbruma.stemutility.tensor.Vector
import kotlin.math.atanh
import kotlin.math.sqrt
import kotlin.math.tanh

private const val c = 299_792_458.0

/**
 * Represents a velocity in metres per second.
 * Velocity obeys special relativity and cannot exceed the speed of light.
 *
 * @property velocity The numerical values representing the velocity in metres per second.
 */
class Velocity(
    velocity: Vector
) : Quantity(velocity, Unit) {
    constructor(velocities: DoubleArray) : this(Vector(velocities))
    constructor(vararg velocities: Number) : this(velocities.map { it.toDouble() }.toDoubleArray())

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Metre to 1,
            BaseSIUnit.Second to -1
        )
    }

    /**
     * Adds two velocities relativistically.
     */
    operator fun plus(other: Velocity): Velocity {
        val v: Vector = components as Vector
        val u: Vector = other.components as Vector

        val vMagSq: Scalar = v dot v  // Dot product of the velocity vector with itself (magnitude squared)
        val gamma: Double = 1.0 / sqrt(1 - vMagSq.value / (c * c))  // Lorentz factor
        val uvDot: Scalar = u dot v  // Dot product between u and v vectors
        val gammaFactor: Double = gamma / (gamma + 1) * uvDot.value  // Factor for velocity addition

        val numerator = (u + v * gammaFactor) as Vector  // Numerator for velocity addition
        val denominator = gamma * (1 + uvDot.value / (c * c))  // Denominator for velocity addition

        return Velocity(numerator / denominator)  // Return the result as a new Velocity object
    }

    /**
     * Subtracts two velocities relativistically.
     */
    operator fun minus(other: Velocity): Velocity {
        val v: Vector = components as Vector
        val u: Vector = other.components as Vector

        val vMagSq: Scalar = v dot v  // Dot product of the velocity vector with itself (magnitude squared)
        val gamma: Double = 1.0 / sqrt(1 - vMagSq.value / (c * c))  // Lorentz factor
        val uvDot: Scalar = u dot v  // Dot product between u and v vectors
        val gammaFactor: Double = gamma / (gamma + 1) * uvDot.value  // Factor for velocity addition

        val numerator = (u - v * gammaFactor) as Vector  // Numerator for velocity subtraction (reverse of addition)
        val denominator = gamma * (1 + uvDot.value / (c * c))  // Denominator for velocity subtraction

        return Velocity(numerator / denominator)  // Return the result as a new Velocity object
    }

    /**
     * Multiplies the velocity by a scalar relativistically.
     * Note: To actually modify the magnitude, access the `value` property.
     */
    operator fun times(scalar: Double): Velocity {
        val v: Vector = components as Vector

        // Calculate rapidity from the velocity
        val rapidity = v.norm / c

        // Scale the rapidity by the scalar
        val newRapidity = rapidity * scalar

        // Calculate the new velocity using the inverse tanh of the scaled rapidity
        val newVelocityNorm = c * tanh(newRapidity)

        // Return the new velocity (scale the vector by the new norm)
        val scaleFactor = newVelocityNorm / v.norm
        val newVelocity = v * scaleFactor

        return Velocity(newVelocity)
    }

    /**
     * Multiplies the velocity by a scalar relativistically.
     * Note: To actually modify the magnitude, access the `value` property.
     */
    operator fun times(scalar: Scalar): Velocity {
        val v: Vector = components as Vector

        // Calculate rapidity from the velocity
        val rapidity = v.norm / c

        // Scale the rapidity by the scalar
        val newRapidity = rapidity * scalar.value

        // Calculate the new velocity using the inverse tanh of the scaled rapidity
        val newVelocityNorm = c * tanh(newRapidity)

        // Return the new velocity (scale the vector by the new norm)
        val scaleFactor = newVelocityNorm / v.norm
        val newVelocity = v * scaleFactor

        return Velocity(newVelocity)
    }

    operator fun times(time: Time) = Length(this.value * time.value)

    /**
     * Divides the velocity by a scalar relativistically.
     * Note: To actually modify the magnitude, access the `value` property.
     */
    operator fun div(scalar: Double): Velocity {
        require(scalar != 0.0) { "Cannot divide by zero" }
        val v: Vector = components as Vector

        // Calculate rapidity from the velocity
        val rapidity = atanh(v.norm / c)

        // Divide the rapidity by the scalar
        val newRapidity = rapidity / scalar

        // Calculate the new velocity using the inverse tanh of the new rapidity
        val newVelocityNorm = c * tanh(newRapidity)

        // Scale the original vector to match the new velocity magnitude
        val scaleFactor = newVelocityNorm / v.norm
        val newVelocity = v * scaleFactor  // This ensures it's still a Vector

        return Velocity(newVelocity)
    }

    /**
     * Divides the velocity by a scalar relativistically.
     * Note: To actually modify the magnitude, access the `value` property.
     */
    operator fun div(scalar: Scalar): Velocity {
        require(scalar.value != 0.0) { "Cannot divide by zero" }
        val v: Vector = components as Vector

        // Calculate rapidity from the velocity
        val rapidity = atanh(v.norm / c)

        // Divide the rapidity by the scalar
        val newRapidity = rapidity / scalar.value

        // Calculate the new velocity using the inverse tanh of the new rapidity
        val newVelocityNorm = c * tanh(newRapidity)

        // Scale the original vector to match the new velocity magnitude
        val scaleFactor = newVelocityNorm / v.norm
        val newVelocity = v * scaleFactor  // This ensures it's still a Vector

        return Velocity(newVelocity)
    }

    init {
        require(value in 0.0..c ) {
            "Speed cannot exceed the speed of light."
        }
    }
}

val DoubleArray.metresPerSecond: Velocity
    get() = Velocity(this)

val Number.metresPerSecond: Velocity
    get() = Velocity(this)