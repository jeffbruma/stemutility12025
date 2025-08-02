@file:Suppress("unused")

package com.jeffbruma.stemutility

import com.jeffbruma.stemutility.miscellaneous.decimal.minus
import com.jeffbruma.stemutility.miscellaneous.decimal.nextUp
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val TOLERANCE = 1E-13

//rename to main to create tolerance
fun createTolerance() { // Change the function name to main to actually compute
    fun calculate(tolerance: Double) {
        val nextValue = tolerance + Math.ulp(tolerance)
        val prevValue = tolerance - Math.ulp(tolerance)

        println("Next value: $nextValue")
        println("Previous value: $prevValue")
        println("Smallest difference: ${Math.ulp(tolerance)}")
    }

    val value1 = 1.854251988336837E300
    val value2 = 1.854251988336836E300

    val absTol = abs(value1 - value2)
    val relTol = abs(1 - min(value1, value2) / max(value1, value2))
    println("Absolute: $absTol")
    calculate(absTol)
    println("Relative: $relTol")
    calculate(relTol)
}

@Suppress("Unused")
fun findInputLimit(
    mathContext: MathContext,
    function: (BigDecimal, MathContext) -> BigDecimal
): BigDecimal? {
    require(mathContext != MathContext.UNLIMITED)

    val timeoutAt = System.currentTimeMillis() + 5 * 60 * 1000
    val tolerance = BigDecimal.ONE.movePointLeft(mathContext.precision - 1)

    fun safeEval(x: BigDecimal): Boolean = try {
        function(x, mathContext)
        true
    } catch (_: Exception) {
        false
    }

    var low = BigDecimal.ZERO
    var high = BigDecimal("1E10")
    var lastGood = low

    var mid = low.add(high).divide(BigDecimal.TWO, mathContext)

    while (System.currentTimeMillis() < timeoutAt) {
        mid = low.add(high).divide(BigDecimal.TWO, mathContext)

        if (safeEval(mid)) {
            lastGood = mid
            low = mid
        } else {
            high = mid
        }


        if (high - low <= tolerance.multiply(high)) break
    }
    println("$mid..$high")

    val limit = lastGood.round(mathContext)
    val failPoint = limit.nextUp() // your version

    return if (!safeEval(failPoint)) {
        limit
    } else {
        null // false positive, limit not confirmed
    }
//            return limit
}