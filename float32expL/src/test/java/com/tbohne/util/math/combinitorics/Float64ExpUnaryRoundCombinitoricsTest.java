package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpUnaryRoundCombinitoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingRoundThenResultsAreCorrect() {
        Float32ExpL decimal = new Float32ExpL(left / 4.0);
        double expectedValue = Math.round(left / 4.0);
        Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.round(), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}