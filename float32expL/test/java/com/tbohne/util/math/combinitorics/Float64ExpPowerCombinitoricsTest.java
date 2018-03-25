package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpPowerCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingPowerThenResultsAreCorrect() {
        Float32ExpL decimal = new Float32ExpL(left);
        double expectedValue = Math.pow(left, right);
        if (!Double.isInfinite(expectedValue)) {
            Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.pow(right), Float64ExpLTestUtils.POW_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.pow(right);
        }
    }
}