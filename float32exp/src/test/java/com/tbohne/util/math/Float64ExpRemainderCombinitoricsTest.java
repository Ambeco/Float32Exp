package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpRemainderCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingRemainderThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        int expectedValue;
        try {
            expectedValue = left % right;

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.remainder(right);
            return;
        }
        Float64ExpTestUtils.assertApproximately(expectedValue, decimal.remainder(right), Float64ExpTestUtils.FULL_ACCURACY);
    }
}