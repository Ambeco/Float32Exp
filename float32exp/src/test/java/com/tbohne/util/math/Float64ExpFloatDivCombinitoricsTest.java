package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpFloatDivCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingFloatDivisionThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        double expectedValue = ((double) left) / right;
        if (Double.isFinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.divide(right), Float64ExpTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.divide(right);
        }
    }
}