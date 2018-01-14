package com.tbohne.util.math;

import com.tbohne.util.Assert;

import java.math.BigInteger;
import java.util.Locale;

import static org.junit.Assert.assertTrue;

public class Float64ExpTestUtils {
    public static final int ZERO_EXPONENT = Integer.MIN_VALUE;
    public static final int FULL_ACCURACY = 31;
    public static final int LOG_ACCURACY = 28;
    public static final int POW_ACCURACY = 21;
    public static final int POW10_ACCURACY = 27;
    public static final int DOUBLE_ACCURACY = 30;

    public static final double SUBNORMAL = Double.MIN_NORMAL / 8;
    public final static double MAX_OFFSET = Double.MAX_VALUE / 1024/1024/1024;


    public static void setAndAssertBits(long value, int expectedSignificand, int expectedExponent, Float64Exp float64Exp) {
        assertApproximately((double)value, expectedSignificand * Math.pow(2,expectedExponent), FULL_ACCURACY);

        float64Exp.setNormalized(value, 0);
        assertBits(expectedSignificand, expectedExponent, float64Exp);
    }

    public static void setAndAssertBits(double value, int expectedSignificand, int expectedExponent, Float64Exp float64Exp) {
        assertApproximately(value, expectedSignificand * Math.pow(2,expectedExponent), FULL_ACCURACY);

        float64Exp.set(value);
        assertBits(expectedSignificand, expectedExponent, float64Exp);
    }

    public static void assertBits(int expectedSignificand, int expectedExponent, Float64Exp actualValue) {
        ImmutableFloat64Exp expected = new ImmutableFloat64Exp(expectedSignificand, expectedExponent);
        String format = "Expected value %s but found %s.\n"
                + "Expected Significand %s (%d) but found %s (%d).\n"
                + "Expected Exponent %d but found %d.";
        String binaryExpected =
                String.format("%32s", Integer.toBinaryString(expectedSignificand))
                        .replace(" ", "0");
        String binaryActual =
                String.format("%32s", Integer.toBinaryString(actualValue.significand()))
                        .replace(" ", "0");
        String msg = String.format(Locale.US,
                format,
                expected,
                actualValue,
                binaryExpected,
                expectedSignificand,
                binaryActual,
                actualValue.significand(),
                expectedExponent,
                actualValue.exponent());
        assertTrue(msg, expectedSignificand == actualValue.significand()
                && expectedExponent == actualValue.exponent());
    }

    public static void assertExactly(long expectedValue, Float64Exp actualValue) {
        Float64Exp expected = new Float64Exp(expectedValue);
        assertBits(expected.significand(), expected.exponent(), actualValue);
    }

    public static void assertExactly(double expectedValue, double actualValue) {
        if (expectedValue != actualValue) {
            String msg = String.format("Expected value %f but found %f.\n", expectedValue, actualValue);
            assertTrue(msg, false);
        }
    }

    public static void assertExactly(Float64Exp expected, Float64ExpChainedExpression actualValue) {
        assertBits(expected.significand(), expected.exponent(), (Float64Exp) actualValue);
    }

    public static void assertExactly(BigInteger expectedValue, Float64ExpChainedExpression actualValue) {
        Float64Exp expected = new Float64Exp(expectedValue);
        assertBits(expected.significand(), expected.exponent(), (Float64Exp) actualValue);
    }

    public static void assertApproximately(double expectedValue, double actualValue, int bitsSimilar) {
        Assert.assertApproximately(expectedValue, actualValue, bitsSimilar);
    }

    public static void assertApproximately(BigInteger expectedValue, Float64ExpChainedExpression actualValue, int bitsSimilar) {
        Float64Exp expected = new Float64Exp(expectedValue);
        if (!expected.approximately(actualValue, bitsSimilar)) {
            assertBits(expected.significand(), expected.exponent(), (Float64Exp) actualValue);
        }
    }

    public static void assertApproximately(double expectedValue, Float64ExpChainedExpression actualValue, int bitsSimilar) {
        Float64Exp expected = new Float64Exp(expectedValue);
        assertApproximately(expected, actualValue, bitsSimilar);
    }

    public static void assertApproximately(IFloat64Exp expected, Float64ExpChainedExpression actualValue, int bitsSimilar) {
        if (!expected.approximately(actualValue, bitsSimilar)) {
            assertBits(expected.significand(), expected.exponent(), (Float64Exp) actualValue);
        }
    }
}