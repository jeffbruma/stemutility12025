package com.jeffbruma.stemutility.physicalquantity

import com.jeffbruma.stemutility.physicalquantity.basequantity.kelvins
import com.jeffbruma.stemutility.physicalquantity.basequantity.kilograms
import com.jeffbruma.stemutility.physicalquantity.basequantity.metres
import com.jeffbruma.stemutility.physicalquantity.basequantity.seconds
import com.jeffbruma.stemutility.physicalquantity.derivedquantity.*
import kotlin.math.PI

object Constant {

    val HyperfineTransitionFrequencyOfCesium133 = 9_192_631_770.hertz

    val SpeedOfLight = 299_792_458.metresPerSecond

    val Planck = 6.62607015e-34.jouleSecond

    val ReducedPlanck = Planck / (2 * PI)

    val ElementaryCharge = 1.602176634e-19.coulombs

    val Boltzmann = Quantity(
        1.380649e-23,
        unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Kelvin to -1,
            BaseSIUnit.Second to -2
        )
    )

    val Avogadro = Quantity(
        6.02214076e23,
        unit = mapOf(BaseSIUnit.Mole to -1)
    )

    val LuminousEfficacyOf540THzRadiation = Quantity(
        683.0,
        unit = mapOf(
            BaseSIUnit.Candela to 1,
            BaseSIUnit.Second to 3,
            BaseSIUnit.Kilogram to -1,
            BaseSIUnit.Metre to -2
        )
    )

    val Gravitational = Quantity(
        6.6743e-11,
        unit = mapOf(
            BaseSIUnit.Metre to 3,
            BaseSIUnit.Kilogram to -1,
            BaseSIUnit.Second to -2
        )
    )

    val VacuumPermittivity = Quantity(
        8.854187818814e-12,
        unit = mapOf(
            BaseSIUnit.Ampere to 2,
            BaseSIUnit.Second to 4,
            BaseSIUnit.Kilogram to -1,
            BaseSIUnit.Metre to -3
        )
    )

    val FineStructure = ElementaryCharge.pow(2) / (2 * VacuumPermittivity * Planck * SpeedOfLight)
    val MolarMass= 1.00000000105e-3.kilogramsPerMole
}

object Planck {
    val Length = 1.61625518e-35.metres
    val Mass = 2.17643424e-8.kilograms
    val Time = 5.39124760e-44.seconds
    val Temperature = 1.41678416e32.kelvins
    val Constant = Quantity(
        6.62607015e-34,
        unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -1
        )
    )
    val reducedConstant = Constant / (2 * PI)
}
