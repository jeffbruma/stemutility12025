package com.jeffbruma.stemutility.miscellaneous

import java.security.SecureRandom
import kotlin.random.Random

class SecureKotlinRandom : Random() {
    companion object {
        val Instance: SecureKotlinRandom by lazy { SecureKotlinRandom() }
    }

    private val secureRandom = SecureRandom()

    override fun nextBits(bitCount: Int): Int {
        require(bitCount in 0..32) { "bitCount must be between 0 and 32" }
        val bytesNeeded = (bitCount + 7) / 8
        val bytes = ByteArray(bytesNeeded)
        secureRandom.nextBytes(bytes)

        var value = 0
        for (b in bytes) {
            value = (value shl 8) or (b.toInt() and 0xFF)
        }

        val excessBits = bytesNeeded * 8 - bitCount
        return value ushr excessBits
    }
}