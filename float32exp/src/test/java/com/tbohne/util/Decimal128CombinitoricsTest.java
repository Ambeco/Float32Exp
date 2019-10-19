package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;

import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.LOG_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.POW_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;

@RunWith(Parameterized.class)
public class Decimal128CombinitoricsTest {

    @Parameters(name = "{0} and {1}")
    public static ArrayList<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>(13*13);
        for(int i=0; i<15; ++i) {
            for(int j=0; j<15; ++j) {
                list.add(new Object[]{7-i, 7-j});
            }
        }
        return list;
    }

    @Parameter(0)  public int left;
    @Parameter(1)  public int right;
    @Rule public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenDoingAdditionThenResultsAreCorrect() {
        assertApproximately(left + right, new Decimal128(left).add(right), FULL_ACCURACY);
    }

    @Test
    public void whenDoingSubtractionThenResultsAreCorrect() {
        assertApproximately(left - right, new Decimal128(left).subtract(right), FULL_ACCURACY);
    }

    @Test
    public void whenDoingMultiplicationThenResultsAreCorrect() {
        assertApproximately(left * right, new Decimal128(left).multiply(right), FULL_ACCURACY);
    }

    @Test
    public void whenDoingFloatDivisionThenResultsAreCorrect() {
        Decimal128 decimal = new Decimal128(left);
        double expectedValue = ((double) left) / right;
        if (!Double.isInfinite(expectedValue)) {
            assertApproximately(expectedValue, decimal.divide(right), FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.divide(right);
        }
    }

    @Test
    public void whenDoingIntDivisionThenResultsAreCorrect() {
        Decimal128 decimal = new Decimal128(left);
        double expectedValue;
        try {
            expectedValue = left / right;

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.divideToIntegralValue(right);
            return;
        }
        assertApproximately(expectedValue, decimal.divideToIntegralValue(right), FULL_ACCURACY);
    }

    @Test
    public void whenDoingRemainderThenResultsAreCorrect() {
        Decimal128 decimal = new Decimal128(left);
        int expectedValue;
        try {
            expectedValue = left % right;

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.remainder(right);
            return;
        }
        assertApproximately(expectedValue, decimal.remainder(right), FULL_ACCURACY);
    }

    @Test
    public void whenDoingPowerThenResultsAreCorrect() {
        Decimal128 decimal = new Decimal128(left);
        double expectedValue = Math.pow(left, right);
        if (!Double.isInfinite(expectedValue)) {
            assertApproximately(expectedValue, decimal.pow(right), POW_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.pow(right);
        }
    }

    @Test
    public void whenDoingMulDivThenResultsAreCorrect() {

        Decimal128 decimal = new Decimal128(Math.PI);
        double expectedValue = Math.PI * left / right;
        if (!Double.isInfinite(expectedValue)) {
            assertApproximately(expectedValue, decimal.muldiv(left, right), FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.muldiv(left, right);
        }
    }
}