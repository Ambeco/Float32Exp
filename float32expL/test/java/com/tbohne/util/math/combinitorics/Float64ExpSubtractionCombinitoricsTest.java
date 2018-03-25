package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpSubtractionCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingSubtractionThenResultsAreCorrect() {
        Float64ExpLTestUtils.assertApproximately(left - right, new Float32ExpL(left).subtract(right), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}