package com.jeffbruma.stemutility.numbersystem.miscellaneous

import com.jeffbruma.stemutility.miscellaneous.cos
import com.jeffbruma.stemutility.miscellaneous.sin
import com.jeffbruma.stemutility.miscellaneous.times
import com.jeffbruma.stemutility.numbersystem.NormedAlgebra

// Accessory is the interface for the normed algebra subclasses' companion object
// that endows the parent class NormedAlgebra access to its properties
@Suppress("PropertyName")
interface Accessory {
    val Dimension: Int
    val Basis: List<String>
    val Zero: NormedAlgebra

    fun conjureRectangularCoordinates(
        norm: Number,
        angle: Number,
        unitVector: Array<Number>
    ): List<Number> {
        require (unitVector.size == Dimension - 1) { "Size and dimension do not match" }
        return listOf(norm * cos(angle)) + unitVector.map { it * sin(angle) * norm }
    }
}