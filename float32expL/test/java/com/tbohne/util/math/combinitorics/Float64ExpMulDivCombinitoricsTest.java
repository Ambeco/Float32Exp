package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpMulDivCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingMulDivThenResultsAreCorrect() {

        Float32ExpL decimal = new Float32ExpL(Math.PI);
        double expectedValue = Math.PI * left / right;
        if (Double.isFinite(expectedValue)) {
            Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.muldiv(left, right), Float64ExpLTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.muldiv(left, right);
        }
    }
}