package com.jeffbruma.stemutility.miscellaneous.integer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

class BigIntegerRangeTest {
    private val veryBigInteger = BigInteger(ULong.MAX_VALUE.toString()) + BigInteger.ONE // 18446744073709551616
    private val veryBigRange = -veryBigInteger..veryBigInteger

    @Nested
    inner class BigIntegerRangeBehaviourTest {

        @Nested
        inner class ConstructionTest {
            @Test
            fun `throws if step is zero on a non-singleton range`() {
                println(veryBigInteger)
                val exception = assertThrows<IllegalArgumentException> {
                    veryBigRange step 0
                }

                assertEquals("Zero step is allowed only for a singleton range.", exception.message)
            }

            @Test
            fun `throws if step is not zero on a singleton range`() {
                val exception = assertThrows<IllegalArgumentException> {
                    BigIntegerRange(1.0, 1.0) step 1.0
                }
                assertEquals("No step for singleton range.", exception.message)
            }

            @Test
            fun `throws if step overextends the range`() {
                val exception1 = assertThrows<IllegalArgumentException> {
                    BigIntegerRange("0", "2") step "3"
                }
                assertEquals("Step overextends start.", exception1.message)

                val exception2 = assertThrows<IllegalArgumentException> {
                    BigIntegerRange(2L, 0L) step -3L
                }
                assertEquals("Step overextends start.", exception2.message)
            }

            @Test
            fun `throws if range is ascending and step is negative`() {
                val exception = assertThrows<IllegalArgumentException> {
                    BigIntegerRange(BigInteger.ZERO, BigInteger.TEN) step -1
                }
                assertEquals("Misaligned step.", exception.message)
            }

            @Test
            fun `throws if range is descending and step is positive`() {
                val exception = assertThrows<IllegalArgumentException> {
                    BigIntegerRange(BigInteger.TEN, BigInteger.ZERO) step 1
                }
                assertEquals("Misaligned step.", exception.message)
            }

            @Test
            fun `throws for non-numeric arguments`() {
                assertThrows<IllegalArgumentException> {
                    BigIntegerRange(Double.NEGATIVE_INFINITY, 0)
                }

                assertThrows<IllegalArgumentException> {
                    BigIntegerRange(0, Double.POSITIVE_INFINITY)
                }

                assertThrows<IllegalArgumentException> {
                    BigIntegerRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
                }

                assertThrows<IllegalArgumentException> {
                    BigIntegerRange(Double.NaN, 0)
                }

                assertThrows<IllegalArgumentException> {
                    BigIntegerRange("Hello", "World")
                }
            }

            @Test
            fun `non-integer arguments are truncated`() {
                assertEquals(BigIntegerRange(0, 10), BigIntegerRange(0.5, 10.1))
                assertEquals(BigIntegerRange(-10, -1), BigIntegerRange(-10.5, -1.25))
            }
        } // 7 passed

        @Nested
        inner class CorePropertiesTest {
            @Test
            fun `last aligns for positive step`() {
                assertEquals(veryBigInteger, veryBigRange.last)
            }

            @Test
            fun `last aligns for negative step`() {
                assertEquals(-veryBigInteger, veryBigRange.reversed().last)
            }
        } // 2 passed

        @Nested
        inner class EqualityTest {
            @Test
            fun `ranges with same numeric values for arguments are equal`() {
                val r1 = BigIntegerRange(1, 10)
                val r2 = BigIntegerRange(1.0, 10.0)
                assertEquals(r1, r2)
                assertEquals(r1.hashCode(), r2.hashCode())
            }

            @Test
            fun `same range but with different steps are not equal`() {
                val a = BigIntegerRange(0, 10) step 2
                val b = BigIntegerRange(0, 10) step 3

                assertNotEquals(a, b)
            }
        } // 2 passed

        @Nested
        inner class ToStringTest {
            @Test
            fun `toString returns readable representation of ranges`() {
                val range = BigIntegerRange(2, 8)
                val steppedRange = BigIntegerRange(0, 10) step 2

                assertEquals("[2..8]", range.toString())
                assertEquals("[0..10] step 2", steppedRange.toString())
            }
        } // 1 passed

        @Nested
        inner class CompareToTest {
            @Test
            fun `compareTo reflects first value ordering`() {
                val a = BigIntegerRange(0, 5) step 1
                val b = BigIntegerRange(1, 5) step 1
                assertTrue(a < b)
                assertTrue(b > a)
            }

            @Test
            fun `compareTo reflects last value ordering`() {
                val a = BigIntegerRange(1, 5) step 1
                val b = BigIntegerRange(1, 4) step 1
                assertTrue(a > b)
                assertTrue(b < a)
            }

            @Test
            fun `compareTo reflects step size ordering`() {
                val a = BigIntegerRange(1, 10) step 2
                val b = BigIntegerRange(1, 10) step 3
                assertTrue(a < b || a > b)
                assertNotEquals(0, a.compareTo(b))
            }

            @Test
            fun `compareTo distinguishes null step from defined step`() {
                val a = BigIntegerRange(1, 5)
                val b = BigIntegerRange(1, 5) step 1
                assertTrue(a < b || a > b)
                assertNotEquals(0, a.compareTo(b))
            }
        } // 4 passed

        @Nested
        inner class ContainsIntegerTest {
            @Test
            fun `value containment with null step behaves like a closed interval`() {
                val range = BigIntegerRange(1, 5) // no step
                assertTrue(BigInteger.valueOf(2) in range)
                assertTrue(BigInteger.valueOf(4) in range)
                assertFalse(BigInteger.ZERO in range)
                assertFalse(BigInteger.valueOf(6) in range)
            }

            @Test
            fun `value containment with step checks alignment`() {
                val range = BigIntegerRange(1, 10) step 2
                assertTrue(BigInteger.valueOf(3) in range)
                assertFalse(BigInteger.valueOf(4) in range)
            }

            @Test
            fun `singleton range only contains its single value`() {
                val range = BigIntegerRange(5, 5)
                assertTrue(BigInteger.valueOf(5) in range)
                assertFalse(BigInteger.valueOf(4) in range)
            }
        } // 3 passed

        @Nested
        inner class ContainsRangeTest {
            @Test
            fun `range contains another fully aligned stepped range`() {
                val outer = BigIntegerRange(0, 10) step 2
                val inner = BigIntegerRange(2, 8) step 2
                assertTrue(inner in outer)
            }

            @Test
            fun `range does not contain another if steps are misaligned`() {
                val outer = BigIntegerRange(0, 10) step 2
                val inner = BigIntegerRange(3, 9) step 2
                assertFalse(inner in outer)
            }

            @Test
            fun `range does not contain another if its endpoints fall outside`() {
                val outer = BigIntegerRange(0, 10) step 2
                val inner = BigIntegerRange(-2, 8) step 2
                assertFalse(inner in outer)
            }

            @Test
            fun `range contains another unstepped range if bounds are within`() {
                val outer = BigIntegerRange(0, 10) step 1
                val inner = BigIntegerRange(1, 9)
                assertTrue(inner in outer)
            }

            @Test
            fun `throws for containment of stepped ranges with opposite directions`() {
                val ascending = BigIntegerRange(0, 10) step 1
                val descending = BigIntegerRange(10, 0) step -1

                val exception = assertThrows<IllegalArgumentException> {
                    descending in ascending
                }
                assertEquals("Cannot compare ranges with opposing step directions", exception.message)
            }
        } // 5 passed

        @Nested
        inner class SequenceTest {
            @Test
            fun `singleton range yields only the start value`() {
                val range = BigIntegerRange(42, 42)
                val values = range.sequence().toList()

                assertEquals(listOf(BigInteger.valueOf(42)), values)
            }

            @Test
            fun `sequence works for ascending range with step 1`() {
                val range = BigIntegerRange(1, 5) step 1
                val expected = listOf(1, 2, 3, 4, 5).map { it.toBigInteger() }

                assertEquals(expected, range.sequence().toList())
            }

            @Test
            fun `sequence works for descending range with step -2`() {
                val range = BigIntegerRange(10, 2) step -2
                val expected = listOf(10, 8, 6, 4, 2).map { it.toBigInteger() }

                assertEquals(expected, range.sequence().toList())
            }

            @Test
            fun `sequence with large step does not overshoot`() {
                val range = BigIntegerRange(0, 10) step 4
                val expected = listOf(0, 4, 8).map { it.toBigInteger() }

                assertEquals(expected, range.sequence().toList())
            }

            @Test
            fun `sequence throws for non-singleton range without step`() {
                val range = BigIntegerRange(0, 100)

                val exception = assertThrows<IllegalStateException> {
                    range.sequence().toList()
                }

                assertEquals("Cannot iterate non-singleton range without a defined step", exception.message)
            }

            @Disabled("This would take centuries to finish. Do not enable.")
            @Test
            fun `do not materialize very big range sequence`() {
                val range = veryBigRange step BigInteger.ONE
                range.sequence().toList() // this is suicide
            }
        } // 5 passed, 1 ignored

        @Nested
        inner class CountTest {
            @Test
            fun `count returns correct number for stepped range`() {
                val range = BigIntegerRange(1, 10) step 2
                assertEquals(5, range.count())
            }

            @Test
            fun `count returns 1 for singleton range`() {
                val range = BigIntegerRange(7, 7)
                assertEquals(1, range.count())
            }

            @Test
            fun `count for unstepped non-singleton range assumes step is 1`() {
                val range = BigIntegerRange(0, 10)
                assertEquals(11, range.count())
            }

            @Test
            fun `count returns Int for small range`() {
                val range = BigIntegerRange(0, 10) step 1
                val count = range.count()
                assertTrue(count is Int)
                assertEquals(11, count)
            }

            @Test
            fun `count returns Long when value exceeds Int range`() {
                val start = BigInteger.ZERO
                val end = BigInteger.valueOf(Int.MAX_VALUE.toLong() + 100L)
                val range = BigIntegerRange(start, end) step 1
                val count = range.count()
                assertTrue(count is Long)
                assertEquals(Int.MAX_VALUE.toLong() + 101L, count)
            }

            @Test
            fun `count returns BigInteger for huge range`() {
                val start = 0
                val end = BigInteger("100000000000000000000")
                val range = BigIntegerRange(start, end) step 1
                val count = range.count()
                assertTrue(count is BigInteger)
                assertEquals(BigInteger("100000000000000000001"), count)
            }
        } // 6 passed

        @Nested
        inner class RandomTest {
            @Test
            fun `random returns the only value for singleton range`() {
                val range = BigIntegerRange(5, 5)
                repeat(10) {
                    assertEquals(5.toBigInteger(), range.random())
                }
            }

            @Test
            fun `random returns a value within the unstepped range`() {
                val range = BigIntegerRange(1, 4)
                val expected = listOf(1, 2, 3, 4).map { it.toBigInteger() }

                repeat(50) {
                    val value = range.random()
                    assertTrue(value in expected) {
                        "Got unexpected value: $value from range $range"
                    }
                }
            }


            @Test
            fun `random returns a value within a stepped range`() {
                val range = BigIntegerRange(1, 10) step 3
                val expected = listOf(1, 4, 7, 10).map { it.toBigInteger() }

                repeat(50) {
                    val value = range.random()
                    assertTrue(value in expected)
                }
            }

            @Test
            fun `random returns a value within descending stepped range`() {
                val range = BigIntegerRange(10, 1) step -3
                val expected = listOf(10, 7, 4, 1).map { it.toBigInteger() }

                repeat(50) {
                    val value = range.random()
                    assertTrue(value in expected)
                }
            }
        } // 4 passed

        @Nested
        inner class ReversedTest {
            @Test
            fun `reverse of singleton returns same instance`() {
                val singleton = BigIntegerRange(3, 3)
                assertSame(singleton, singleton.reversed())
            }

            @Test
            fun `reverse swaps bounds and inclusivity`() {
                val original = BigIntegerRange(1, 5) step 1
                val reversed = original.reversed()

                assertEquals(BigIntegerRange(5, 1) step -1, reversed)
            }
        } // 2 passed

        @Nested
        inner class RangeToTest {
            @Test
            fun `creates a range using double-dot operator`() {
                assertEquals(BigIntegerRange(1, 10), BigInteger.ONE..BigInteger.TEN)
                assertEquals(BigIntegerRange(1, 10) step 2, BigInteger.ONE..BigInteger.TEN step 2)
                assertEquals(BigIntegerRange(10, 1), BigInteger.TEN..BigInteger.ONE)
                assertEquals(BigIntegerRange(10, 1) step -1, BigInteger.TEN..BigInteger.ONE step -1)
            }
        } // 1 passed
    }

    @Nested
    inner class BigIntegerRangePerformanceTest
}