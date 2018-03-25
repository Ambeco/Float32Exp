package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float64ExpTestUtils;
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