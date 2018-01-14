package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpLog2CombintoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingLog2ThenResultsAreCorrect() {
        Float64Exp decimal = new Float64Exp(left);
        double expectedValue;
        try {
            expectedValue = Math.log(left)/Math.log(2);

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.log2();
            return;
        }
        Float64ExpTestUtils.assertApproximately(expectedValue, decimal.log2(), Float64ExpTestUtils.FULL_ACCURACY);
    }
}