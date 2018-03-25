package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpUnaryFloorCombinitoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingRoundThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left / 4.0);
        double expectedValue = Math.floor(left / 4.0);
        Float64ExpTestUtils.assertApproximately(expectedValue, decimal.floor(), Float64ExpTestUtils.FULL_ACCURACY);
    }
}