package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpFloatDivCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingFloatDivisionThenResultsAreCorrect() {
        Float32ExpL decimal = new Float32ExpL(left);
        double expectedValue = ((double) left) / right;
        if (Double.isFinite(expectedValue)) {
            Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.divide(right), Float64ExpLTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.divide(right);
        }
    }
}