package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpMultiplicationCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingMultiplicationThenResultsAreCorrect() {
        Float64ExpTestUtils.assertApproximately(left * right, new Float64Exp(left).multiply(right), Float64ExpTestUtils.FULL_ACCURACY);
    }
}