package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpLBigDecimalsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32ExpL mDecimal = new Float32ExpL();

    @Test
    public void whenConstructedFromSmallPositiveIntegersBigIntegerThenInternalsCorrect() {
        setAndAssertBits(BigInteger.ONE, 0x40000000, -30);
        setAndAssertBits(BigInteger.valueOf(2), 0x40000000, -29);
        setAndAssertBits(BigInteger.valueOf(3), 0x60000000, -29);
        setAndAssertBits(BigInteger.valueOf(4), 0x40000000, -28);
        setAndAssertBits(BigInteger.valueOf(5), 0x50000000, -28);
    }

    @Test
    public void whenConstructedFromSmallNegativeIntegersBigIntegerThenInternalsCorrect() {
        setAndAssertBits(BigInteger.ONE.negate(), 0x80000000, -31);
        setAndAssertBits(BigInteger.valueOf(-2), 0x80000000, -30);
        setAndAssertBits(BigInteger.valueOf(-3), 0xA0000000, -29);
        setAndAssertBits(BigInteger.valueOf(-4), 0x80000000, -29);
        setAndAssertBits(BigInteger.valueOf(-5), 0xB0000000, -28);
    }

    @Test
    public void whenConstructedFromLargePositiveBigIntegerThenInternalsCorrect() {
        mDecimal.set(BigInteger.valueOf(10).pow(70));
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0x5CBAEB5B, 202), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargeNegativeBigIntegerThenInternalsCorrect() {
        mDecimal.set(BigInteger.valueOf(10).pow(70).negate());
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0xA34514A5, 202), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromSmallPositiveIntegersBigDecimalThenInternalsCorrect() {
        setAndAssertBits(BigDecimal.ONE, 0x40000000, -30);
        setAndAssertBits(BigDecimal.valueOf(2), 0x40000000, -29);
        setAndAssertBits(BigDecimal.valueOf(3), 0x60000000, -29);
        setAndAssertBits(BigDecimal.valueOf(4), 0x40000000, -28);
        setAndAssertBits(BigDecimal.valueOf(5), 0x50000000, -28);
    }

    @Test
    public void whenConstructedFromSmallNegativeIntegersBigDecimalThenInternalsCorrect() {
        setAndAssertBits(BigDecimal.ONE.negate(), 0x80000000, -31);
        setAndAssertBits(BigDecimal.valueOf(-2), 0x80000000, -30);
        setAndAssertBits(BigDecimal.valueOf(-3), 0xA0000000, -29);
        setAndAssertBits(BigDecimal.valueOf(-4), 0x80000000, -29);
        setAndAssertBits(BigDecimal.valueOf(-5), 0xB0000000, -28);
    }

    @Test
    public void whenConstructedFromZeroBigDecimalThenInternalsCorrect() {
        setAndAssertBits(BigDecimal.ZERO,0, Float64ExpLTestUtils.ZERO_EXPONENT);
    }

    @Test
    public void whenConstructedFromPositiveFractionsBigDecimalThenInternalsCorrect() {
        BigDecimal value = BigDecimal.valueOf(21).divide(BigDecimal.valueOf(110), MathContext.DECIMAL128);
        mDecimal.set(value);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0x61BED61B, -33), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromNegativeFractionsBigDecimalThenInternalsCorrect() {
        BigDecimal value = BigDecimal.valueOf(-21).divide(BigDecimal.valueOf(110), MathContext.DECIMAL128);
        mDecimal.set(value);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0x9E4129E5, -33), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargePositiveBigDecimalThenInternalsCorrect() {
        mDecimal.set(new BigDecimal("94379921276728600000000000000"));
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0x4C3D4F6E, 66), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargeNegativeBigDecimalThenInternalsCorrect() {
        mDecimal.set(new BigDecimal("-94379921276728600000000000000"));
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0xB3C2B092, 66), mDecimal, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenSmallPowerOfTenThenExact() {
        Float64ExpLTestUtils.assertExactly(new Float32ExpL(10), Float32ExpL.getPowerOf10(1<<0));
        Float64ExpLTestUtils.assertExactly(new Float32ExpL(100), Float32ExpL.getPowerOf10(1<<1));
        Float64ExpLTestUtils.assertExactly(new Float32ExpL(10000), Float32ExpL.getPowerOf10(1<<2));
        Float64ExpLTestUtils.assertExactly(new Float32ExpL(100000000), Float32ExpL.getPowerOf10(1<<3));
    }

    @Test
    public void whenZeroPowerOfTenThenOne() {
        Float64ExpLTestUtils.assertExactly(1, Float32ExpL.getPowerOf10(0));
    }

    @Test
    public void whenTrivialPowerOfTenThenExact() {
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<0)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<0));
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<1)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<1));
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<2)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<2));
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<3)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<3));
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<4)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<4));
        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<5)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<5));

        Float64ExpLTestUtils.assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<19)-1).toBigInteger(), Float32ExpL.getPowerOf10(1<<19));
    }

    @Test
    public void whenSmallPowerOfTenThenVeryApproximate() {
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(2).toBigInteger(), Float32ExpL.getPowerOf10(3), Float64ExpLTestUtils.FULL_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(4).toBigInteger(), Float32ExpL.getPowerOf10(5), Float64ExpLTestUtils.FULL_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(5).toBigInteger(), Float32ExpL.getPowerOf10(6), Float64ExpLTestUtils.FULL_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(6).toBigInteger(), Float32ExpL.getPowerOf10(7), Float64ExpLTestUtils.FULL_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(8).toBigInteger(), Float32ExpL.getPowerOf10(9), Float64ExpLTestUtils.FULL_ACCURACY);

    }

    @Test
    public void whenDifficultPowerOfTenThenApproximate() {
        Float64ExpLTestUtils.assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(0x7FFFE).toBigInteger(), Float32ExpL.getPowerOf10(524287), Float64ExpLTestUtils.POW10_ACCURACY);
    }

    @Test
    public void whenFractionalPowerOfTenThenApproximate() {
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0.1), Float32ExpL.getPowerOf10(-1) , Float64ExpLTestUtils.POW10_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0.01), Float32ExpL.getPowerOf10(-2), Float64ExpLTestUtils.POW10_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0.001), Float32ExpL.getPowerOf10(-3), Float64ExpLTestUtils.POW10_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0.0001), Float32ExpL.getPowerOf10(-4), Float64ExpLTestUtils.POW10_ACCURACY);
        Float64ExpLTestUtils.assertApproximately(new Float32ExpL(0.00001), Float32ExpL.getPowerOf10(-5), Float64ExpLTestUtils.POW10_ACCURACY);
    }

    @Test
    public void whenPowerOf10OutOfRangeThrowException() throws Exception{
        Float32ExpL.getPowerOf10(646457002);
        exception.expect(ArithmeticException.class);
        Float32ExpL.getPowerOf10(646457003);
    }

    private void setAndAssertBits(BigInteger value, int expectedSignificand, int expectedExponent) {
        mDecimal.set(value);
        Float64ExpLTestUtils.assertBits(expectedSignificand, expectedExponent, mDecimal);
    }

    private void setAndAssertBits(BigDecimal value, int expectedSignificand, int expectedExponent) {
        mDecimal.set(value);
        Float64ExpLTestUtils.assertBits(expectedSignificand, expectedExponent, mDecimal);
    }
}