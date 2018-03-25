package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpMulDivCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingMulDivThenResultsAreCorrect() {

        Float32Exp decimal = new Float32Exp(Math.PI);
        double expectedValue = Math.PI * left / right;
        if (Double.isFinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.muldiv(left, right), Float64ExpTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            decimal.muldiv(left, right);
        }
    }
}