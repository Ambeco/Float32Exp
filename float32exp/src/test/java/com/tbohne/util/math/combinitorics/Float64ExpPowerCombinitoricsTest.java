package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpPowerCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingPowerThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        double expectedValue = Math.pow(left, right);
        if (!Double.isInfinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.pow(right), Float64ExpTestUtils.POW_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.pow(right);
        }
    }
}