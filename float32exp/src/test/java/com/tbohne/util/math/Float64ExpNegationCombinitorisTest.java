package com.tbohne.util.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpNegationCombinitorisTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingNegationThenResultsAreCorrect() {
        Float64ExpTestUtils.assertApproximately(-left, new Float32Exp(left).negate(), Float64ExpTestUtils.FULL_ACCURACY);
    }
}