package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpRoundCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingRoundThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        if (right != 0) {
            double expectedValue = (int) ((left + right / 2.0) / right) * right;
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.round(right), Float64ExpTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(ArithmeticException.class);
            expectedException.expectMessage("/ by zero");
            decimal.round(right);
            return;
        }
    }
}