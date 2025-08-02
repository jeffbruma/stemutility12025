package com.jeffbruma.stemutility.physicalquantity

import com.jeffbruma.stemutility.physicalquantity.basequantity.Angle
import com.jeffbruma.stemutility.physicalquantity.basequantity.ElectricCurrent
import com.jeffbruma.stemutility.physicalquantity.basequantity.Length
import com.jeffbruma.stemutility.physicalquantity.basequantity.LuminousIntensity
import com.jeffbruma.stemutility.physicalquantity.basequantity.Mass
import com.jeffbruma.stemutility.physicalquantity.basequantity.SolidAngle
import com.jeffbruma.stemutility.physicalquantity.basequantity.SubstanceAmount
import com.jeffbruma.stemutility.physicalquantity.basequantity.Temperature
import com.jeffbruma.stemutility.physicalquantity.basequantity.Time
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Acceleration
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Action
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.AngularAcceleration
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.AngularMomentum
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.AngularVelocity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Area
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Capacitance
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Density
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.ElectricalResistance
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.ElectricCharge
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Energy
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Force
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Frequency
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.MagneticFlux
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.MagneticFluxDensity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.MolarMass
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Momentum
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Power
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Pressure
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.RotationalInertia
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Torque
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Velocity
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Voltage
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.Volume
import com.jeffbruma.stemutility.tensor.Scalar
import com.jeffbruma.stemutility.tensor.Tensor
import com.jeffbruma.stemutility.tensor.Vector

/**
 * A class representing a physical quantity.
 *
 * @property components The components of the quantity.
 * @property unit The unit of the quantity.
 */
open class Quantity(
    val components: Tensor,
    val unit: Map<BaseSIUnit, Int>
) : Comparable<Quantity> {
    constructor(vararg value: Number, unit: Map<BaseSIUnit, Int>) : this(Vector(value.map { it.toDouble() }.toDoubleArray()), unit)

    val value = components.norm
    val dimensions = components.shape[0]

    private fun create(
        value: Tensor,
        unit: Map<BaseSIUnit, Int>
    ): Quantity {
        return when {
            unit == Angle.Unit -> Angle(value as Scalar)
            unit == ElectricCurrent.Unit -> ElectricCurrent(value as Scalar)
            unit == Length.Unit -> Length(value as Scalar)
            unit == LuminousIntensity.Unit -> LuminousIntensity(value as Scalar)
            unit == Mass.Unit -> Mass(value as Scalar)
            unit == SolidAngle.Unit -> SolidAngle(value as Scalar)
            unit == SubstanceAmount.Unit -> SubstanceAmount(value as Scalar)
            unit == Temperature.Unit -> Temperature(value as Scalar)
            unit == Time.Unit -> Time(value as Scalar)
            unit == Acceleration.Unit -> Acceleration(value as Vector)
            unit == Action.Unit && value is Scalar -> Action(value)
            unit == AngularAcceleration.Unit -> AngularAcceleration(value as Vector)
            unit == AngularMomentum.Unit && value is Vector -> AngularMomentum(value)
            unit == AngularVelocity.Unit -> AngularVelocity(value as Vector)
            unit == Area.Unit -> Area(value as Scalar)
            unit == Capacitance.Unit -> Capacitance(value as Scalar)
            unit == Density.Unit -> Density(value as Scalar)
            unit == ElectricalResistance.Unit -> ElectricalResistance(value as Scalar)
            unit == ElectricCharge.Unit -> ElectricCharge(value as Scalar)
            unit == Energy.Unit && value is Scalar -> Energy(value)
            unit == Force.Unit -> Force(value as Vector)
            unit == Frequency.Unit -> Frequency(value as Scalar)
            unit == MagneticFlux.Unit -> MagneticFlux(value as Scalar)
            unit == MagneticFluxDensity.Unit -> MagneticFluxDensity(value as Scalar)
            unit == MolarMass.Unit -> MolarMass(value as Scalar)
            unit == Momentum.Unit -> Momentum(value as Vector)
            unit == Power.Unit -> Power(value as Scalar)
            unit == Pressure.Unit -> Pressure(value as Scalar)
            unit == RotationalInertia.Unit -> RotationalInertia(value as Scalar)
            unit == Torque.Unit && value is Vector -> Torque(value)
            unit == Velocity.Unit -> Velocity(value as Vector)
            unit == Volume.Unit -> Volume(value as Scalar)
            unit == Voltage.Unit -> Voltage(value as Scalar)
            unit == Dimensionless.Unit -> Dimensionless(value as Scalar)
            else -> Quantity(value, unit)
        }
    }

    operator fun unaryMinus() = -components

    operator fun plus(other: Quantity): Quantity {
        require(this::class == other::class && this.dimensions == other.dimensions) { "Same kind of quantity only" }
        val sum = components + other.components
        return create(sum, unit)
    }

    operator fun minus(other: Quantity): Quantity {
        require(this::class == other::class && this.dimensions == other.dimensions) { "Same kind of quantity only" }
        val difference = components - other.components
        return create(difference, this.unit)
    }

    open operator fun times(scalar: Number): Quantity {
        require(scalar.toDouble().isFinite()) { "Scalar must be finite" }
        return create (components * scalar.toDouble(), unit)

    }
    
    operator fun times(other: Quantity): Quantity {
        val newUnits = combineUnits(unit, other.unit, 1)
        return create(components * other.components, newUnits)
    }

    operator fun div(scalar: Number): Quantity {
        require(scalar.toDouble() != 0.0) { "Cannot divide by zero" }
        return create(components / scalar.toDouble(), unit)
    }

    operator fun div(other: Quantity): Quantity {
        require(other.value != 0.0) { "Cannot divide by zero" }
        val newUnits = combineUnits(unit, other.unit, -1)
        return create(components / other.components, newUnits)
    }

    fun pow(exponent: Int): Quantity {
        val newUnits = unit.mapValues { (_, power) -> power * exponent }
        return create(components.pow(exponent), newUnits)
    }

    private fun combineUnits(
        unit1: Map<BaseSIUnit, Int>,
        unit2: Map<BaseSIUnit, Int>,
        flag: Int
    ): Map<BaseSIUnit, Int> {
        val combinedUnit = mutableMapOf<BaseSIUnit, Int>()

        // Merge keys of both maps. If a key is in both, sum the powers
        (unit1.keys + unit2.keys).forEach { unit ->
            val power1 = unit1.getOrDefault(unit, 0)
            val power2 = unit2.getOrDefault(unit, 0)
            val newPower = power1 + power2 * flag
            if (newPower != 0)
                combinedUnit[unit] = newPower
        }
        return combinedUnit
    }
    
    override fun toString(): String {
        if (value == 0.0) return "0"

        val unitStrings = mutableListOf<String>()

        val positiveUnits = unit.filterValues { it > 0 }.entries.sortedBy { it.value }
        val negativeUnits = unit.filterValues { it < 0 }.entries.sortedByDescending { it.value }

        positiveUnits.forEach { (baseUnit, power) ->
            unitStrings.add(
                if (power == 1) baseUnit.symbol else "${baseUnit.symbol}^$power"
            )
        }

        negativeUnits.forEach { (baseUnit, power) ->
            unitStrings.add("${baseUnit.symbol}^$power")
        }

        return "$value ${unitStrings.joinToString(separator = "\u22c5")}".trim()
    }

    override fun compareTo(other: Quantity): Int {
        require(this::class == other::class) {
            "Cannot compare ${this::class.simpleName} with ${other::class.simpleName}"
        }
        return this.value.compareTo(other.value)
    }

    init {
        require(value.isFinite()) { "Magnitude must be finite" }
    }
}

operator fun Number.times(quantity: Quantity) = quantity * this
