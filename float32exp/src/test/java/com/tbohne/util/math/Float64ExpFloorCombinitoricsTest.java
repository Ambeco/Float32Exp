package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpFloorCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingFloorThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        double expectedValue;
        try {
            expectedValue = left / right * right;

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.floor(right);
            return;
        }
        Float64ExpTestUtils.assertApproximately(expectedValue, decimal.floor(right), Float64ExpTestUtils.FULL_ACCURACY);
    }
}