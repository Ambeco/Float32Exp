package com.tbohne.util.math;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Ignore
public class Float64ExpPowerCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    @Ignore
    public void whenDoingPowerThenResultsAreCorrect() {
        Float64Exp decimal = new Float64Exp(left);
        double expectedValue = Math.pow(left, right);
        if (!Double.isInfinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.pow(right), Float64ExpTestUtils.POW_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.pow(right);
        }
    }
}