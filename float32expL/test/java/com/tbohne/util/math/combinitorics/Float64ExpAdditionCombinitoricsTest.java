package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpAdditionCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingAdditionThenResultsAreCorrect() {
        Float64ExpLTestUtils.assertApproximately(left + right, new Float32ExpL(left).add(right), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}