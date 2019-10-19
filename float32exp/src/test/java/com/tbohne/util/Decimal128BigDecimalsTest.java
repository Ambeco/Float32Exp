package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static com.tbohne.util.Decimal128TestUtils.DOUBLE_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.POW10_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.POW_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.SUBNORMAL;
import static com.tbohne.util.Decimal128TestUtils.ZERO_EXPONENT;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;
import static com.tbohne.util.Decimal128TestUtils.assertBits;
import static com.tbohne.util.Decimal128TestUtils.assertExactly;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128BigDecimalsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

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
        assertApproximately(new Decimal128(0x5CBAEB5B, 202), mDecimal, DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargeNegativeBigIntegerThenInternalsCorrect() {
        mDecimal.set(BigInteger.valueOf(10).pow(70).negate());
        assertApproximately(new Decimal128(0xA34514A5, 202), mDecimal, DOUBLE_ACCURACY);
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
        setAndAssertBits(BigDecimal.ZERO,0, ZERO_EXPONENT);
    }

    @Test
    public void whenConstructedFromPositiveFractionsBigDecimalThenInternalsCorrect() {
        BigDecimal value = BigDecimal.valueOf(21).divide(BigDecimal.valueOf(110), MathContext.DECIMAL128);
        mDecimal.set(value);
        assertApproximately(new Decimal128(0x61BED61B, -33), mDecimal, DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromNegativeFractionsBigDecimalThenInternalsCorrect() {
        BigDecimal value = BigDecimal.valueOf(-21).divide(BigDecimal.valueOf(110), MathContext.DECIMAL128);
        mDecimal.set(value);
        assertApproximately(new Decimal128(0x9E4129E5, -33), mDecimal, DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargePositiveBigDecimalThenInternalsCorrect() {
        mDecimal.set(new BigDecimal("94379921276728600000000000000"));
        assertApproximately(new Decimal128(0x4C3D4F6E, 66), mDecimal, DOUBLE_ACCURACY);
    }

    @Test
    public void whenConstructedFromLargeNegativeBigDecimalThenInternalsCorrect() {
        mDecimal.set(new BigDecimal("-94379921276728600000000000000"));
        assertApproximately(new Decimal128(0xB3C2B092, 66), mDecimal, DOUBLE_ACCURACY);
    }

    @Test
    public void whenSmallPowerOfTenThenExact() {
        assertExactly(new Decimal128(10), Decimal128.getPowerOf10(1<<0));
        assertExactly(new Decimal128(100), Decimal128.getPowerOf10(1<<1));
        assertExactly(new Decimal128(10000), Decimal128.getPowerOf10(1<<2));
        assertExactly(new Decimal128(100000000), Decimal128.getPowerOf10(1<<3));
    }

    @Test
    public void whenZeroPowerOfTenThenOne() {
        assertExactly(1, Decimal128.getPowerOf10(0));
    }

    @Test
    public void whenTrivialPowerOfTenThenExact() {
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<0)-1).toBigInteger(), Decimal128.getPowerOf10(1<<0));
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<1)-1).toBigInteger(), Decimal128.getPowerOf10(1<<1));
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<2)-1).toBigInteger(), Decimal128.getPowerOf10(1<<2));
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<3)-1).toBigInteger(), Decimal128.getPowerOf10(1<<3));
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<4)-1).toBigInteger(), Decimal128.getPowerOf10(1<<4));
        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<5)-1).toBigInteger(), Decimal128.getPowerOf10(1<<5));

        assertExactly(BigDecimal.TEN.scaleByPowerOfTen((1<<19)-1).toBigInteger(), Decimal128.getPowerOf10(1<<19));
    }

    @Test
    public void whenSmallPowerOfTenThenVeryApproximate() {
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(2).toBigInteger(), Decimal128.getPowerOf10(3), FULL_ACCURACY);
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(4).toBigInteger(), Decimal128.getPowerOf10(5), FULL_ACCURACY);
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(5).toBigInteger(), Decimal128.getPowerOf10(6), FULL_ACCURACY);
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(6).toBigInteger(), Decimal128.getPowerOf10(7), FULL_ACCURACY);
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(8).toBigInteger(), Decimal128.getPowerOf10(9), FULL_ACCURACY);

    }

    @Test
    public void whenDifficultPowerOfTenThenApproximate() {
        assertApproximately(BigDecimal.TEN.scaleByPowerOfTen(0x7FFFE).toBigInteger(), Decimal128.getPowerOf10(524287), POW10_ACCURACY);
    }

    @Test
    public void whenFractionalPowerOfTenThenApproximate() {
        assertApproximately(new Decimal128(0.1), Decimal128.getPowerOf10(-1) , POW10_ACCURACY);
        assertApproximately(new Decimal128(0.01), Decimal128.getPowerOf10(-2), POW10_ACCURACY);
        assertApproximately(new Decimal128(0.001), Decimal128.getPowerOf10(-3), POW10_ACCURACY);
        assertApproximately(new Decimal128(0.0001), Decimal128.getPowerOf10(-4), POW10_ACCURACY);
        assertApproximately(new Decimal128(0.00001), Decimal128.getPowerOf10(-5), POW10_ACCURACY);
    }

    @Test
    public void whenPowerOf10OutOfRangeThrowException() throws Exception{
        Decimal128.getPowerOf10(646457002);
        exception.expect(ArithmeticException.class);
        Decimal128.getPowerOf10(646457003);
    }

    private void setAndAssertBits(BigInteger value, int expectedSignificand, int expectedExponent) {
        mDecimal.set(value);
        assertBits(expectedSignificand, expectedExponent, mDecimal);
    }

    private void setAndAssertBits(BigDecimal value, int expectedSignificand, int expectedExponent) {
        mDecimal.set(value);
        assertBits(expectedSignificand, expectedExponent, mDecimal);
    }
}