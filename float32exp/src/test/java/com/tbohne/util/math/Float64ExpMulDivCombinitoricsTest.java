package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpMulDivCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingMulDivThenResultsAreCorrect() {

        Float64Exp decimal = new Float64Exp(Math.PI);
        double expectedValue = Math.PI * left / right;
        if (!Double.isInfinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.muldiv(left, right), Float64ExpTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.muldiv(left, right);
        }
    }
}