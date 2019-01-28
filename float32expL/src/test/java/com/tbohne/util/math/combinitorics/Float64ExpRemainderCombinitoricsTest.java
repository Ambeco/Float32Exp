package com.tbohne.util.math.combinitorics;

import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.Float64ExpLTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Float64ExpRemainderCombinitoricsTest extends Float64ExpCombinitoricsBase {

    @Test
    public void whenDoingRemainderThenResultsAreCorrect() {
        Float32ExpL decimal = new Float32ExpL(left);
        int expectedValue;
        try {
            expectedValue = left % right;

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.remainder(right);
            return;
        }
        Float64ExpLTestUtils.assertApproximately(expectedValue, decimal.remainder(right), Float64ExpLTestUtils.FULL_ACCURACY);
    }
}