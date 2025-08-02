package com.jeffbruma.stemutility.numbersystem.miscellaneous

import com.jeffbruma.stemutility.miscellaneous.contentRelativeEquals
import com.jeffbruma.stemutility.miscellaneous.relativeEquals

@Suppress("Unused")
data class PolarForm(
    val norm: Number = 0,
    val angle: Number = 0,
    val unitVector: Array<Number> = emptyArray()
) {
    constructor (vararg value: Number) : this(value[0], value[1], value.drop(2).toTypedArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (other is PolarForm) {
            require(unitVector.size == other.unitVector.size)

            if (!norm.relativeEquals(other.norm)) return false
            if (!angle.relativeEquals(other.angle)) return false
            if (!unitVector.contentRelativeEquals(other.unitVector)) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = norm.hashCode()
        result = 31 * result + angle.hashCode()
        result = 31 * result + unitVector.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Norm: $norm, Angle: $angle, Unit Vector: ${unitVector.joinToString(prefix = "< ", separator = " ", postfix = " >")}"
    }
}