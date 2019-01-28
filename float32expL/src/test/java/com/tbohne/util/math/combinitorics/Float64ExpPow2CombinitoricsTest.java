package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpPow2CombinitoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingPow2ThenResultsAreCorrect() {
        Float64ExpLTestUtils.assertApproximately(Math.pow(2, left), new Float32ExpL(left).pow2(), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}