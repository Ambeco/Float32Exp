package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpSubtractionCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingSubtractionThenResultsAreCorrect() {
        Float64ExpTestUtils.assertApproximately(left - right, new Float32Exp(left).subtract(right), Float64ExpTestUtils.FULL_ACCURACY);
    }
}