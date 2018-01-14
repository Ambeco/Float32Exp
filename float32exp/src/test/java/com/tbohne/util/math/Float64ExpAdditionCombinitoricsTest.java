package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpAdditionCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingAdditionThenResultsAreCorrect() {
        Float64ExpTestUtils.assertApproximately(left + right, new Float64Exp(left).add(right), Float64ExpTestUtils.FULL_ACCURACY);
    }
}