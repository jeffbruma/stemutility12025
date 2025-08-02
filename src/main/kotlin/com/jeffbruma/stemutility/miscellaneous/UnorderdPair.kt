package com.jeffbruma.stemutility.miscellaneous

//Unsigned types not supported. Yet.

import com.jeffbruma.stemutility.miscellaneous.decimal.times
import com.jeffbruma.stemutility.miscellaneous.integer.times
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

class UnorderedPair<out A : Number, out B : Number>(
    val first: A,
    val second: B
) {
    val product: Number
        get() = when {
            first is Double || second is Double -> checkDouble()
            first is Float || second is Float -> checkFloat()
            first is Long || second is Long -> checkLong()
            first is Int || second is Int -> checkInt()
            first is Short || second is Short -> checkInt() // No Short.sign implementation exists, and I refuse to make one
            first is Byte || second is Byte -> checkInt() // No Byte.sign implementation exists, and I refuse to make one
            else -> checkDouble()
        }

    private fun checkDouble(): Number {
        val convertedFirst = first.toDouble()
        val convertedSecond = second.toDouble()

        return if (
            (convertedFirst == Double.ZERO || convertedSecond == Double.ZERO) ||
            (convertedFirst.sign == convertedSecond.sign && convertedSecond <= Double.MAX_VALUE / convertedFirst * convertedFirst.sign) ||
            (convertedFirst.sign != convertedSecond && max(convertedFirst, convertedSecond) <= -Double.MAX_VALUE / min(
                convertedFirst,
                convertedSecond
            ))
        ) {
            convertedFirst * convertedSecond
        } else {
            BigDecimal.valueOf(convertedFirst) * BigDecimal.valueOf(convertedSecond)
        }
    }

    private fun checkFloat(): Number {
        val convertedFirst = first.toFloat()
        val convertedSecond = second.toFloat()

        return if (
            (convertedFirst == Float.ZERO || convertedSecond == Float.ZERO) ||
            (convertedFirst.sign == convertedSecond.sign && convertedSecond <= Float.MAX_VALUE / convertedFirst * convertedFirst.sign) ||
            (convertedFirst.sign != convertedSecond && max(convertedFirst, convertedSecond) <= -Float.MAX_VALUE / min(
                convertedFirst,
                convertedSecond
            ))
        ) {
            convertedFirst * convertedSecond
        } else {
            first.toDouble() * second.toDouble()
        }
    }

    private fun checkLong(): Number {
        val convertedFirst = first.toLong()
        val convertedSecond = second.toLong()

        return if (
            (convertedFirst == Long.ZERO || convertedSecond == Long.ZERO) ||
            (convertedFirst.sign == convertedSecond.sign && convertedSecond <= Long.MAX_VALUE / convertedFirst * convertedFirst.sign) ||
            (convertedFirst.sign != convertedSecond.sign && max(
                convertedFirst,
                convertedSecond
            ) <= Long.MIN_VALUE / min(convertedFirst, convertedSecond))
        ) {
            convertedFirst * convertedSecond
        } else {
            BigInteger.valueOf(convertedFirst) * BigInteger.valueOf(convertedSecond)
        }
    }

    private fun checkInt(): Number {
        val convertedFirst = first.toInt()
        val convertedSecond = second.toInt()

        return if (
            (convertedFirst == Int.ZERO || convertedSecond == Int.ZERO) ||
            (convertedFirst.sign == convertedSecond.sign && convertedSecond <= Int.MAX_VALUE / convertedFirst * convertedFirst.sign) ||
            (convertedFirst.sign != convertedSecond.sign && max(convertedFirst, convertedSecond) <= Int.MIN_VALUE / min(
                convertedFirst,
                convertedSecond
            ))
        ) {
            convertedFirst * convertedSecond
        } else {
            first.toLong() * second.toLong()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnorderedPair<*, *>) return false

        return (first == other.first && second == other.second) ||
                (first == other.second && second == other.first)
    }

    override fun hashCode() = first.hashCode() xor second.hashCode()

    override fun toString() = "($first, $second)"
}