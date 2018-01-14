package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpPow2CombinitoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingPow2ThenResultsAreCorrect() {
        Float64ExpTestUtils.assertApproximately(Math.pow(2, left), new Float64Exp(left).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
    }
}