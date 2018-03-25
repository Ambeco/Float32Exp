package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpLog2CombintoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingLog2ThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left);
        double expectedValue = Math.log(left) / Math.log(2);
        if (Double.isFinite(expectedValue)) {
            Float64ExpTestUtils.assertApproximately(expectedValue, decimal.log2(), Float64ExpTestUtils.FULL_ACCURACY);
        } else {
            expectedException.expect(IllegalArgumentException.class);
            decimal.log2();
        }
    }
}