package com.jeffbruma.stemutility.physicalquantity.derivedquantity

import com.jeffbruma.stemutility.physicalquantity.BaseSIUnit
import com.jeffbruma.stemutility.physicalquantity.Quantity
import com.jeffbruma.stemutility.tensor.Scalar

/** In physics, action is a scalar quantity that describes how the balance of
 * kinetic versus potential energy of a physical system changes with trajectory.
 * Action is significant because it is an input to the principle of stationary action,
 * an approach to classical mechanics that is simpler for multiple objects.
 * Action and the variational principle are used in Feynman's formulation of
 * quantum mechanics[2] and in general relativity.[3] For systems with small values of
 * action similar to the Planck constant, quantum effects are significant.
 *
 * https://en.wikipedia.org/wiki/Action_(physics)
 */
class Action(
    action: Scalar
) : Quantity(action, Unit) {
    constructor(action: Number) : this(Scalar(action.toDouble()))

    companion object {
        val Unit = mapOf(
            BaseSIUnit.Kilogram to 1,
            BaseSIUnit.Metre to 2,
            BaseSIUnit.Second to -1
        )
    }
}

val Number.jouleSecond: Action
    get() = Action(this)
