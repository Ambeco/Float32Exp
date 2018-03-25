package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpUnaryRoundCombinitoricsTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingRoundThenResultsAreCorrect() {
        Float32Exp decimal = new Float32Exp(left / 4.0);
        double expectedValue = Math.round(left / 4.0);
        Float64ExpTestUtils.assertApproximately(expectedValue, decimal.round(), Float64ExpTestUtils.FULL_ACCURACY);
    }
}