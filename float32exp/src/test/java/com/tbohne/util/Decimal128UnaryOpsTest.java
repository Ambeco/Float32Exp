package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;

import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.POW_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;

@RunWith(Parameterized.class)
public class Decimal128UnaryOpsTest {

    @Parameters(name = "{0}")
    public static ArrayList<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>(11);
        for(int i=0; i<13; ++i) {
            list.add(new Object[]{6-i});
        }
        return list;
    }

    @Parameter(0)  public int left;
    @Rule public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenDoingLog2ThenResultsAreCorrect() {
        Decimal128 decimal = new Decimal128(left);
        double expectedValue;
        try {
            expectedValue = Math.log(left)/Math.log(2);

        } catch (Throwable e) {
            expectedException.expect(e.getClass());
            expectedException.expectMessage(e.getMessage());
            decimal.log2();
            return;
        }
        assertApproximately(expectedValue, decimal.log2(), FULL_ACCURACY);
    }

    @Test
    public void whenDoingPow2ThenResultsAreCorrect() {
        assertApproximately(Math.pow(2, left), new Decimal128(left).pow2(), FULL_ACCURACY);
    }

    @Test
    public void whenDoingNegationThenResultsAreCorrect() {
        assertApproximately(-left, new Decimal128(left).negate(), FULL_ACCURACY);
    }
}