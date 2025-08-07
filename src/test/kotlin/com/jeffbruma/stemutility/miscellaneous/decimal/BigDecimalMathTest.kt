package com.jeffbruma.stemutility.miscellaneous.decimal

import com.jeffbruma.stemutility.E
import com.jeffbruma.stemutility.LOG_2
import com.jeffbruma.stemutility.PI
import com.jeffbruma.stemutility.miscellaneous.SecureKotlinRandom
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

const val REPETITIONS = 100
private val mc32 = MathContext(32)
private val mc64 = MathContext(64)

class ExtensionPropertiesTest {
    val pi = getConstant("pi", mc32.precision + 2)
    @Test
    fun `squared returns the second power of a number`() {
        assertEquals(BigDecimal(4), BigDecimal.TWO.squared)
        assertEquals(BigDecimal(4), (-BigDecimal.TWO).squared)
        assertEquals(BigDecimal("9.8696044010893586188344909998762"), pi.squared.roundAndStrip(mc32))
    }

    @Test
    fun `cubed returns the third power of a number`() {
        assertEquals(BigDecimal(8), BigDecimal.TWO.cubed)
        assertEquals(BigDecimal(-8), (-BigDecimal.TWO).cubed)
        assertEquals(BigDecimal("31.006276680299820175476315067101"), pi.cubed.roundAndStrip(mc32))
    }

    @Test
    fun `sign returns a BigDecimal representation of the signum of a number`() {
        val positiveNumber = random(BigDecimal.ZERO, BigDecimal.TEN, inclusiveMin = false)
        val negativeNumber = random(-BigDecimal.TEN, BigDecimal.ZERO, inclusiveMax = false)
        assertEquals(BigDecimal.ONE, positiveNumber.sign)
        assertEquals(-BigDecimal.ONE, negativeNumber.sign)
        assertEquals(BigDecimal.ZERO, BigDecimal.ZERO.sign)
    }
}

class PredicatesTest {
    @Test
    fun `isInteger returns true if BigDecimal has zero scale`() {
        assertTrue(BigDecimal("3").isInteger())
        assertTrue(BigDecimal("3.000000000").isInteger())
        assertTrue(BigDecimal("0.00").isInteger())
        assertFalse(BigDecimal("3.14").isInteger())
        assertFalse(BigDecimal("-1.0001").isInteger())
    }

    @Test
    fun `isNegative returns true if BigDecimal is lesser than zero`() {
        assertTrue((-BigDecimal.ONE).isNegative())
        assertFalse(BigDecimal("0").isNegative())
        assertFalse(BigDecimal("1").isNegative())
    }

    @Test
    fun `isNegativeInteger returns true if BigDecimal is an integer lesser than zero`() {
        assertTrue((-BigDecimal.ONE).isNegativeInteger())
        assertFalse(BigDecimal.ONE.isNegativeInteger())
        assertFalse(BigDecimal("-1.6").isNegativeInteger())
    }

    @Test
    fun `isNonPositive returns true if BigDecimal is zero or lesser`() {
        assertTrue((-BigDecimal.ONE).isNonPositive())
        assertTrue(BigDecimal("0").isNonPositive())
        assertFalse(BigDecimal("1").isNonPositive())
    }

    @Test
    fun `isZero returns true if BigDecimal is exactly zero`() {
        assertTrue(BigDecimal("0").isZero())
        assertTrue(BigDecimal("0.000000").isZero())
        assertFalse(BigDecimal("1").isZero())
    }

    @Test
    fun `isNonNegative returns true if BigDecimal is zero or greater`() {
        assertTrue(BigDecimal("1").isNonNegative())
        assertTrue(BigDecimal("0").isNonNegative())
        assertFalse((-BigDecimal.ONE).isNonNegative())
    }

    @Test
    fun `isOne returns true if BigDecimal is exactly one`() {
        assertTrue(BigDecimal("1").isOne())
        assertFalse(BigDecimal("1.0000001").isOne())
        assertFalse(BigDecimal("0.999999").isOne())
    }

    @Test
    fun `isPositive returns true if BigDecimal is greater than zero`() {
        assertTrue(BigDecimal("1").isPositive())
        assertFalse(BigDecimal("0").isPositive())
        assertFalse((-BigDecimal.ONE).isPositive())
    }

    @Test
    fun `isPositiveInteger returns true if BigDecimal is an integer greater than zero`() {
        assertTrue(BigDecimal.ONE.isPositiveInteger())
        assertFalse((-BigDecimal.ONE).isPositiveInteger())
        assertFalse(BigDecimal("1.6").isPositiveInteger())
    }

    @Test
    fun `isEven returns true if BigDecimal is an integer divisible by two`() {
        assertTrue(BigDecimal.TWO.isEven())
        assertTrue(BigDecimal("2.0").isEven())
        assertTrue(BigDecimal("-4").isEven())
        assertFalse(BigDecimal("3").isEven())
        assertFalse(BigDecimal("3.1").isEven())
    }

    @Test
    fun `isOdd returns true if BigDecimal is an integer that is not even`() {
        assertTrue(BigDecimal("1").isOdd())
        assertTrue(BigDecimal("3").isOdd())
        assertTrue((-BigDecimal.ONE).isOdd())
        assertFalse(BigDecimal.TWO.isOdd())
        assertFalse(BigDecimal("2.5").isOdd())
    }
}

class BasicArithmeticTest {
    @Test
    fun `operator unaryMinus performs the negate method of BigDecimal`() {
        assertEquals(BigDecimal("-1"), -BigDecimal("1"))
        assertEquals(BigDecimal.ONE, -BigDecimal("-1"))
        assertEquals(BigDecimal("-3.141592654"), -BigDecimal("3.141592654"))
    }

    @Test
    fun `operator plus performs the add method of BigDecimal`() {
        assertEquals(BigDecimal("15"), BigDecimal("10") + BigDecimal("5"))
        assertEquals(BigDecimal("5"), BigDecimal("10") + BigDecimal("-5"))
        assertEquals(BigDecimal("10.5"), BigDecimal("10.2") + BigDecimal("0.3"))
    }

    @Test
    fun `operator minus performs the subtract method of BigDecimal`() {
        assertEquals(BigDecimal("5"), BigDecimal("10") - BigDecimal("5"))
        assertEquals(BigDecimal("15"), BigDecimal("10") - BigDecimal("-5"))
        assertEquals(BigDecimal("9.9"), BigDecimal("10.2") - BigDecimal("0.3"))
    }

    @Test
    fun `operator times performs the multiply method of BigDecimal`() {
        assertEquals(BigDecimal("50"), BigDecimal("10") * BigDecimal("5"))
        assertEquals(BigDecimal("-50"), BigDecimal("10") * BigDecimal("-5"))
        assertEquals(BigDecimal("0"), BigDecimal("10") * BigDecimal("0"))
        assertEquals(BigDecimal("3.06"), BigDecimal("10.2") * BigDecimal("0.3"))
    }

    @Test
    fun `operator div performs the divide method of BigDecimal`() {
        // Note: The division uses scale 10 and RoundingMode.HALF_UP
        assertEquals(BigDecimal.TWO, BigDecimal("10") / BigDecimal("5"))
        assertEquals(BigDecimal("-2"), BigDecimal("10") / BigDecimal("-5"))
        assertEquals(BigDecimal("3.3333333333333333333333333333333"), BigDecimal("10") / BigDecimal("3"))
        assertEquals(
            BigDecimal("6.6666666666666666666666666666667"),
            BigDecimal("20") / BigDecimal("3")
        ) // Test rounding
    }

    @Test
    fun `operator rem performs the remainder method of BigDecimal`() {
        assertEquals(BigDecimal("0"), BigDecimal("10") % BigDecimal("5"))
        assertEquals(BigDecimal("1"), BigDecimal("10") % BigDecimal("3"))
        assertEquals(-BigDecimal.ONE, BigDecimal("-10") % BigDecimal("3"))
        assertEquals(BigDecimal("1.2"), BigDecimal("10.2") % BigDecimal("3"))
    }

    @Test
    fun `abs returns the absolute value of a number`() {
        assertEquals(BigDecimal.ONE, abs(BigDecimal.ONE))
        assertEquals(BigDecimal.ONE, abs(-BigDecimal.ONE))
    }

    @Test
    fun `max returns the larger of two numbers`() {
        assertEquals(BigDecimal("10"), max(BigDecimal("10"), BigDecimal("5")))
        assertEquals(BigDecimal("10"), max(BigDecimal("5"), BigDecimal("10")))
        assertEquals(BigDecimal("10"), max(BigDecimal("10"), BigDecimal("10")))
        assertEquals(BigDecimal("-5"), max(BigDecimal("-10"), BigDecimal("-5")))
    }

    @Test
    fun `min returns the smaller of two numbers`() {
        assertEquals(BigDecimal("5"), min(BigDecimal("10"), BigDecimal("5")))
        assertEquals(BigDecimal("5"), min(BigDecimal("5"), BigDecimal("10")))
        assertEquals(BigDecimal("5"), min(BigDecimal("5"), BigDecimal("5")))
        assertEquals(BigDecimal("-10"), min(BigDecimal("-10"), BigDecimal("-5")))
    }

    @Test
    fun `ceil returns the smallest integer greater than or equal to the value`() {
        assertEquals(BigDecimal.TWO, BigDecimal("1.5").ceil())
        assertEquals(-BigDecimal.ONE, BigDecimal("-1.5").ceil())
    }

    @Test
    fun `floor returns the greatest integer less than or equal to the value`() {
        assertEquals(BigDecimal.ONE, BigDecimal("1.5").floor())
        assertEquals(-BigDecimal.TWO, BigDecimal("-1.5").floor())
    }

    @Test
    fun `nextUp returns the smallest increment greater than the input`() {
        assertEquals(BigDecimal("1.01"), BigDecimal("1.00").nextUp())
        assertEquals(BigDecimal("0.0001"), BigDecimal("0.0000").nextUp())
        assertTrue(BigDecimal("5.5").nextUp() > BigDecimal("5.5"))
    }

    @Test
    fun `nextDown returns the smallest increment lesser than the input`() {
        assertEquals(BigDecimal("1.00"), BigDecimal("1.01").nextDown())
        assertEquals(BigDecimal("0.0000"), BigDecimal("0.0001").nextDown())
        assertTrue(BigDecimal("5.5").nextDown() < BigDecimal("5.5"))
    }

    @Test
    fun `normalized cleans up trailing zeroes and corrects scale`() {
        val testNumber = BigDecimal("-3141592654.0000000000")
        assertEquals(BigDecimal.valueOf(-3141592654L), testNumber.normalized())
    }

    @Test
    fun `reciprocal returns one divided by the BigDecimal`() {
        assertEquals(BigDecimal("0.5"), BigDecimal.TWO.reciprocal())
        assertEquals(BigDecimal.TWO, BigDecimal("0.5").reciprocal())
        assertEquals(BigDecimal("3"), BigDecimal("3").reciprocal().reciprocal().roundAndStrip())
    }

    @Test
    fun `reciprocal throws when number is zero`() {
        val exception = assertThrows<ArithmeticException> {
            BigDecimal.ZERO.reciprocal(mc32)
        }

        assertEquals("Cannot take reciprocal of zero", exception.message)
    }
}

class PowerTest {

    @Test
    fun `32 significant-figure basic exponentiation`() {
        val pi = getConstant("pi", mc32.precision + 1)
        val anyBase: BigDecimal = listOf(
            BigDecimal("-4"), BigDecimal("4")
        ).random()
        val anyExponent = listOf(
            BigDecimal("-2"), BigDecimal("2"),
            BigDecimal("-3"), BigDecimal("3"),
        ).random()

        // positive base and positive integer exponent
        assertEquals(BigDecimal("64"), BigDecimal("4").pow(BigDecimal("3"), mc32))

        // positive base and zero exponent
        assertEquals(BigDecimal.ONE, BigDecimal("4").pow(BigDecimal.ZERO, mc32))

        // positive base and negative integer exponent
        assertEquals(BigDecimal("0.015625"), BigDecimal("4").pow(BigDecimal("-3"), mc32))

        // positive base and rational exponent
        assertEquals(BigDecimal.TWO, BigDecimal("4").pow(BigDecimal("0.5"), mc32))

        // positive one base and any exponent
        assertEquals(BigDecimal.ONE, BigDecimal.ONE.pow(anyExponent, mc32))

        // positive base and irrational exponent
        assertEquals(BigDecimal("8.824977827076287623856429604208"), BigDecimal.TWO.pow(pi, mc32))

        // positive base and irrational exponent
        assertEquals(BigDecimal("0.11331473229676087302028217221333"), BigDecimal.TWO.pow(-pi, mc32))

        // negative one base and positive odd integer exponent
        assertEquals(-BigDecimal.ONE, (-BigDecimal.ONE).pow(BigDecimal("3"), mc32))

        // negative one base amd positive even integer exponent
        assertEquals(BigDecimal.ONE, (-BigDecimal.ONE).pow(BigDecimal.TWO, mc32))

        // negative base and positive even integer exponent
        assertEquals(BigDecimal("16"), BigDecimal("-4").pow(BigDecimal.TWO, mc32))

        // negative base and positive odd integer exponent
        assertEquals(BigDecimal("-64"), BigDecimal("-4").pow(BigDecimal("3"), mc32))

        // negative base and positive rational exponent
        assertEquals(BigDecimal("-2"), BigDecimal("-8").pow(BigDecimal("3").reciprocal(mc32), mc32))

        // negative base and rational exponent with odd denominator
        assertEquals(BigDecimal("4"), BigDecimal("-8").pow(BigDecimal.TWO.divide(BigDecimal("3"), mc32), mc32))

        // any base and zero exponent
        assertEquals(BigDecimal.ONE, anyBase.pow(BigDecimal.ZERO, mc32))

        // zero base and positive exponent
        assertEquals(BigDecimal.ZERO, BigDecimal.ZERO.pow(BigDecimal("3"), mc32))

        // zero base and zero exponent
        assertEquals(BigDecimal.ONE, BigDecimal.ZERO.pow(BigDecimal.ZERO, mc32))
    }

    @Test
    fun `64 significant-figure basic exponentiation`() {
        val pi = getConstant("pi", mc64.precision + 1)
        val anyBase: BigDecimal = listOf(
            BigDecimal("-4"), BigDecimal("4")
        ).random()
        val anyExponent = listOf(
            BigDecimal("-2"), BigDecimal("2"),
            BigDecimal("-3"), BigDecimal("3"),
        ).random()

        // positive base and positive integer exponent
        assertEquals(BigDecimal("64"), BigDecimal("4").pow(BigDecimal("3"), mc64))

        // positive base and zero exponent
        assertEquals(BigDecimal.ONE, BigDecimal("4").pow(BigDecimal.ZERO, mc64))

        // positive base and negative integer exponent
        assertEquals(BigDecimal("0.015625"), BigDecimal("4").pow(BigDecimal("-3"), mc64))

        // positive base and rational exponent
        assertEquals(BigDecimal.TWO, BigDecimal("4").pow(BigDecimal("0.5"), mc64))

        // positive one base and any exponent
        assertEquals(BigDecimal.ONE, BigDecimal.ONE.pow(anyExponent, mc64))

        // positive base and irrational exponent
        assertEquals(BigDecimal("8.824977827076287623856429604208001581704410815271484926668959865"), BigDecimal.TWO.pow(pi, mc64))

        // positive base and irrational exponent
        assertEquals(BigDecimal("0.113314732296760873020282172213329377896121456442127822633075062"), BigDecimal.TWO.pow(-pi, mc64))

        // negative one base and positive odd integer exponent
        assertEquals(-BigDecimal.ONE, (-BigDecimal.ONE).pow(BigDecimal("3"), mc64))

        // negative one base amd positive even integer exponent
        assertEquals(BigDecimal.ONE, (-BigDecimal.ONE).pow(BigDecimal.TWO, mc64))

        // negative base and positive even integer exponent
        assertEquals(BigDecimal("16"), BigDecimal("-4").pow(BigDecimal.TWO, mc64))

        // negative base and positive odd integer exponent
        assertEquals(BigDecimal("-64"), BigDecimal("-4").pow(BigDecimal("3"), mc64))

        // negative base and positive rational exponent
        assertEquals(BigDecimal("-2"), BigDecimal("-8").pow(BigDecimal("3").reciprocal(mc64), mc64))

        // negative base and rational exponent with odd denominator
        assertEquals(BigDecimal("4"), BigDecimal("-8").pow(BigDecimal.TWO.divide(BigDecimal("3"), mc64), mc64))

        // any base and zero exponent
        assertEquals(BigDecimal.ONE, anyBase.pow(BigDecimal.ZERO, mc64))

        // zero base and positive exponent
        assertEquals(BigDecimal.ZERO, BigDecimal.ZERO.pow(BigDecimal("3"), mc64))

        // zero base and zero exponent
        assertEquals(BigDecimal.ONE, BigDecimal.ZERO.pow(BigDecimal.ZERO, mc64))
    }

    @Test
    fun `pow throws on negative base and irrational exponent`() {
        val pi = getConstant("pi", mc32.precision + 1)
        val exception = assertThrows<ArithmeticException> {
            (-BigDecimal.TWO).pow(pi, mc32)
        }
        assertEquals("Irrational power of negative base is undefined in ℝ", exception.message)
    }

    @Test
    fun `pow throws on zero base and negative exponent`() {
        val exception = assertThrows<ArithmeticException> {
            BigDecimal.ZERO.pow(BigDecimal("-3"), mc32)
        }
        assertEquals("Zero cannot be raised to a negative power", exception.message)
    }

    @Test
    fun `pow throws on negative base and rational exponent with even denominator`() {
        val exception = assertThrows<ArithmeticException> {
            BigDecimal("-8").pow(BigDecimal("1.5"), mc32)
        }
        assertEquals("Even root of negative base is undefined in ℝ", exception.message)
    }

    @Test
    fun `pow performance`() {
        val testBase = BigDecimal("1.6069380442589902755419620923409E+60")
        val testPower = BigDecimal("2")
        performanceTest {
            testBase.pow(testPower, mc32)
        }.apply {
            println("Maximum: $maximum") // 327.6us
            println("Average: $average") // 29.263us
            assertTrue(maximum < 10.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `rationalize performance`() {
        val testNumber = BigDecimal("3").divide(BigDecimal("4"), mc32)
        performanceTest { rationalize(testNumber, mc32) }.apply {
            println("Max: $maximum") // 520.7us
            println("Ave: $average") // 116.89us
        }
    }
}

class RootTest  {
    @Test
    fun `32 significant-figure rooting`() {
        // positive number, positive degree
        assertEquals(BigDecimal("3"), BigDecimal("27").root(3, mc32))

        // root of zero
        assertEquals(BigDecimal.ZERO, BigDecimal.ZERO.root(3, mc32))

        // root of one
        assertEquals(BigDecimal.ONE, BigDecimal.ONE.root(5, mc32))

        // degree is one
        assertEquals(BigDecimal("123.456"), BigDecimal("123.456").root(1, mc32))

        // odd root of negative number
        assertEquals(BigDecimal("-3"), BigDecimal("-27").root(3, mc32))

        // even root of negative number should throw exception
        assertThrows<ArithmeticException> { BigDecimal("-16").root(2, mc32) }

        // invalid root degree (zero) should throw exception
        assertThrows<ArithmeticException> { BigDecimal("8").root(0, mc32) }

        // floating point precision test (sqrt(2))
        assertEquals(BigDecimal("1.4142135623730950488016887242097"), BigDecimal.TWO.root(2, mc32))
    }

    @Test
    fun `64 significant-figure rooting`() {
        // positive number, positive degree
        assertEquals(BigDecimal("3"), BigDecimal("27").root(3, mc64))

        // root of zero
        assertEquals(BigDecimal.ZERO, BigDecimal.ZERO.root(3, mc64))

        // root of one
        assertEquals(BigDecimal.ONE, BigDecimal.ONE.root(5, mc64))

        // degree is one
        assertEquals(BigDecimal("123.456"), BigDecimal("123.456").root(1, mc64))

        // odd root of negative number
        assertEquals(BigDecimal("-3"), BigDecimal("-27").root(3, mc64))

        // even root of negative number should throw exception
        assertThrows<ArithmeticException> { BigDecimal("-16").root(2, mc64) }

        // invalid root degree (zero) should throw exception
        assertThrows<ArithmeticException> { BigDecimal("8").root(0, mc64) }

        // floating point precision test (sqrt(2))
        assertEquals(BigDecimal("1.414213562373095048801688724209698078569671875376948073176679738"), BigDecimal.TWO.root(2, mc64))
    }

    @Test
    fun `32 significant-figure square-rooting`() {
        // Square root of a perfect square
        assertEquals(BigDecimal("5"), squareRoot(BigDecimal("25"), mc32))

        // Square root of 0
        assertEquals(BigDecimal.ZERO, squareRoot(BigDecimal.ZERO, mc32))

        // Square root of 1
        assertEquals(BigDecimal.ONE, squareRoot(BigDecimal.ONE, mc32))

        // Square root of a non-perfect square (with approximate expected value)
        assertEquals(BigDecimal("1.4142135623730950488016887242097"), squareRoot(BigDecimal.TWO, mc32))

        // Square root of a large number
        assertEquals(BigDecimal("1E+10"), squareRoot(BigDecimal("1E+20"), mc32))

        // Square root of a small number
        assertEquals(BigDecimal("0.031622776601683793319988935444327"), squareRoot(BigDecimal("0.001"), mc32))

        // Square root of a negative number should throw
        assertThrows<ArithmeticException> { squareRoot(BigDecimal("-4"), mc32) }
    }

    @Test
    fun `64 significant-figure square-rooting`() {
        // Square root of a perfect square
        assertEquals(BigDecimal("5"), squareRoot(BigDecimal("25"), mc64))

        // Square root of 0
        assertEquals(BigDecimal.ZERO, squareRoot(BigDecimal.ZERO, mc64))

        // Square root of 1
        assertEquals(BigDecimal.ONE, squareRoot(BigDecimal.ONE, mc64))

        // Square root of a non-perfect square (with approximate expected value)
        assertEquals(BigDecimal("1.414213562373095048801688724209698078569671875376948073176679738"), squareRoot(BigDecimal.TWO, mc64))

        // Square root of a large number
        assertTrue(BigDecimal("1E+10").compareTo(squareRoot(BigDecimal("1E+20"), mc64)) == 0)

        // Square root of a small number
        assertEquals(BigDecimal("0.03162277660168379331998893544432718533719555139325216826857504853"), squareRoot(BigDecimal("0.001"), mc64))

        // Square root of a negative number should throw
        assertThrows<ArithmeticException> { squareRoot(BigDecimal("-4"), mc64) }
    }

    @Test
    fun `32 significant-figure cube-rooting`() {
        // Cube root of a perfect cube
        assertEquals(BigDecimal.TWO, cubeRoot(BigDecimal("8"), mc32))
        assertEquals(-BigDecimal.TWO, cubeRoot(BigDecimal("-8"), mc32))

        // Cube root of 0
        assertEquals(BigDecimal.ZERO, cubeRoot(BigDecimal.ZERO, mc32))

        // Cube root of 1
        assertEquals(BigDecimal.ONE, cubeRoot(BigDecimal.ONE, mc32))

        // Cube root of -1
        assertEquals(-BigDecimal.ONE, cubeRoot(-BigDecimal.ONE, mc32))

        // Cube root of a non-perfect square (with approximate expected value)
        assertEquals(BigDecimal("1.2599210498948731647672106072782"), cubeRoot(BigDecimal.TWO, mc32))
        assertEquals(BigDecimal("-1.2599210498948731647672106072782"), cubeRoot(-BigDecimal.TWO, mc32))

        // Cube root of a large number
        assertEquals(BigDecimal("1E+10"), cubeRoot(BigDecimal("1E+30"), mc32))
        assertEquals(BigDecimal("-1E+10"), cubeRoot(BigDecimal("-1E+30"), mc32))

        // Cube root of a small number
        assertEquals(BigDecimal("0.1"), cubeRoot(BigDecimal("0.001"), mc32))
        assertEquals(BigDecimal("-0.1"), cubeRoot(BigDecimal("-0.001"), mc32))
    }

    @Test
    fun `64 significant-figure cube-rooting`() {
        // Cube root of a perfect cube
        assertEquals(BigDecimal.TWO, cubeRoot(BigDecimal("8"), mc64))
        assertEquals(-BigDecimal.TWO, cubeRoot(BigDecimal("-8"), mc64))

        // Cube root of 0
        assertEquals(BigDecimal.ZERO, cubeRoot(BigDecimal.ZERO, mc64))

        // Cube root of 1
        assertEquals(BigDecimal.ONE, cubeRoot(BigDecimal.ONE, mc64))

        // Cube root of -1
        assertEquals(-BigDecimal.ONE, cubeRoot(-BigDecimal.ONE, mc64))

        // Cube root of a non-perfect square (with approximate expected value)
        assertEquals(BigDecimal("1.259921049894873164767210607278228350570251464701507980081975112"), cubeRoot(BigDecimal.TWO, mc64))
        assertEquals(BigDecimal("-1.259921049894873164767210607278228350570251464701507980081975112"), cubeRoot(-BigDecimal.TWO, mc64))

        // Cube root of a large number
        assertEquals(BigDecimal("1E+10"), cubeRoot(BigDecimal("1E+30"), mc64))
        assertEquals(BigDecimal("-1E+10"), cubeRoot(BigDecimal("-1E+30"), mc64))

        // Cube root of a small number
        assertEquals(BigDecimal("0.1"), cubeRoot(BigDecimal("0.001"), mc64))
        assertEquals(BigDecimal("-0.1"), cubeRoot(BigDecimal("-0.001"), mc64))
    }
}

class ExponentialTest {
    @Test
    fun `basic exponentiation`() {
        // exp(0) == 1
        assertEquals(BigDecimal.ONE, exp(BigDecimal.ZERO))

        // exp(1) ≈ 2.7182818284590452353602874713527
        assertEquals(getConstant("e", mc32.precision), exp(BigDecimal.ONE, mc32))

        // exp(-1) ≈ 0.36787944117144232159552377016146
        assertEquals(getConstant("e").reciprocal(mc32), exp(-BigDecimal.ONE, mc32))
    }

    @Test
    fun `32 significant-figure calculation`() {

        // e^2 ≈ 7.389056098930650227230427460575
        assertEquals(BigDecimal("7.389056098930650227230427460575"), exp(BigDecimal.TWO, mc32))

        // e^-2 = 0.13533528323661269189399949497248
        assertEquals(BigDecimal("0.13533528323661269189399949497248"), exp(-BigDecimal.TWO, mc32))

        // e^10 ≈ 22,026.465794806716516957900645284
        assertEquals(BigDecimal("22026.465794806716516957900645284"), exp(BigDecimal("10"), mc32))

        // e^-10 ≈ 0.000045399929762484851535591515560551
        assertEquals(BigDecimal("4.5399929762484851535591515560551E-5"), exp(BigDecimal("-10"), mc32))

        // e^4,000,000,000 = 4.1021100812893056193851470091437E+1737177927
        assertEquals(BigDecimal("4.1021100812893056193851470091437E+1737177927"), exp(BigDecimal("4E+9"), mc32))

        // e^-4,000,000,000 = 2.4377697823401583901087137763094E-1737177928
        assertEquals(BigDecimal("2.4377697823401583901087137763094E-1737177928"), exp(BigDecimal("-4E+9"), mc32))

        // e^ln(2) == 2
        assertEquals(BigDecimal.TWO, exp(getConstant("log_2", mc32.precision + 1), mc32))

        // e^-ln(2) == 1/2
        assertEquals(BigDecimal("0.5"), exp(-getConstant("log_2", mc32.precision + 1), mc32))
    }

    @Test
    fun `64 significant-figure calculation`() {

        // e^2 ≈ 7.389056098930650227230427460575
        assertEquals(BigDecimal("7.389056098930650227230427460575007813180315570551847324087127823"), exp(BigDecimal.TWO, mc64))

        // e^-2 = 0.13533528323661269189399949497248
        assertEquals(BigDecimal("0.1353352832366126918939994949724844034076315459095758814681588727"), exp(-BigDecimal.TWO, mc64))

        // e^10 ≈ 22,026.465794806716516957900645284
        assertEquals(BigDecimal("22026.46579480671651695790064528424436635351261855678107423542636"), exp(BigDecimal("10"), mc64))

        // e^-10 ≈ 0.000045399929762484851535591515560551
        assertEquals(BigDecimal("4.539992976248485153559151556055061023791808886656496925907130565E-5"), exp(BigDecimal("-10"), mc64))

        // e^4,000,000,000 = 4.1021100812893056193851470091437E+1737177927
        assertEquals(BigDecimal("4.102110081289305619385147009143678121997159142766017529069929482e+1737177927"), exp(BigDecimal("4E+9"), mc64))

        // e^-4,000,000,000 = 2.4377697823401583901087137763094E-1737177928
        assertEquals(BigDecimal("2.437769782340158390108713776309409546273532846510596562554489152e-1737177928"), exp(BigDecimal("-4E+9"), mc64))

        // e^ln(2) == 2
        assertEquals(BigDecimal.TWO, exp(getConstant("log_2", mc64.precision + 1), mc64))

        // e^-ln(2) == 1/2
        assertEquals(BigDecimal("0.5"), exp(-getConstant("log_2", mc64.precision + 1), mc64))
    }

    @Test
    fun `128 significant figure calculation`() {
        val context = MathContext(128)
        assertEquals(BigDecimal.ONE, exp(BigDecimal.ONE.movePointLeft(context.precision), context))
    }

    @Test
    fun `exp throws when math context is unlimited`() {
        val pi = getConstant("pi", mc32.precision + 1)
        val exception = assertThrows<IllegalArgumentException> {
            exp(pi, MathContext.UNLIMITED)
        }

        assertEquals("Invalid MathContext", exception.message)
    }

    @Test
    fun `exp performance`() {
        val testNumber = BigDecimal("4E+9")
        performanceTest { exp(testNumber, mc32) }.apply {
            println("Max: $maximum")
            println("Ave: $average")
            assertTrue(maximum < 10.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

}

class LogarithmTest {
    @Test
    fun `32 significant-figure natural logarithm`() {
        // log(1) = 0
        assertEquals(BigDecimal.ZERO, log(BigDecimal.ONE, mc32))

        // log(e) = 1
        assertEquals(BigDecimal.ONE, log(BigDecimal("2.7182818284590452353602874713527"), mc32))

        // log(10) ≈ 2.3025850929940456840179914546844
        assertEquals(BigDecimal("2.3025850929940456840179914546844"), log(BigDecimal.TEN, mc32))

        // log(100) ≈ 4.6051701859880913680359829093687
        assertEquals(BigDecimal("4.6051701859880913680359829093687"), log(BigDecimal("100"), mc32))

        // log(2) ≈ 0.69314718055994530941723212145818
        assertEquals(BigDecimal("0.69314718055994530941723212145818"), log(BigDecimal.TWO, mc32))

        // log(0.5) ≈ -0.69314718055994530941723212145818
        assertEquals(BigDecimal("-0.69314718055994530941723212145818"), log(BigDecimal("0.5"), mc32))

        // log of number very close to 1
        assertEquals(BigDecimal("9.9999999999999995E-17"), log(BigDecimal("1.0000000000000001"), mc32))
    }

    @Test
    fun `64 significant-figure natural logarithm`() {
        // log(1) = 0
        assertEquals(BigDecimal.ZERO, log(BigDecimal.ONE, mc64))

        // log(e) = 1
        assertEquals(BigDecimal.ONE, log(BigDecimal("2.718281828459045235360287471352662497757247093699959574966967628"), mc64))

        // log(10) ≈ 2.3025850929940456840179914546844
        assertEquals(BigDecimal("2.302585092994045684017991454684364207601101488628772976033327901"), log(BigDecimal.TEN, mc64))

        // log(100) ≈ 4.6051701859880913680359829093687
        assertEquals(BigDecimal("4.605170185988091368035982909368728415202202977257545952066655802"), log(BigDecimal("100"), mc64))

        // log(2) ≈ 0.69314718055994530941723212145818
        assertEquals(BigDecimal("0.6931471805599453094172321214581765680755001343602552541206800095"), log(BigDecimal.TWO, mc64))

        // log(0.5) ≈ -0.69314718055994530941723212145818
        assertEquals(BigDecimal("-0.6931471805599453094172321214581765680755001343602552541206800095"), log(BigDecimal("0.5"), mc64))

        // log of number very close to 1
        assertEquals(BigDecimal("9.999999999999999500000000000000033333333333333330833333333333334E-17"), log(BigDecimal("1.0000000000000001"), mc64))
    }

    @Test
    fun `log of a negative number throws`() {
        // log of a negative number throws
        assertThrows<ArithmeticException> { log(-BigDecimal.ONE, mc32) }
    }

    @Test
    fun `log of zero throws`() {
        // log(0) throws
        assertThrows<ArithmeticException> { log(BigDecimal.ZERO, mc32) }
    }

    @Test
    fun `32 significant-figure base-two logarithm`() {
        assertEquals(BigDecimal.ONE, logTwo(BigDecimal.TWO, mc32))
        assertEquals(BigDecimal("1E+1"), logTwo(BigDecimal("1024"), mc32))
        assertEquals(BigDecimal("1E+2"), logTwo(BigDecimal("1267650600228229401496703205376"), mc32))
        assertEquals(BigDecimal("1.5849625007211561814537389439478"), logTwo(BigDecimal("3"), mc32))
        assertEquals(BigDecimal("-1.5849625007211561814537389439478"), logTwo(BigDecimal("3").reciprocal(mc32), mc32))
    }

    @Test
    fun `64 significant-figure base-two logarithm`() {
        assertEquals(BigDecimal.ONE, logTwo(BigDecimal.TWO, mc64))
        assertEquals(BigDecimal("1E+1"), logTwo(BigDecimal("1024"), mc64))
        assertEquals(BigDecimal("1E+2"), logTwo(BigDecimal("1267650600228229401496703205376"), mc64))
        assertEquals(BigDecimal("1.584962500721156181453738943947816508759814407692481060455752655"), logTwo(BigDecimal("3"), mc64))
        assertEquals(BigDecimal("-1.584962500721156181453738943947816508759814407692481060455752655"), logTwo(BigDecimal("3").reciprocal(mc64), mc64))
    }

    @Test
    fun `32 significant-figure base-ten logarithm`() {
        assertEquals(BigDecimal("1E+0"), logTen(BigDecimal.TEN, mc32))
        assertEquals(BigDecimal("1E+1"), logTen(BigDecimal("1E+10"), mc32))
        assertEquals(BigDecimal("1E+2"), logTen(BigDecimal("1E+100"), mc32))
        assertEquals(BigDecimal("0.47712125471966243729502790325512"), logTen(BigDecimal("3"), mc32))
        assertEquals(BigDecimal("-0.47712125471966243729502790325512"), logTen(BigDecimal("3").reciprocal(mc32), mc32))
    }

    @Test
    fun `64 significant-figure base-ten logarithm`() {
        assertEquals(BigDecimal.ONE, logTen(BigDecimal.TEN, mc64))
        assertEquals(BigDecimal("1E+1"), logTen(BigDecimal("1E+10"), mc64))
        assertEquals(BigDecimal("1E+2"), logTen(BigDecimal("1E+100"), mc64))
        assertEquals(BigDecimal("0.4771212547196624372950279032551153092001288641906958648298656403"), logTen(BigDecimal("3"), mc64))
        assertEquals(BigDecimal("-0.4771212547196624372950279032551153092001288641906958648298656403"), logTen(BigDecimal("3").reciprocal(mc64), mc64))
    }

    @Test
    fun `log performance`() {
        val testNumber = BigDecimal("1E+19")
        val mc = MathContext(34)
        val agmMethod = performanceTest { log(testNumber, mc) }
        println("Maximum Time: ${agmMethod.maximum}")
        println("Average Time: ${agmMethod.average}")
        assertTrue(agmMethod.maximum < 100.milliseconds)
        assertTrue(agmMethod.average < 100.milliseconds)
    }
}

class HyperbolicFunctionsTest {

    @Test
    fun `32 significant-figure sinh`() {
        // 0
        assertEquals(BigDecimal.ZERO, sinh(BigDecimal.ZERO, mc32))

        // 1
        assertEquals(BigDecimal("1.1752011936438014568823818505956"), sinh(BigDecimal.ONE, mc32))

        // -1
        assertEquals(BigDecimal("-1.1752011936438014568823818505956"), sinh(-BigDecimal.ONE, mc32))

        // small positive values
        assertEquals(BigDecimal("1.0000000000000000000016666666667E-10"), sinh(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("1.0000000000000000000000166666667E-11"), sinh(BigDecimal("1E-11"), mc32))

        // small negative values
        assertEquals(BigDecimal("-1.0000000000000000000016666666667E-10"), sinh(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("-1.0000000000000000000000166666667E-11"), sinh(BigDecimal("-1E-11"), mc32))

        // large numbers
        assertEquals(BigDecimal("4.0014908853304862665209546871825E+434294481"), sinh(BigDecimal("1E+9"), mc32))
        assertEquals(BigDecimal("-4.0014908853304862665209546871825E+434294481"), sinh(BigDecimal("-1E+9"), mc32))

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.12345678901234567890123456789012")
        val expected = sinh(x, mc32)  // with rounding (your current function)
        val actual = sinh(x, mc32, rounding = false)
        // The unrounded should be at least as precise or very close
        assertTrue(abs(expected - actual) < BigDecimal("1E-31"))

        // Anti-symmetry test
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(-sinh(x, mc32), sinh(-x, mc32), "sinh(-$x) should be -sinh($x)")
        }
    }

    @Test
    fun `64 significant-figure sinh`() {
        // 0
        assertEquals(BigDecimal.ZERO, sinh(BigDecimal.ZERO, mc64))

        // 1
        assertEquals(BigDecimal("1.175201193643801456882381850595600815155717981334095870229565413"), sinh(BigDecimal.ONE, mc64))

        // -1
        assertEquals(BigDecimal("-1.175201193643801456882381850595600815155717981334095870229565413"), sinh(-BigDecimal.ONE, mc64))

        // small positive values
        assertEquals(BigDecimal("1.0000000000000000000016666666666666666666675E-10"), sinh(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("1.00000000000000000000001666666666666666666666675E-11"), sinh(BigDecimal("1E-11"), mc64))

        // small negative values
        assertEquals(BigDecimal("-1.0000000000000000000016666666666666666666675E-10"), sinh(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("-1.00000000000000000000001666666666666666666666675E-11"), sinh(BigDecimal("-1E-11"), mc64))

        // large numbers
        assertEquals(BigDecimal("4.001490885330486266520954687182500344391157498588187282678222737E+434294481"), sinh(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("-4.001490885330486266520954687182500344391157498588187282678222737E+434294481"), sinh(BigDecimal("-1E+9"), mc64))
        // 4.001490885330486266520954687182500344391157498588187282678222737E+434294481

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.1234567890123456789012345678901234567890123456789012345678901234")
        val expected = sinh(x, mc64)  // with rounding (your current function)
        val actual = sinh(x, mc64, rounding = false)
        // The unrounded should be at least as precise or very close
        assertTrue(abs(expected - actual) < BigDecimal("1E-63"))

        // Anti-symmetry test
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(-sinh(x, mc64), sinh(-x, mc64), "sinh(-$x) should be -sinh($x)")
        }
    }

    @Test
    fun `sinh performance`() {
        performanceTest { sinh(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 1.035399ms
            println("Ave: $average") // 363.206us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure cosh`() {
        // 0
        assertEquals(BigDecimal.ONE, cosh(BigDecimal.ZERO, mc32))

        // 1
        assertEquals(BigDecimal("1.5430806348152437784779056207571"), cosh(BigDecimal.ONE, mc32))

        // -1 (cosh is even function, so cosh(-1) == cosh(1))
        assertEquals(BigDecimal("1.5430806348152437784779056207571"), cosh(-BigDecimal.ONE, mc32))

        // small positive values
        assertEquals(BigDecimal("1.000000000000000000005"), cosh(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("1.00000000000000000000005"), cosh(BigDecimal("1E-11"), mc32))

        // small negative values (cosh is even)
        assertEquals(BigDecimal("1.000000000000000000005"), cosh(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("1.00000000000000000000005"), cosh(BigDecimal("-1E-11"), mc32))

        // large numbers
        assertEquals(BigDecimal("4.0014908853304862665209546871825E+434294481"), cosh(BigDecimal("1E+9"), mc32))
        assertEquals(BigDecimal("4.0014908853304862665209546871825E+434294481"), cosh(BigDecimal("-1E+9"), mc32))
        // 4.0014908853304862665209546871825E+434294481

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.12345678901234567890123456789012")
        val expected = cosh(x, mc32)  // with rounding (your current function)
        val actual = cosh(x, mc32, rounding = false)
        assertTrue(abs(expected - actual) < BigDecimal("1E-31"))

        // Evenness test: cosh(-x) == cosh(x)
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(cosh(x, mc32), cosh(-x, mc32), "cosh(-$x) should be cosh($x)")
        }
    }

    @Test
    fun `64 significant-figure cosh`() {
        // 0
        assertEquals(BigDecimal.ONE, cosh(BigDecimal.ZERO, mc64))

        // 1
        assertEquals(BigDecimal("1.543080634815243778477905620757061682601529112365863704737402215"), cosh(BigDecimal.ONE, mc64))

        // -1 (cosh is even function, so cosh(-1) == cosh(1))
        assertEquals(BigDecimal("1.543080634815243778477905620757061682601529112365863704737402215"), cosh(-BigDecimal.ONE, mc64))

        // small positive values
        assertEquals(BigDecimal("1.000000000000000000005000000000000000000004166666666666666666668"), cosh(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("1.000000000000000000000050000000000000000000000416666666666666667"), cosh(BigDecimal("1E-11"), mc64))

        // small negative values (cosh is even)
        assertEquals(BigDecimal("1.000000000000000000005000000000000000000004166666666666666666668"), cosh(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("1.000000000000000000000050000000000000000000000416666666666666667"), cosh(BigDecimal("-1E-11"), mc64))

        // large numbers
        assertEquals(BigDecimal("4.001490885330486266520954687182500344391157498588187282678222737E+434294481"), cosh(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("4.001490885330486266520954687182500344391157498588187282678222737E+434294481"), cosh(BigDecimal("-1E+9"), mc64))
        // 4.001490885330486266520954687182500344391157498588187282678222736E+434294481

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.1234567890123456789012345678901234567890123456789012345678901234")
        val expected = cosh(x, mc64)  // with rounding (your current function)
        val actual = cosh(x, mc64, rounding = false)
        assertTrue(abs(expected - actual) < BigDecimal("1E-63"))

        // Evenness test: cosh(-x) == cosh(x)
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(cosh(x, mc64), cosh(-x, mc64), "cosh(-$x) should be cosh($x)")
        }
    }

    @Test
    fun `cosh performance`() {
        performanceTest { cosh(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 1.621300ms
            println("Ave: $average") // 455.93us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure tanh`() {
        // 0
        assertEquals(BigDecimal.ZERO, tanh(BigDecimal.ZERO, mc32))

        // 1
        assertEquals(BigDecimal("0.76159415595576488811945828260479"), tanh(BigDecimal.ONE, mc32))

        // -1
        assertEquals(BigDecimal("-0.76159415595576488811945828260479"), tanh(-BigDecimal.ONE, mc32))

        // small positive values
        assertEquals(BigDecimal("9.9999999999999999999666666666667E-11"), tanh(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("9.9999999999999999999996666666667E-12"), tanh(BigDecimal("1E-11"), mc32))

        // small negative values
        assertEquals(BigDecimal("-9.9999999999999999999666666666667E-11"), tanh(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("-9.9999999999999999999996666666667E-12"), tanh(BigDecimal("-1E-11"), mc32))

        // large numbers
        assertEquals(BigDecimal("0.99999999587769276361959283713828"), tanh(BigDecimal("10"), mc32))
        assertEquals(BigDecimal("-0.99999999587769276361959283713828"), tanh(BigDecimal("-10"), mc32))

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.12345678901234567890123456789012")
        val expected = tanh(x, mc32)  // with rounding (your default)
        val actual = tanh(x, mc32, rounding = false)
        assertTrue(abs(expected - actual) < BigDecimal("1E-31"))

        // Odd function property: tanh(-x) = -tanh(x)
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(-tanh(x, mc32), tanh(-x, mc32), "tanh(-$x) should be -tanh($x)")
        }

        // Identity check: tanh(x) = sinh(x) / cosh(x)
        for (x in values) {
            val sinhX = sinh(x, mc32, rounding = false)
            val coshX = cosh(x, mc32, rounding = false)
            val tanhX = tanh(x, mc32, rounding = false)
            val expectedTanh = sinhX.divide(coshX, mc32)
            assertTrue(abs(tanhX - expectedTanh) < BigDecimal("1E-31"), "tanh($x) ≈ sinh($x)/cosh($x)")
        }
    }

    @Test
    fun `64 significant-figure tanh`() {
        // 0
        assertEquals(BigDecimal.ZERO, tanh(BigDecimal.ZERO, mc64))

        // 1
        assertEquals(BigDecimal("7.615941559557648881194582826047935904127685972579365515968105001E-1"), tanh(BigDecimal.ONE, mc64))

        // -1
        assertEquals(BigDecimal("-7.615941559557648881194582826047935904127685972579365515968105001E-1"), tanh(-BigDecimal.ONE, mc64))

        // small positive values
        assertEquals(BigDecimal("9.99999999999999999996666666666666666666679999999999999999999946E-11"), tanh(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("9.99999999999999999999966666666666666666666668E-12"), tanh(BigDecimal("1E-11"), mc64))

        // small negative values
        assertEquals(BigDecimal("-9.99999999999999999996666666666666666666679999999999999999999946E-11"), tanh(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("-9.99999999999999999999966666666666666666666668E-12"), tanh(BigDecimal("-1E-11"), mc64))

        // large numbers
        assertEquals(BigDecimal("1"), tanh(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("-1"), tanh(BigDecimal("-1E+9"), mc64))

        // Compare with rounding disabled for high precision
        val x = BigDecimal("0.1234567890123456789012345678901234567890123456789012345678901234")
        val expected = tanh(x, mc64)  // with rounding (your default)
        val actual = tanh(x, mc64, rounding = false)
        assertTrue(abs(expected - actual) < BigDecimal("1E-63"))

        // Odd function property: tanh(-x) = -tanh(x)
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TEN,
            BigDecimal("0.1"),
            BigDecimal("1E-10"),
            BigDecimal("123.456")
        )
        for (x in values) {
            assertEquals(-tanh(x, mc64), tanh(-x, mc64), "tanh(-$x) should be -tanh($x)")
        }

        // Identity check: tanh(x) = sinh(x) / cosh(x)
        for (x in values) {
            val sinhX = sinh(x, mc64, rounding = false)
            val coshX = cosh(x, mc64, rounding = false)
            val tanhX = tanh(x, mc64, rounding = false)
            val expectedTanh = sinhX.divide(coshX, mc64)
            assertTrue(abs(tanhX - expectedTanh) < BigDecimal("1E-63"), "tanh($x) ≈ sinh($x)/cosh($x)")
        }
    }

    @Test
    fun `tanh performance`() {
        performanceTest { tanh(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 1.074ms
            println("Ave: $average") // 429.318us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure csch`() {
        // 0
        assertThrows<ArithmeticException> { csch(BigDecimal.ZERO, mc32) }

        // 1
        assertEquals(BigDecimal("8.5091812823932154513384276328718E-1"), csch(BigDecimal.ONE, mc32))

        // -1 (odd function)
        assertEquals(BigDecimal("-8.5091812823932154513384276328718E-1"), csch(-BigDecimal.ONE, mc32))

        // Small positive values
        assertEquals(BigDecimal("9.9999999999999999999833333333333E+9"), csch(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("9.9999999999999999999998333333333E+10"), csch(BigDecimal("1E-11"), mc32))

        // small negative values
        assertEquals(BigDecimal("-9.9999999999999999999833333333333E+9"), csch(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("-9.9999999999999999999998333333333E+10"), csch(BigDecimal("-1E-11"), mc32))

        // large values
        assertEquals(BigDecimal("2.49906854384202656184875699803E-434294482"), csch(BigDecimal("1E+9"), mc32))
        assertEquals(BigDecimal("-2.49906854384202656184875699803E-434294482"), csch(BigDecimal("-1E+9"), mc32))
    }

    @Test
    fun `64 significant-figure csch`() {
        // 0
        assertThrows<ArithmeticException> { csch(BigDecimal.ZERO, mc64) }

        // 1
        assertEquals(BigDecimal("8.509181282393215451338427632871752841817246609103396169904211517E-1"), csch(BigDecimal.ONE, mc64))

        // -1 (odd function)
        assertEquals(BigDecimal("-8.509181282393215451338427632871752841817246609103396169904211517E-1"), csch(-BigDecimal.ONE, mc64))

        // Small positive values
        assertEquals(BigDecimal("9.999999999999999999983333333333333333333352777777777777777777757E+9"), csch(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("9.999999999999999999999833333333333333333333335277777777777777778E+10"), csch(BigDecimal("1E-11"), mc64))

        // small negative values
        assertEquals(BigDecimal("-9.999999999999999999983333333333333333333352777777777777777777757E+9"), csch(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("-9.999999999999999999999833333333333333333333335277777777777777778E+10"), csch(BigDecimal("-1E-11"), mc64))

        // large values
        assertEquals(BigDecimal("2.499068543842026561848756998029982179529022758373454714411968778E-434294482"), csch(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("-2.499068543842026561848756998029982179529022758373454714411968778E-434294482"), csch(BigDecimal("-1E+9"), mc64))
    }
    
    @Test
    fun `csch performance`() {
        performanceTest { csch(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 1.914200ms
            println("Ave: $average") // 402.921us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure sech`() {
        // 0
        assertEquals(BigDecimal.ONE, sech(BigDecimal.ZERO, mc32))

        // 1
        assertEquals(BigDecimal("6.4805427366388539957497735322615E-1"), sech(BigDecimal.ONE, mc32))

        // -1 (even function)
        assertEquals(BigDecimal("6.4805427366388539957497735322615E-1"), sech(-BigDecimal.ONE, mc32))

        // small positive values
        assertEquals(BigDecimal("9.99999999999999999995E-1"), sech(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("9.9999999999999999999995E-1"), sech(BigDecimal("1E-11"), mc32))

        // small negative values
        assertEquals(BigDecimal("9.99999999999999999995E-1"), sech(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("9.9999999999999999999995E-1"), sech(BigDecimal("-1E-11"), mc32))

        // large values
        assertEquals(BigDecimal("2.49906854384202656184875699803E-434294482"), sech(BigDecimal("1E+9"), mc32))
        assertEquals(BigDecimal("2.49906854384202656184875699803E-434294482"), sech(BigDecimal("-1E+9"), mc32))
        
        // 2.499068543842026561848756998029982179529022758373454714411968778E-434294482
    }

    @Test
    fun `64 significant-figure sech`() {
        // 0
        assertEquals(BigDecimal.ONE, sech(BigDecimal.ZERO, mc64))

        // 1
        assertEquals(BigDecimal("6.480542736638853995749773532261503231084893120719420230378653373E-1"), sech(BigDecimal.ONE, mc64))

        // -1 (even function)
        assertEquals(BigDecimal("6.480542736638853995749773532261503231084893120719420230378653373E-1"), sech(-BigDecimal.ONE, mc64))

        // 6.480542736638853995749773532261503231084893120719420230378653373E-1

        // small positive values
        assertEquals(BigDecimal("9.999999999999999999950000000000000000000208333333333333333332486E-1"), sech(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("9.999999999999999999999500000000000000000000020833333333333333333E-1"), sech(BigDecimal("1E-11"), mc64))

        // small negative values
        assertEquals(BigDecimal("9.999999999999999999950000000000000000000208333333333333333332486E-1"), sech(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("9.999999999999999999999500000000000000000000020833333333333333333E-1"), sech(BigDecimal("-1E-11"), mc64))

        // large values
        assertEquals(BigDecimal("2.499068543842026561848756998029982179529022758373454714411968778E-434294482"), sech(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("2.499068543842026561848756998029982179529022758373454714411968778E-434294482"), sech(BigDecimal("-1E+9"), mc64))
    }

    @Test
    fun `sech performance`() {
        performanceTest { sech(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 1.914200ms
            println("Ave: $average") // 402.921us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure coth`() {
        // 0
        assertThrows<ArithmeticException> { coth(BigDecimal.ZERO, mc32) }

        // 1
        assertEquals(BigDecimal("1.3130352854993313036361612469308"), coth(BigDecimal.ONE, mc32))

        // -1 (odd function)
        assertEquals(BigDecimal("-1.3130352854993313036361612469308"), coth(-BigDecimal.ONE, mc32))

        // Small positive values
        assertEquals(BigDecimal("1.0000000000000000000033333333333E+10"), coth(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("1.0000000000000000000000333333333E+11"), coth(BigDecimal("1E-11"), mc32))

        // Small negative values
        assertEquals(BigDecimal("-1.0000000000000000000033333333333E+10"), coth(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("-1.0000000000000000000000333333333E+11"), coth(BigDecimal("-1E-11"), mc32))

        // large values
        assertEquals(BigDecimal("1"), coth(BigDecimal("1E+9"), mc32))
        assertEquals(BigDecimal("-1"), coth(BigDecimal("-1E+9"), mc32))
    }

    @Test
    fun `64 significant-figure coth`() {
        // 0
        assertThrows<ArithmeticException> { coth(BigDecimal.ZERO, mc64) }

        // 1
        assertEquals(BigDecimal("1.313035285499331303636161246930847832912013941240452655543152968"), coth(BigDecimal.ONE, mc64))

        // -1 (odd function)
        assertEquals(BigDecimal("-1.313035285499331303636161246930847832912013941240452655543152968"), coth(-BigDecimal.ONE, mc64))

        // Small positive values
        assertEquals(BigDecimal("1.000000000000000000003333333333333333333331111111111111111111113E+10"), coth(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("1.000000000000000000000033333333333333333333333111111111111111111E+11"), coth(BigDecimal("1E-11"), mc64))

        // Small negative values
        assertEquals(BigDecimal("-1.000000000000000000003333333333333333333331111111111111111111113E+10"), coth(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("-1.000000000000000000000033333333333333333333333111111111111111111E+11"), coth(BigDecimal("-1E-11"), mc64))

        // large values
        assertEquals(BigDecimal("1"), coth(BigDecimal("1E+9"), mc64))
        assertEquals(BigDecimal("-1"), coth(BigDecimal("-1E+9"), mc64))
    }
    
    @Test
    fun `coth performance`() {
        performanceTest { coth(BigDecimal("4E+9"), mc32) }.apply {
            println("Max: $maximum") // 10.951900ms
            println("Ave: $average") // 907.999us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }
}

class InverseHyperbolicFunctionsTest {
    @Test
    fun `32 significant-figure arsinh`() {
        // 0
        assertEquals(BigDecimal.ZERO, arsinh(BigDecimal.ZERO, mc32))

        // 1
        assertEquals(BigDecimal("8.8137358701954302523260932497979E-1"), arsinh(BigDecimal.ONE, mc32))

        // -1 (odd function)
        assertEquals(BigDecimal("-8.8137358701954302523260932497979E-1"), arsinh(-BigDecimal.ONE, mc32))

        // Small positive values
        assertEquals(BigDecimal("9.9999999999999999999833333333333E-11"), arsinh(BigDecimal("1E-10"), mc32))
        assertEquals(BigDecimal("9.9999999999999999999998333333333E-12"), arsinh(BigDecimal("1E-11"), mc32))
        // 9.999999999999999999999833333333333333333333340833333333333333333E-12

        // Small negative values
        assertEquals(BigDecimal("-9.9999999999999999999833333333333E-11"), arsinh(BigDecimal("-1E-10"), mc32))
        assertEquals(BigDecimal("-9.9999999999999999999998333333333E-12"), arsinh(BigDecimal("-1E-11"), mc32))

        // large values (arsinh(x) ≈ ln(2x))
        assertEquals(BigDecimal("2.3025920244658512834710856270056E+5"), arsinh(BigDecimal("1E+100000"), mc32))
        assertEquals(BigDecimal("-2.3025920244658512834710856270056E+5"), arsinh(BigDecimal("-1E+100000"), mc32))
        // 2.302592024465851283471085627005578789366782243630116578585869107E+5
    }

    @Test
    fun `64 significant-figure arsinh`() {
        // 0
        assertEquals(BigDecimal.ZERO, arsinh(BigDecimal.ZERO, mc64))

        // 1
        assertEquals(BigDecimal("8.813735870195430252326093249797923090281603282616354107532956087E-1"), arsinh(BigDecimal.ONE, mc64))

        // -1 (odd function)
        assertEquals(BigDecimal("-8.813735870195430252326093249797923090281603282616354107532956087E-1"), arsinh(-BigDecimal.ONE, mc64))

        // Small positive values
        assertEquals(BigDecimal("9.999999999999999999983333333333333333333408333333333333333332887E-11"), arsinh(BigDecimal("1E-10"), mc64))
        assertEquals(BigDecimal("9.999999999999999999999833333333333333333333340833333333333333333E-12"), arsinh(BigDecimal("1E-11"), mc64))

        // Small negative values
        assertEquals(BigDecimal("-9.999999999999999999983333333333333333333408333333333333333332887E-11"), arsinh(BigDecimal("-1E-10"), mc64))
        assertEquals(BigDecimal("-9.999999999999999999999833333333333333333333340833333333333333333E-12"), arsinh(BigDecimal("-1E-11"), mc64))

        // large values (arsinh(x) ≈ ln(2x))
        assertEquals(BigDecimal("2.302592024465851283471085627005578789366782243630116578585869108E+5"), arsinh(BigDecimal("1E+100000"), mc64))
        assertEquals(BigDecimal("-2.302592024465851283471085627005578789366782243630116578585869108E+5"), arsinh(BigDecimal("-1E+100000"), mc64))
    }

    @Test
    fun `arsinh performance`() {
        performanceTest { arsinh(BigDecimal("1E+100"), mc32) }.apply {
            println("Max: $maximum") // 4.081900ms
            println("Ave: $average") // 753.847us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure arcosh`() {
        // 1
        assertEquals(BigDecimal.ZERO, arcosh(BigDecimal.ONE, mc32))

        // a little over 1
        assertEquals(BigDecimal("4.4721359549995421250187223418061E-7"), arcosh(BigDecimal("1.0000000000001"), mc32))
        assertEquals(BigDecimal("1.4142135623730950488016887242097E-50"), arcosh(BigDecimal.ONE.add(BigDecimal("1E-100")), mc32))

        // large input
        assertEquals(BigDecimal("230.95165647996451371121637758989"), arcosh(BigDecimal("1E+100"), mc32))
        assertEquals(BigDecimal("230259.20244658512834710856270056"), arcosh(BigDecimal("1E+100000"), mc32))

    }

    @Test
    fun `64 significant-figure arcosh`() {
        // 1
        assertEquals(BigDecimal.ZERO, arcosh(BigDecimal.ONE, mc64))

        // a little over 1
        assertEquals(BigDecimal("4.472135954999542125018722341806137809549154044656747254706369007E-7"), arcosh(BigDecimal("1.0000000000001"), mc64))
        assertEquals(BigDecimal("1.414213562373095048801688724209698078569671875376948073176679738E-50"), arcosh(BigDecimal.ONE.add(BigDecimal("1E-100")), mc64))

        // large input
        assertEquals(BigDecimal("2.309516564799645137112163775898945973281856489972375528574534701E+2"), arcosh(BigDecimal("1E+100"), mc64))
        assertEquals(BigDecimal("2.302592024465851283471085627005578789366782243630116578585869108E+5"), arcosh(BigDecimal("1E+100000"), mc64))
    }

    @Test
    fun `arcosh throws when input is less than one`() {
        // input is less than 1
        val random = random(BigDecimal("-1E+32"), BigDecimal.ONE, inclusiveMax = false)
        assertThrows<ArithmeticException> { arcosh(random, mc32) }
    }

    @Test
    fun `arcosh performance`() {
        performanceTest { arcosh(BigDecimal("1E+100"), mc32) }.apply {
            println("Max: $maximum") // 3.685200ms
            println("Ave: $average") // 668.644us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure artanh`() {
        // 0
        assertEquals(BigDecimal.ZERO, artanh(BigDecimal.ZERO, mc32))

        // very close to |1|
        assertEquals(BigDecimal("37.187935078184703598996479335679"), artanh(BigDecimal("0.99999999999999999999999999999999"), mc32))
        assertEquals(BigDecimal("-37.187935078184703598996479335679"), artanh(BigDecimal("-0.99999999999999999999999999999999"), mc32))
    }

    @Test
    fun `64 significant-figure artanh`() {
        // 0
        assertEquals(BigDecimal.ZERO, artanh(BigDecimal.ZERO, mc64))

        // very close to |1|
        assertEquals(BigDecimal("74.02929656608943454328434261062874292727299770330086286012683284"), artanh(BigDecimal("0.9999999999999999999999999999999999999999999999999999999999999999"), mc64))
        assertEquals(BigDecimal("-74.02929656608943454328434261062874292727299770330086286012683284"), artanh(BigDecimal("-0.9999999999999999999999999999999999999999999999999999999999999999"), mc64))
    }

    @Test
    fun `artanh throws when input is lesser than or equal to -1 or greater than or equal to 1`() {
        // input is lesser than or equal to -1 or greater than or equal to 1
        fun randomOutOfArtanhDomain(): BigDecimal {
            val x = random(BigDecimal("-1E-32"), BigDecimal("1E+32"))
            return if (x > -BigDecimal.ONE && x < BigDecimal.ONE) randomOutOfArtanhDomain() else x
        }
        repeat(10) {
            val outOfArtanhRange = randomOutOfArtanhDomain()
            assertThrows<ArithmeticException> { artanh(outOfArtanhRange, mc32) }
        }
        assertThrows<ArithmeticException> { artanh(BigDecimal.ONE, mc32) }
        assertThrows<ArithmeticException> { artanh(-BigDecimal.ONE, mc32) }
    }

    @Test
    fun `artanh performance`() {
        performanceTest { artanh(BigDecimal("1E-19"), mc32) }.apply {
            println("Max: $maximum") // 520.8us
            println("Ave: $average") // 141.248us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure arcsch`() {
        // small input
        assertEquals(BigDecimal("2.3095165647996451371121637758989E+2"), arcsch(BigDecimal("1E-100"), mc32))
        assertEquals(BigDecimal("-2.3095165647996451371121637758989E+2"), arcsch(BigDecimal("-1E-100"), mc32))

        // large input
        assertEquals(BigDecimal("1E-100"), arcsch(BigDecimal("1E+100"), mc32))
        assertEquals(BigDecimal("-1E-100"), arcsch(BigDecimal("-1E+100"), mc32))
    }

    @Test
    fun `64 significant-figure arcsch`() {
        // small input
        assertEquals(BigDecimal("2.309516564799645137112163775898945973281856489972375528574534701E+2"), arcsch(BigDecimal("1E-100"), mc64))
        assertEquals(BigDecimal("-2.309516564799645137112163775898945973281856489972375528574534701E+2"), arcsch(BigDecimal("-1E-100"), mc64))

        // large input
        assertEquals(BigDecimal("1E-100"), arcsch(BigDecimal("1E+100"), mc64))
        assertEquals(BigDecimal("-1E-100"), arcsch(BigDecimal("-1E+100"), mc64))
    }

    @Test
    fun `arcsch throws when input is zero`() {
        assertThrows<ArithmeticException> { arcsch(BigDecimal.ZERO, mc32) }
    }

    @Test
    fun `arcsch performance`() {
        performanceTest { arcsch(BigDecimal("1E-100"), mc32) }.apply {
            println("Max: $maximum") // 4.526ms
            println("Ave: $average") // 743.327us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure arsech`() {
        // Close to zero
        assertEquals(BigDecimal("2.3032782401746056293274086868058E+3"), arsech(BigDecimal("1E-1000"), mc32))
        assertEquals(BigDecimal("2.3025920244658512834710856270056E+5"), arsech(BigDecimal("1E-100000"), mc32))

        // 1
        assertEquals(BigDecimal.ZERO, arsech(BigDecimal.ONE, mc32))
    }

    @Test
    fun `64 significant-figure arsech`() {
        // Close to zero
        assertEquals(BigDecimal("2.303278240174605629327408686805822384169176988763133231287448581E+3"), arsech(BigDecimal("1E-1000"), mc64))
        assertEquals(BigDecimal("2.302592024465851283471085627005578789366782243630116578585869108E+5"), arsech(BigDecimal("1E-100000"), mc64))

        // 1
        assertEquals(BigDecimal.ZERO, arsech(BigDecimal.ONE, mc64))
    }

    @Test
    fun `arsech throws when input is less than or equal to zero or greater than one`() {
        // Outside of domain
        fun randomOutOfArsechDomain(): BigDecimal {
            val x = random(BigDecimal("-1E-32"), BigDecimal("1E+32"))
            return if (x > BigDecimal.ZERO && x <= BigDecimal.ONE) randomOutOfArsechDomain() else x
        }
        repeat(10) {
            val randomOutOfDomain = randomOutOfArsechDomain()
            assertThrows<ArithmeticException> { arsech(randomOutOfDomain, mc32) }
        }

        // 0
        assertThrows<ArithmeticException> { arsech(BigDecimal.ZERO, mc32) }
    }

    @Test
    fun `arsech performance`() {
        performanceTest { arsech(BigDecimal("1E-100"), mc32) }.apply {
            println("Max: $maximum") // 3.778200ms
            println("Ave: $average") // 813.469us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

    @Test
    fun `32 significant-figure arcoth`() {
        // largest possible input lesser than -1
        val x1 = (-BigDecimal.ONE).subtract(BigDecimal("1E-31"), mc32)
        assertEquals(BigDecimal("-36.036642531687680756987483608337"), arcoth(x1, mc32))

        // smallest possible input greater than  1
        val x2 = BigDecimal.ONE.add(BigDecimal("1E-31"))
        assertEquals(BigDecimal("36.036642531687680756987483608337"), arcoth(x2, mc32))
    }

    @Test
    fun `64 significant-figure arcoth`() {
        // largest possible input lesser than -1
        val x1 = (-BigDecimal.ONE).subtract(BigDecimal("1E-63"), mc64)
        assertEquals(BigDecimal("-72.87800401959241170127534688328656082347244695898647637211016888"), arcoth(x1, mc64))

        // smallest possible input greater than  1
        val x2 = BigDecimal.ONE.add(BigDecimal("1E-63"))
        assertEquals(BigDecimal("72.87800401959241170127534688328656082347244695898647637211016888"), arcoth(x2, mc64))
    }
    
    @Test
    fun `arcoth throws when absolute value of input is less than or equal to one`() {
        // Outside of domain
        repeat(10) {
            val randomOutOfDomain = random(-BigDecimal.ONE, BigDecimal.ONE)
            assertThrows<ArithmeticException> { arcoth(randomOutOfDomain, mc32) }
        }

        // -1
        assertThrows<ArithmeticException> { arcoth(-BigDecimal.ONE, mc32) }

        // 1
        assertThrows<ArithmeticException> { arcoth(BigDecimal.ONE, mc32) }
    }

    @Test
    fun `arcoth performance`() {
        performanceTest { arcoth(BigDecimal("1E+100"), mc32) }.apply {
            println("Max: $maximum") // 497.499us
            println("Ave: $average") // 94.541us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }
}

class HyperbolicIdentitiesTest {
    @Test
    fun `sinh and arsinh are inverses of each other`() {
        val x = BigDecimal.ZERO
        assertEquals(x, sinh(arsinh(x, rounding = false)))
        assertEquals(x, arsinh(sinh(x, rounding = false)))
    }

    @Test
    fun `cosh and arcosh are inverses of each other`() {
        val x = BigDecimal.ONE
        assertEquals(x, cosh(arcosh(x, rounding = false)))
        assertEquals(x, arcosh(cosh(x, rounding = false)))
    }

    @Test
    fun `tanh and artanh are inverses of each other`() {
        val x = BigDecimal.ZERO
        assertEquals(x, tanh(artanh(x, rounding = false)))
        assertEquals(x, artanh(tanh(x, rounding = false)))
    }

    @Test
    fun `csch and arcsch are inverses of each other`() {
        val x = BigDecimal("0.93202002935234390539111484019364")
        assertEquals(x, csch(arcsch(x, rounding = false)))
        assertEquals(x, arcsch(csch(x, rounding = false)))
    }

    @Test
    fun `sech and arsech are inverses of each other`() {
        val x = BigDecimal("0.76500995455073212265532174248281")
        assertEquals(x, sech(arsech(x, mc32, rounding = false)))
        assertEquals(x, arsech(sech(x, mc32, rounding = false)))
    }

    @Test
    fun `coth and arcoth are inverses of each other`() {
        val x = BigDecimal("1.1996786402577338339163698486411")
        assertEquals(x, coth(arcoth(x, rounding = false)))
        assertEquals(x, arcoth(coth(x, rounding = false)))
    }

    @Test
    fun `hyperbolic functions are consistent with hyperbolic identities`() {
        val values = listOf(
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.TWO,
            BigDecimal.TEN,
            BigDecimal("0.5"),
        )

        // cosh²(x) - sinh²(x) = 1
        for (x in values) {
            val sinhX = sinh(x, rounding = false)
            val coshX = cosh(x, rounding = false)
            val lhs = coshX.multiply(coshX).subtract(sinhX.multiply(sinhX)).roundAndStrip()
            assertEquals(BigDecimal.ONE, lhs, "cosh²($x) - sinh²($x) should be 1")
        }

        // tanh²(x) + sech²(x) = 1
        for (x in values) {
            val sechX = sech(x, rounding = false)
            val tanhX = tanh(x, rounding = false)
            val lhs = tanhX.multiply(tanhX).add(sechX.multiply(sechX)).roundAndStrip()
            assertEquals(BigDecimal.ONE, lhs, "tanh²($x) + sech²($x) should be 1")
        }
    }
}

class SineTest {
    val pi = getConstant("pi", mc64.precision + 1)
    val twoPi: BigDecimal = pi.multiply(BigDecimal.TWO)
    val halfPi: BigDecimal = pi.divide(BigDecimal.TWO)
    val threeHalvesPi: BigDecimal = halfPi.multiply(BigDecimal("3"))
    val integerRange = BigDecimal.TWO..BigDecimal.TEN

    private fun someFullRotations(): BigDecimal = twoPi.multiply(integerRange.random())

    @Test
    fun `sin returns with correct sign`() {
        repeat(REPETITIONS) {
            val q1 = random(BigDecimal.ZERO, halfPi, inclusiveMax = false)
            val q2 = random(halfPi, pi, inclusiveMax = false)
            val q3 = random(pi, threeHalvesPi, inclusiveMax = false)
            val q4 = random(threeHalvesPi, twoPi, inclusiveMax = false)

            assertTrue(sin(q1, mc32).isNonNegative())
            assertTrue(sin(q2, mc32).isNonNegative())
            assertTrue(sin(q3, mc32).isNonPositive())
            assertTrue(sin(q4, mc32).isNonPositive())
        }
    }

    @Test
    fun `sin returns 1 or -1 at odd multiples of half pi`() {
        repeat(REPETITIONS) {
            assertEquals(BigDecimal.ONE, sin(halfPi.add(someFullRotations()), mc32))
            assertEquals(-BigDecimal.ONE, sin(-halfPi.subtract(someFullRotations()), mc32))
            assertEquals(-BigDecimal.ONE, sin(threeHalvesPi.add(someFullRotations()), mc32))
            assertEquals(BigDecimal.ONE, sin(-threeHalvesPi.subtract(someFullRotations()), mc32))
        }
    }

    @Test
    fun `sin returns 0 at 0 or multiples of pi`() {
        repeat(REPETITIONS) {
            assertEquals(BigDecimal.ZERO, sin(BigDecimal.ZERO, mc32))
            assertEquals(BigDecimal.ZERO, sin(someFullRotations(), mc32))
            assertEquals(BigDecimal.ZERO, sin(-someFullRotations(), mc32))
            assertEquals(BigDecimal.ZERO, sin(pi.add(someFullRotations()), mc32))
            assertEquals(BigDecimal.ZERO, sin(-pi.subtract(someFullRotations()), mc32))
        }
    }

    @Test
    fun `32 significant-figure sin`() {
        assertEquals(BigDecimal("0.8414709848078965066525023216303"), sin(BigDecimal.ONE, mc32))
        assertEquals(BigDecimal("-0.8414709848078965066525023216303"), sin(-BigDecimal.ONE, mc32))
    }

    @Test
    fun `64 significant-figure sin`() {
        assertEquals(BigDecimal("0.84147098480789650665250232163029899962256306079837106567275171"), sin(BigDecimal.ONE, mc64))
        assertEquals(BigDecimal("-0.84147098480789650665250232163029899962256306079837106567275171"), sin(-BigDecimal.ONE, mc64))
    }

    @Test
    fun `sin performance`() {
        val testNumber = BigDecimal("0.785398163397448309615660845819875")
        performanceTest { sin(testNumber, mc32) }.apply {
            println("Maximum Time: $maximum") // 1.229900ms
            println("Average Time: $average") // 236.96us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }
}

class CosineTest {
    val pi = getConstant("pi", mc64.precision + 3)
    val twoPi: BigDecimal = pi.multiply(BigDecimal.TWO)
    val halfPi: BigDecimal = pi.divide(BigDecimal.TWO)
    val threeHalvesPi: BigDecimal = halfPi.multiply(BigDecimal("3"))
    val integerRange = BigDecimal.TWO..BigDecimal.TEN

    private fun someFullRotations(): BigDecimal = twoPi.multiply(integerRange.random())

    @Test
    fun `cos returns with correct sign`() {
        repeat(REPETITIONS) {
            val q1 = random(BigDecimal.ZERO, halfPi, inclusiveMax = false)
            val q2 = random(halfPi, pi, inclusiveMax = false)
            val q3 = random(pi, threeHalvesPi, inclusiveMax = false)
            val q4 = random(threeHalvesPi, twoPi, inclusiveMax = false)

            assertTrue(cos(q1, mc32).isNonNegative())
            assertTrue(cos(q2, mc32).isNonPositive())
            assertTrue(cos(q3, mc32).isNonPositive())
            assertTrue(cos(q4, mc32).isNonNegative())
        }
    }

    @Test
    fun `cos returns 0 at odd multiples of half pi`() {
        repeat(REPETITIONS) {
            assertEquals(BigDecimal.ZERO, cos(-(halfPi.add(someFullRotations())), mc32))
            assertEquals(BigDecimal.ZERO, cos(halfPi.add(someFullRotations()), mc32))
            assertEquals(BigDecimal.ZERO, cos(threeHalvesPi.add(someFullRotations()), mc32))
            assertEquals(BigDecimal.ZERO, cos(-(threeHalvesPi.add(someFullRotations())), mc32))
        }
    }

    @Test
    fun `cos returns 1 or -1 at 0 or multiples of pi`() {
        assertEquals(BigDecimal.ONE, cos(BigDecimal.ZERO, mc32))
        assertEquals(BigDecimal.ONE, cos(someFullRotations(), mc32))
        assertEquals(BigDecimal.ONE, cos(-someFullRotations(), mc32))
        assertEquals(-BigDecimal.ONE, cos(pi.add(someFullRotations()), mc32))
        assertEquals(-BigDecimal.ONE, cos(-pi.subtract(someFullRotations()), mc32))
    }

    @Test
    fun `32 significant-figure cos`() {
        assertEquals(BigDecimal("0.54030230586813971740093660744298"), cos(BigDecimal.ONE, mc32))
        assertEquals(BigDecimal("0.54030230586813971740093660744298"), cos(-BigDecimal.ONE, mc32))
    }

    @Test
    fun `64 significant-figure cos`() {
        assertEquals(BigDecimal("0.5403023058681397174009366074429766037323104206179222276700972554"), cos(BigDecimal.ONE, mc64))
        assertEquals(BigDecimal("0.5403023058681397174009366074429766037323104206179222276700972554"), cos(-BigDecimal.ONE, mc64))
    }

    @Test
    fun `cos performance`() {
        val testNumber = BigDecimal("0.785398163397448309615660845819875")
        performanceTest { cos(testNumber, mc32) }.apply {
            println("Maximum Time: $maximum") // 1.350300ms
            println("Average Time: $average") // 361.426us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }

}

class TangentTest {
    val pi = getConstant("pi", mc64.precision + 3)
    val twoPi: BigDecimal = pi.multiply(BigDecimal.TWO)
    val halfPi: BigDecimal = pi.divide(BigDecimal.TWO)
    val threeHalvesPi: BigDecimal = halfPi.multiply(BigDecimal("3"))
    val integerRange = BigDecimal.TWO..BigDecimal.TEN

    private fun someFullRotations(): BigDecimal = twoPi.multiply(integerRange.random())

    @Test
    fun `tan returns with correct sign`() {
        repeat(REPETITIONS) {
            val q1 = random(BigDecimal.ZERO, halfPi, inclusiveMax = false)
            val q2 = random(halfPi, pi, inclusiveMin = false, inclusiveMax = false)
            val q3 = random(pi, threeHalvesPi, inclusiveMax = false)
            val q4 = random(threeHalvesPi, twoPi, inclusiveMin = false, inclusiveMax = false)

            assertTrue(tan(q1).isNonNegative())
            assertTrue(tan(q2).isNegative())
            assertTrue(tan(q3).isNonNegative())
            assertTrue(tan(q4).isNegative())
        }
    }

    @Test
    fun `tan returns 1 or -1 at odd multiples of 45 degrees`() {
        val radianToExpectedTangent = listOf(
            pi.multiply(BigDecimal("0.25")) to BigDecimal.ONE,
            pi.multiply(BigDecimal("0.75")) to -BigDecimal.ONE,
            pi.multiply(BigDecimal("1.25")) to BigDecimal.ONE,
            pi.multiply(BigDecimal("1.75")) to -BigDecimal.ONE
        )

        for ((radian, expectedTangent) in radianToExpectedTangent) {
            repeat(REPETITIONS) {
                assertEquals(expectedTangent, tan(radian.add(someFullRotations()), mc32)) { "$radian, $expectedTangent" }
            }
        }
    }

    @Test
    fun `tan returns 0 at 0 and multiples of pi`() {
        repeat(REPETITIONS) {
            assertEquals(BigDecimal.ZERO, tan(BigDecimal.ZERO.add(someFullRotations()), mc32))
            assertEquals(BigDecimal.ZERO, tan(pi.add(someFullRotations()), mc32))
        }
    }

    @Test
    fun `tan throws at odd multiples of half pi`() {
        val halfPiException = assertThrows<ArithmeticException> {
            tan(halfPi.add(someFullRotations()), mc32)
        }
        assertTrue(halfPiException.message?.contains("Undefined")!!)

        val threeHalvesPiException = assertThrows<ArithmeticException> {
            tan(threeHalvesPi.add(someFullRotations()), mc32)
        }
        assertTrue(threeHalvesPiException.message?.contains("Undefined")!!)
    }

    @Test
    fun `32 significant-figure tan`() {
        assertEquals(BigDecimal("1.5574077246549022305069748074584"), tan(BigDecimal.ONE.add(someFullRotations()), mc32))
    }

    @Test
    fun `64 significant-figure tan`() {
        assertEquals(
            BigDecimal("1.557407724654902230506974807458360173087250772381520038383946606"),
            tan(BigDecimal.ONE.add(someFullRotations()), mc64)
        )
    }

    @Test
    fun `tan performance`() {
        val testNumber = BigDecimal("0.785398163397448309615660845819875")
        performanceTest { tan(testNumber, mc32) }.apply {
            println("Maximum Time: $maximum") // 1.418800ms
            println("Average Time: $average") // 257.218us
            assertTrue(maximum < 50.milliseconds)
            assertTrue(average < 1.milliseconds)
        }
    }
}

class TrigonometricFunctionsTest {
    val pi = getConstant("pi", mc64.precision + 2)
    val twoPi: BigDecimal = pi.multiply(BigDecimal.TWO)
    val halfPi: BigDecimal = pi.divide(BigDecimal.TWO)
    val threeHalvesPi: BigDecimal = halfPi.multiply(BigDecimal("3"))
    val integerRange = BigDecimal.TWO..BigDecimal.TEN

    private fun someFullRotations(): BigDecimal = twoPi.multiply(integerRange.random())

    @Test
    fun `32 significant-figure csc`() {
        val radianToResult = listOf(
            halfPi to BigDecimal.ONE,
            threeHalvesPi to -BigDecimal.ONE
        )

        for ((radian, result) in radianToResult) {
            repeat(REPETITIONS) {
                assertEquals(result, csc(radian.add(someFullRotations()), mc32))
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                csc(BigDecimal.ZERO.add(someFullRotations()), mc32)
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions =
                    twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                csc(pi.add(someRandomRevolutions))
            }
        }

        // high-precision calculation
        assertEquals(BigDecimal("1.1883951057781212162615994523746"), csc(BigDecimal.ONE))
    }

    @Test
    fun `csc performance`() {
        val times = performanceTest { csc(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 726.2us
        println("Average Time: ${times.average}") // 72.088us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `32 significant-figure sec`() {
        val radianToResult = listOf(
            BigDecimal.ZERO to BigDecimal.ONE,
            pi to -BigDecimal.ONE
        )

        for ((radian, result) in radianToResult) {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions = twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                assertEquals(result, sec(radian.add(someRandomRevolutions)))
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions =
                    twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                sec(halfPi.add(someRandomRevolutions))
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions =
                    twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                sec(threeHalvesPi.add(someRandomRevolutions))
            }
        }

        // high-precision calculation
        assertEquals(BigDecimal("1.8508157176809256179117532413987"), sec(BigDecimal.ONE))
    }

    @Test
    fun `sec performance`() {
        val times = performanceTest { sec(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 847.5us
        println("Average Time: ${times.average}") // 107.151us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `32 significant-figure cot`() {
        val radianToResult = listOf(
            halfPi to BigDecimal.ZERO,
            threeHalvesPi to BigDecimal.ZERO,
        )

        for ((radian, result) in radianToResult) {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions = twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                assertEquals(result, cot(radian.add(someRandomRevolutions)))
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions =
                    twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                cot(BigDecimal.ZERO.add(someRandomRevolutions))
            }
        }

        assertThrows<ArithmeticException> {
            repeat(REPETITIONS) {
                val someInteger = integerRange.random()
                val someRandomRevolutions =
                    twoPi.multiply(someInteger) // using math mc32 here breaks the test, due to precision-loss
                cot(pi.add(someRandomRevolutions))
            }
        }

        // high-precision calculation
        assertEquals(BigDecimal("0.64209261593433070300641998659427"), cot(BigDecimal.ONE))
    }

    @Test
    fun `cot performance`() {
        val times = performanceTest { cot(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 4.323200ms
        println("Average Time: ${times.average}") // 179.923us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }
}

class InverseTrigonometricFunctionsTest {
    val pi = getConstant("pi", mc32.precision + 1)
    val halfPi: BigDecimal = pi.divide(BigDecimal.TWO, mathCtx)
    val realRange = BigDecimal(-Double.MAX_VALUE)..BigDecimal(Double.MAX_VALUE)

    @Test
    fun `32 significant-figure asin`() {
        fun outOfDomain(): BigDecimal {
            val x = realRange.random()
            return if (abs(x) <= BigDecimal.ONE) outOfDomain() else x
        }

        assertEquals(-halfPi, asin(-BigDecimal.ONE))
        assertEquals(BigDecimal.ZERO, asin(BigDecimal.ZERO))
        assertEquals(halfPi, asin(BigDecimal.ONE))

        repeat(REPETITIONS) {
            assertDoesNotThrow { asin((-BigDecimal.ONE..BigDecimal.ONE).random()) }
        }

        repeat(REPETITIONS) {
            assertThrows<ArithmeticException> { asin(outOfDomain()) }
        }

        // high-precision calculation
        assertEquals(BigDecimal("0.52359877559829887307710723054658"), asin(BigDecimal("0.5")))
    }

    @Test
    fun `asin performance`() {
        val number = BigDecimal("0.5")
        val times = performanceTest(100) { asin(number) }
        println("Maximum Time: ${times.maximum}") // 3.614700ms
        println("Average Time: ${times.average}") // 254.584us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `acos returns values consistent with reference high-precision calculator`() {
        fun outOfDomain(): BigDecimal {
            val x = realRange.random()
            return if (x.abs() <= BigDecimal.ONE) outOfDomain() else x
        }

        assertEquals(pi, acos(-BigDecimal.ONE))
        assertEquals(halfPi, acos(BigDecimal.ZERO))
        assertEquals(BigDecimal.ZERO, acos(BigDecimal.ONE))

        repeat(REPETITIONS) {
            Assertions.assertDoesNotThrow { acos((-BigDecimal.ONE..BigDecimal.ONE).random()) }
        }

        repeat(REPETITIONS) {
            assertThrows<ArithmeticException> { acos(outOfDomain()) }
        }

        // high-precision calculation
        assertEquals(BigDecimal("1.0471975511965977461542144610932"), acos(BigDecimal("0.5")))
    }

    @Test
    fun `acos performance`() {
        val times = performanceTest { acos(BigDecimal("1E-19")) }
        println("Maximum Time: ${times.maximum}") // 5.198801ms // 707.1us
        println("Average Time: ${times.average}") // 201.409us // 57.808us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `atan returns values consistent with reference high-precision calculator`() {
        assertEquals(BigDecimal.ZERO, atan(BigDecimal.ZERO))

        repeat(REPETITIONS) {
            val x = realRange.random()
            Assertions.assertDoesNotThrow { atan(x) }
        }

        // high-precision calculation
        assertEquals(BigDecimal("0.46364760900080611621425623146121"), atan(BigDecimal("0.5")))
    }

    @Test
    fun `atan performance`() {
        val times = performanceTest { atan(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 852us
        println("Average Time: ${times.average}") // 68.877us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `atan2 returns values consistent with reference high-precision calculator`() {
        repeat(REPETITIONS) {
            val x = realRange.random()
            val y = realRange.random()
            Assertions.assertDoesNotThrow { atan2(y, x) }
        }

        repeat(REPETITIONS) {
            val x = (BigDecimal.ZERO..BigDecimal(Double.MAX_VALUE.toString())).random()
            assertEquals(pi / BigDecimal("4"), atan2(x, x))
            assertEquals(-pi / BigDecimal("4"), atan2(-x, -x))
        }

        // high-precision calculation
        assertEquals(
            BigDecimal("0.64350110879328438680280922871732"),
            atan2(BigDecimal("3"), BigDecimal("4"))
        )
    }

    @Test
    fun `atan2 performance`() {
        val times = performanceTest { atan2(BigDecimal("1E-19"), BigDecimal("1E-19")) }
        println("Maximum Time: ${times.maximum}") // 760.1us
        println("Average Time: ${times.average}") // 33.669us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `acsc returns values consistent with reference high-precision calculator`() {
        fun inDomain(): BigDecimal {
            val x = realRange.random()
            return if (x.abs() <= BigDecimal.ONE) inDomain() else x
        }

        assertEquals(halfPi, acsc(BigDecimal.ONE))
        assertEquals(-halfPi, acsc(-BigDecimal.ONE))

        repeat(REPETITIONS) {
            Assertions.assertDoesNotThrow { acsc(inDomain()) }
        }

        repeat(REPETITIONS) {
            assertThrows<ArithmeticException> {
                acsc(random(-BigDecimal.ONE, BigDecimal.ONE, inclusiveMin = false, inclusiveMax = false))
            }
        }

        // high-precision calculation
        assertEquals(asin(BigDecimal("0.5")), acsc(BigDecimal.TWO))
    }

    @Test
    fun `acsc performance`() {
        val times = performanceTest { acsc(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 4.450200ms
        println("Average Time: ${times.average}") // 211.625us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `asec returns values consistent with reference high-precision calculator`() {
        repeat(REPETITIONS) {
            assertThrows<ArithmeticException> {
                asec(random(-BigDecimal.ONE, BigDecimal.ONE, inclusiveMin = false, inclusiveMax = false))
            }
        }

        repeat(REPETITIONS) {
            Assertions.assertDoesNotThrow {
                asec(random(BigDecimal.ONE, BigDecimal(Double.MAX_VALUE)))
            }
        }

        assertEquals(halfPi, asec(BigDecimal(Double.MAX_VALUE)))
        assertEquals(halfPi, asec(BigDecimal(-Double.MAX_VALUE)))
    }

    @Test
    fun `asec performance`() {
        val times = performanceTest { asec(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 5.235800ms
        println("Average Time: ${times.average}") // 313.045us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `acot returns values consistent with reference high-precision calculator`() {
        repeat(REPETITIONS) {
            Assertions.assertDoesNotThrow { acot(realRange.random()) }
        }

        // 0
        assertEquals(halfPi, acot(BigDecimal.ZERO))

        // high-precision calculation
        assertEquals(BigDecimal("0.46364760900080611621425623146121"), acot(BigDecimal.TWO))
    }

    @Test
    fun `acot performance`() {
        val times = performanceTest { acot(BigDecimal("1E+19")) }
        println("Maximum Time: ${times.maximum}") // 3.932600ms
        println("Average Time: ${times.average}") // 169.16us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }

    @Test
    fun `acot2 returns values consistent with reference high-precision calculator`() {
        repeat(REPETITIONS) {
            Assertions.assertDoesNotThrow { acot2(realRange.random(), realRange.random()) }
        }

        // (0, 0)
        assertEquals(BigDecimal.ZERO, acot2(BigDecimal.ZERO, BigDecimal.ZERO))

        // high-precision calculation
        assertEquals(
            BigDecimal("0.46364760900080611621425623146121"),
            acot2(BigDecimal.TWO, BigDecimal.ONE)
        )
    }

    @Test
    fun `acot2 performance`() {
        val times = performanceTest { acot2(BigDecimal("1E-19"), BigDecimal("1E-19")) }
        println("Maximum Time: ${times.maximum}") // 618.4us
        println("Average Time: ${times.average}") // 39.686us
        assertTrue(times.maximum < 50.milliseconds)
        assertTrue(times.average < 1.milliseconds)
    }
}

class TrigonometricIdentitiesTest {
    val pi = getConstant("pi", mc32.precision + 1)
    val twoPi: BigDecimal = pi.multiply(BigDecimal.TWO)

    @Test
    fun `sin squared plus cos squared equals one`() {
        repeat(REPETITIONS) {
            val tolerance = BigDecimal("1E-31")
            val randomRadian = random(BigDecimal.ZERO, twoPi, inclusiveMax = false)
            val sinSquared = sin(randomRadian, rounding = false).squared
            val cosSquared = cos(randomRadian, rounding = false).squared
            val result = sinSquared.add(cosSquared).roundAndStrip()
            assertTrue(relativeEquals(BigDecimal.ONE, result, tolerance))
        }
    }

    // More to come, as soon as sine is fixed.
}

class SpecialFunctionsTest {
    @Test
    fun `relativeEquals evaluates correctly at tolerance boundary`() {
        val onePercentTolerance = BigDecimal("0.01")

        // Exact match
        assertTrue(relativeEquals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
        assertTrue(relativeEquals(BigDecimal.ZERO, BigDecimal.ZERO, onePercentTolerance))
        assertTrue(relativeEquals(BigDecimal("42.0"), BigDecimal("42.0"), onePercentTolerance))

        // Within relative tolerance
        assertTrue(
            relativeEquals(
                BigDecimal("100"),
                BigDecimal("100.5"),
                onePercentTolerance
            )
        ) // 0.5% diff, 1% allowed
        assertTrue(relativeEquals(BigDecimal("0.001"), BigDecimal.ZERO, onePercentTolerance))
        assertTrue(relativeEquals(BigDecimal("-0.001"), BigDecimal.ZERO, onePercentTolerance))

        // Outside relative tolerance
        assertFalse(relativeEquals(BigDecimal("100"), BigDecimal("102"), onePercentTolerance)) // 2% diff
        assertFalse(relativeEquals(BigDecimal("1E-10"), BigDecimal("3E-10"), BigDecimal("1E-11")))

        // Negative tolerance check
        assertThrows<IllegalArgumentException> {
            relativeEquals(BigDecimal.ONE, BigDecimal.TWO, BigDecimal("-1E-5"))
        }
    }

    @Test
    fun `agm performs arithmetic-geometric mean`() {
        // WolframAlpha: AGM(1, 0.5) ≈ 0.72839551552345343459321619163254
        assertTrue(
            relativeEquals(
                BigDecimal("0.72839551552345343459321619163254"),
                agm(BigDecimal.ONE, BigDecimal("0.5")),
                BigDecimal("1E-32")
            )
        )
        // Equal inputs → should return the input
        assertEquals(
            BigDecimal("123456.789"),
            agm(BigDecimal("123456.789"), BigDecimal("123456.789"))
        )

        // Extreme magnitude gap → result should be > 0 and finite
        assertTrue(
            agm(BigDecimal("1E+100"), BigDecimal("1E-100")) > BigDecimal.ZERO
        )

        // Tiny delta between inputs → result should be close to the smaller input
        assertTrue(
            relativeEquals(
                BigDecimal.ONE,
                agm(BigDecimal.ONE, BigDecimal("1.00000000000000000000000000000001")),
                BigDecimal("1E-32")
            )
        )

        // Symmetry check → agm(x, y) == agm(y, x)
        assertTrue(
            relativeEquals(
                agm(BigDecimal("0.25"), BigDecimal("4.0")),
                agm(BigDecimal("4.0"), BigDecimal("0.25")),
                BigDecimal("1E-32")
            )
        )

        // Subnormal inputs → should still converge and be > 0
        assertTrue(
            agm(BigDecimal("1E-300"), BigDecimal("1E-310")) > BigDecimal.ZERO
        )
    }

    @Test
    fun `compareBigDecimals handles tolerance and direction correctly`() {
        val ctx = MathContext(10) // precision = 10 → tolerance = 1E-10

        // Exact equality
        assertEquals(0, compareBigDecimals(BigDecimal("1.2345"), BigDecimal("1.2345"), ctx))

        // Within absolute tolerance
        val epsilon = BigDecimal.ONE.movePointLeft(ctx.precision)
        val x = BigDecimal("1.0000000000") // 10 digits
        val y = x.add(epsilon.divide(BigDecimal.TWO)) // half of tolerance
        assertEquals(0, compareBigDecimals(x, y, ctx))

        // Just outside absolute tolerance
        val z = x.add(epsilon.multiply(BigDecimal("1.1"))) // slightly over tolerance
        assertEquals(1, compareBigDecimals(z, x, ctx))
        assertEquals(-1, compareBigDecimals(x, z, ctx))

        // Relative tolerance: small difference on large values
        val a = BigDecimal("1000000000.0000000001")
        val b = BigDecimal("1000000000.0000000000")
        assertEquals(0, compareBigDecimals(a, b, ctx)) // small diff relative to large magnitude

        // Relative tolerance: same diff on small numbers = too large
        val smallA = BigDecimal("0.0000000001")
        val smallB = BigDecimal("0.0000000002")
        assertEquals(-1, compareBigDecimals(smallA, smallB, ctx))
        assertEquals(1, compareBigDecimals(smallB, smallA, ctx))

        // ZERO comparisons
        assertEquals(0, compareBigDecimals(BigDecimal.ZERO, BigDecimal.ZERO, ctx))
        assertEquals(0, compareBigDecimals(BigDecimal.ZERO, epsilon.divide(BigDecimal.TWO), ctx))
        assertEquals(-1, compareBigDecimals(BigDecimal.ZERO, epsilon.multiply(BigDecimal("2")), ctx))
        assertEquals(1, compareBigDecimals(epsilon.multiply(BigDecimal("2")), BigDecimal.ZERO, ctx))
    }

    @Test
    fun `getConstant returns expected values and rounding`() {
        val ctx = MathContext(10) // 10-digit precision

        // Case-insensitive lookup
        assertEquals(PI.round(ctx), getConstant("pi", ctx.precision))
        assertEquals(PI.round(ctx), getConstant("PI", ctx.precision))
        assertEquals(E.round(ctx), getConstant("e", ctx.precision))
        assertEquals(LOG_2.round(ctx), getConstant("Ln2", ctx.precision))

        // Different precision rounding
        val ctxHigh = MathContext(32)
        assertEquals(PI.round(ctxHigh), getConstant("pi", ctxHigh.precision))
        assertNotEquals(getConstant("pi", ctx.precision), getConstant("pi", ctxHigh.precision))

        // Throws when constant is not in Constants.kt
        assertThrows<IllegalStateException> {
            getConstant("tau", ctx.precision)
        }
    }

    @Test
    fun `mathContextFromScale behaves as expected`() {
        val testCases = listOf(
            // ZERO values
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, 0),
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, 10),
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, -10),

            // Opposing sign scales
            Triple(BigDecimal("1E+100"), BigDecimal("1E-100"), 5),
            Triple(BigDecimal("1E-100"), BigDecimal("1E+100"), 5),

            // Subnormal vs. large
            Triple(BigDecimal("1E-300"), BigDecimal("9.999999999999999E+300"), 10),

            // Negative and positive extremes
            Triple(BigDecimal("-1E+308"), BigDecimal("1E-308"), 32),
            Triple(BigDecimal("-1E-308"), BigDecimal("1E+308"), 32),

            // Very high precision with fractional tail
            Triple(
                BigDecimal("123456789012345678901234567890.123456789"),
                BigDecimal("0.00000000000000000000000000000000000000000000001"), 15),

            // Differing signs and magnitude
            Triple(BigDecimal("1E+50"), BigDecimal("-1E-50"), 20),

            // Very small difference, but large integer part
            Triple(
                BigDecimal("1000000000000000000000000000.0000000001"),
                BigDecimal("999999999999999999999999999.9999999999"), 10),

            // Mixed integer-digit cases
            Triple(BigDecimal("999.999"), BigDecimal("0.000000001"), 6),
            Triple(BigDecimal("0.000000001"), BigDecimal("999.999"), 6),
        )

        for ((index, case) in testCases.withIndex()) {
            val (x, y, scale) = case
            val mc = mathContextFromScaleAndValues(x, y, scale)

            // Compute expected
            val maxAbs = max(x.abs(), y.abs())
            val expectedIntegerDigits = maxAbs.precision() - maxAbs.scale()
            val expectedPrecision = (expectedIntegerDigits + scale).coerceAtLeast(1)

            assertEquals(
                expectedPrecision, mc.precision,
                "Case $index: Precision mismatch\nx=$x\ny=$y\nscale=$scale\nexpected=$expectedPrecision, actual=${mc.precision}"
            )

            assertEquals(
                RoundingMode.HALF_EVEN, mc.roundingMode,
                "Case $index: Rounding mismatch\nx=$x\ny=$y\nscale=$scale"
            )
        }
    }

    /**
     * Tests the statistical properties of the `randomBigDecimal` function generating `BigDecimal` values within a specified range.
     *
     * This test generates a large sample of random `BigDecimal` values in the interval [min, max],
     * then evaluates several statistical properties to verify the uniformity and correctness of the distribution:
     *
     * - **Mean**: The sample mean must be within 1% relative error of the expected mean `(min + max) / 2`.
     * - **Median**: The sample median must be within 1% relative error of the expected median `(min + max) / 2`.
     * - **Mode frequency**: The highest frequency bucket should not exceed 130% of the expected uniform frequency.
     * - **Chi-squared test**: The distribution of samples into buckets must pass the chi-squared goodness-of-fit test
     *   with the critical value for 19 degrees of freedom at 0.05 significance level.
     *   This test may fail approximately 5% of the time due to statistical randomness; re-run if it does.
     *
     * Buckets are created by dividing the range [min, max] into equal-width intervals, and samples are assigned accordingly.
     * The sample size (e.g., 1,000,000) ensures statistical reliability.
     *
     * @throws AssertionError if any of the statistical tests fail.
     */
    @Test
    fun `random statistics`() {
        val min = BigDecimal("1E-32")
        val max = BigDecimal("1E+32")
        val sampleSize = 1_000
        val bucketsCount = 20
        val random = SecureKotlinRandom.Instance

        fun generateSamples(
            min: BigDecimal,
            max: BigDecimal,
            sampleSize: Int,
            random: Random
        ): List<BigDecimal> {
            return List(sampleSize) {
                random(min, max, inclusiveMin = true, inclusiveMax = true, random = random)
            }
        }

        fun bucketize(
            samples: List<BigDecimal>,
            min: BigDecimal,
            max: BigDecimal,
            numBuckets: Int
        ): IntArray {
            val buckets = IntArray(numBuckets)
            val range = max.subtract(min)
            samples.forEach { value ->
                val relativePosition = (value.subtract(min)).divide(range, mathCtx)
                var index = (relativePosition.multiply(BigDecimal(numBuckets))).toInt()
                if (index == numBuckets) index = numBuckets - 1
                buckets[index]++
            }
            return buckets
        }

        fun mean(samples: List<BigDecimal>): BigDecimal {
            val sum = samples.fold(BigDecimal.ZERO) { acc, v -> acc.add(v) }
            return sum.divide(BigDecimal(samples.size))
        }

        fun median(samples: List<BigDecimal>): BigDecimal {
            val sorted = samples.sorted()
            val mid = samples.size / 2
            return if (samples.size % 2 == 1) {
                sorted[mid]
            } else {
                (sorted[mid - 1].add(sorted[mid])).divide(BigDecimal.TWO, mathCtx)
            }
        }

        // Returns the index of the bucket with the highest count.
        // Used only for a heuristic check (not statistically rigorous).
        fun mode(buckets: IntArray): Int {
            return buckets.indices.maxByOrNull { buckets[it] } ?: -1
        }

        fun chiSquaredTest(
            observed: IntArray,
            expectedCount: Double
        ): Double {
            return observed.fold(0.0) { acc, o ->
                val diff = o - expectedCount
                acc + (diff * diff) / expectedCount
            }
        }

        val samples = generateSamples(min, max, sampleSize, random)

        val expectedMean = (min.add(max)).divide(BigDecimal.TWO, mathCtx)
        val actualMean = mean(samples)

        val diff = actualMean - expectedMean
        val relativeError = diff.abs() / BigDecimal(expectedMean.toString())
        assertTrue(relativeError <= BigDecimal("0.05"), "Mean relative error too large: $relativeError")

        val actualMedian = median(samples)
        val expectedMedian = (min + max).divide(BigDecimal.TWO, mc32)
        val relativeErrorMedian = (actualMedian - expectedMedian).abs().divide(BigDecimal(expectedMedian.toString()), mc32)
        assertTrue(relativeErrorMedian <= BigDecimal("0.05"), "Median relative error too large: $relativeErrorMedian")

        val buckets = bucketize(samples, min, max, bucketsCount)
        val expectedPerBucket = sampleSize.toDouble() / bucketsCount

        // Heuristic sanity check: mode shouldn't dominate in a uniform distribution.
        // 1.3x is an arbitrary upper bound; not statistically rigorous.
        val modeIndex = mode(buckets)
        val modeFrequency = buckets[modeIndex]
        assertTrue(
            modeFrequency < expectedPerBucket * 1.3,
            "Mode frequency too high for uniform distribution: $modeFrequency"
        )

        val chiSq = chiSquaredTest(buckets, expectedPerBucket)
        val chiSqThreshold = 31.4 // Chi-squared 0.05 cutoff for df=19 buckets
        assertTrue(
            chiSq < chiSqThreshold,
            "Chi-squared test failed: $chiSq > $chiSqThreshold (not uniform)"
        )
    }

    @Test
    fun `factorialBD behaves as expected`() {
        for (i in 0..1000) {
            Assertions.assertDoesNotThrow { factorialBD(i) }
        }
        assertEquals(BigDecimal.ONE, factorialBD(0))
        assertEquals(BigDecimal.ONE, factorialBD(1))
        assertEquals(BigDecimal.TWO, factorialBD(2))
        assertEquals(BigDecimal("6"), factorialBD(3))
        assertEquals(BigDecimal("24"), factorialBD(4))
        assertEquals(BigDecimal("120"), factorialBD(5))
    }

    @Test
    fun `roundAndStrip rounds to context and strips trailing zeros`() {
        val ctx = MathContext(5)

        // Rounding and stripping
        assertEquals(BigDecimal("3.1416"), BigDecimal("3.1415926535").roundAndStrip(ctx))
        assertEquals(BigDecimal("2.5"), BigDecimal("2.50000").roundAndStrip(ctx))
        assertEquals(BigDecimal("42"), BigDecimal("42.000000").roundAndStrip(ctx))
        assertEquals(BigDecimal("1.2345"), BigDecimal("1.2345").roundAndStrip(ctx)) // no change
    }

    @Test
    fun `roundToNearestSimpleValue behaves as expected`() {
        val ctx = MathContext(10)
        val someNumber = BigDecimal("1E-31")
        assertEquals(someNumber, someNumber.roundToNearestSimpleValue(ctx))

        // Already simple → no change
        assertEquals(BigDecimal("2.5"), BigDecimal("2.5").roundToNearestSimpleValue(ctx))

        // Trailing nines → simplify to 1.2
        assertEquals(BigDecimal("1.2"), BigDecimal("1.20000000009").roundToNearestSimpleValue(ctx))
        assertEquals(BigDecimal("3.14"), BigDecimal("3.1400000000001").roundToNearestSimpleValue(ctx))

        // Trailing zeros → simplify to 42
        assertEquals(BigDecimal("42"), BigDecimal("42.000000000").roundToNearestSimpleValue(ctx))

        // Near-round numbers → simplify to expected round value
        assertEquals(BigDecimal("0.1"), BigDecimal("0.099999999999").roundToNearestSimpleValue(ctx))
        assertEquals(BigDecimal("1"), BigDecimal("0.999999999999").roundToNearestSimpleValue(ctx))

        // No simplification possible → return original rounded + stripped
        assertEquals(
            BigDecimal("1.234567891").stripTrailingZeros(),
            BigDecimal("1.23456789123456789").roundToNearestSimpleValue(ctx)
        )
    }
}

/**
 * Tests a single BigDecimal parameter function that returns another BigDecimal
 * Returns the maximum and average times over a given number of REPETITIONS
 */
fun performanceTest(iterations: Int = 100, function: () -> Any): PerformanceTime {
    val times = mutableListOf<Duration>()

    repeat(10) { function() } // warmup

    repeat(iterations) {
        val time = measureTime { function() }
        times.add(time)
    }
    val maxTime = times.maxOrNull() ?: Duration.ZERO
    val averageTime = times.fold(Duration.ZERO) { acc, d -> acc + d } / iterations
    return PerformanceTime(maxTime, averageTime)
}

data class PerformanceTime(
    val maximum: Duration,
    val average: Duration
)

// Reference: https://www.mathsisfun.com/calculator-precision.html
// Reference: https://www.wolframalpha.com