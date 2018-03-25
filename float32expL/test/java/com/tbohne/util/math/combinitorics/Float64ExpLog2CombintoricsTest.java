package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpLog2CombintoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingLog2ThenResultsAreCorrect() {
        Float32ExpL decimal = new Float32ExpL(left);
        double expectedValue = Math.log(left) / Math.log(2);
        if (Double.isFinite(expectedValue)) {
            Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.log2(), Float64ExpLTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(IllegalArgumentException.class);
            decimal.log2();
        }
    }
}