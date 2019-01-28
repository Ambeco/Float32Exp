package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpNegationCombinitorisTest extends Float64ExpUnaryCombinitoricsBase {

    @Test
    public void whenDoingNegationThenResultsAreCorrect() {
        Float64ExpLTestUtils.assertApproximately(-left, new Float32ExpL(left).negate(), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}