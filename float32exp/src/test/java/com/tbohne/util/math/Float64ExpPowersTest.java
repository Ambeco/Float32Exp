package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpPowersTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float64Exp mDecimal = new Float64Exp();

    @Test
    public void whenLogPower2ThenInternalsAreCorrect() {
        mDecimal.set(1).log2();
        Float64ExpTestUtils.assertExactly(0, mDecimal);
        mDecimal.set(2).log2();
        Float64ExpTestUtils.assertExactly(1, mDecimal);
        mDecimal.set(4).log2();
        Float64ExpTestUtils.assertExactly(2, mDecimal);
        mDecimal.set(8).log2();
        Float64ExpTestUtils.assertExactly(3, mDecimal);
        mDecimal.set(16).log2();
        Float64ExpTestUtils.assertExactly(4, mDecimal);
    }

    @Test
    public void whenIntegerPow2ThenInternalsAreCorrect() {
        mDecimal.set(0).pow2();
        Float64ExpTestUtils.assertExactly(1, mDecimal);
        mDecimal.set(1).pow2();
        Float64ExpTestUtils.assertExactly(2, mDecimal);
        mDecimal.set(2).pow2();
        Float64ExpTestUtils.assertExactly(4, mDecimal);
        mDecimal.set(3).pow2();
        Float64ExpTestUtils.assertExactly(8, mDecimal);
        mDecimal.set(4).pow2();
        Float64ExpTestUtils.assertExactly(16, mDecimal);
    }

    @Test
    public void whenLogNonPower2ThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(1.58496250072, mDecimal.set(3).log2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2.32192809489, mDecimal.set(5).log2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2.58496250072, mDecimal.set(6).log2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2.80735492206, mDecimal.set(7).log2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(3.16992500144, mDecimal.set(9).log2(), Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenPow2ToIntegerThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(3, mDecimal.set(1.58496250072).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(5, mDecimal.set(2.32192809489).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(6, mDecimal.set(2.58496250072).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(7, mDecimal.set(2.80735492206).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(9, mDecimal.set(3.16992500144).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenLogFractionLessThanOneThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(-3.32192809489, mDecimal.set(1.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-2.32192809489, mDecimal.set(2.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-1.73696559417, mDecimal.set(3.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-1.32192809489, mDecimal.set(4.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-1.00000000000, mDecimal.set(5.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-0.73696559416, mDecimal.set(6.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-0.51457317283, mDecimal.set(7.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-0.32192809488, mDecimal.set(8.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(-0.15200309344, mDecimal.set(9.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
    }

    @Test
    public void whenNegativePow2ThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(1.0 / 10.0, mDecimal.set(-3.32192809489).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2.0 / 10.0, mDecimal.set(-2.32192809489).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(3.0 / 10.0, mDecimal.set(-1.73696559417).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(4.0 / 10.0, mDecimal.set(-1.32192809489).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(5.0 / 10.0, mDecimal.set(-1.00000000000).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(6.0 / 10.0, mDecimal.set(-0.73696559416).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(7.0 / 10.0, mDecimal.set(-0.51457317283).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(8.0 / 10.0, mDecimal.set(-0.32192809488).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(9.0 / 10.0, mDecimal.set(-0.15200309344).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenLogFractionGreaterThanOneThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(0.13750352375, mDecimal.set(11.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.26303440583, mDecimal.set(12.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.37851162325, mDecimal.set(13.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.48542682717, mDecimal.set(14.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.58496250072, mDecimal.set(15.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.67807190511, mDecimal.set(16.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.76553474636, mDecimal.set(17.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.84799690655, mDecimal.set(18.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
        Float64ExpTestUtils.assertApproximately(0.92599941855, mDecimal.set(19.0 / 10.0).log2(), Float64ExpTestUtils.LOG_ACCURACY);
    }
    @Test
    public void whenPow2BetweenZeroAndOneThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(11.0 / 10.0, mDecimal.set(0.13750352375).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(12.0 / 10.0, mDecimal.set(0.26303440583).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(13.0 / 10.0, mDecimal.set(0.37851162325).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(14.0 / 10.0, mDecimal.set(0.48542682717).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(15.0 / 10.0, mDecimal.set(0.58496250072).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(16.0 / 10.0, mDecimal.set(0.67807190511).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(17.0 / 10.0, mDecimal.set(0.76553474636).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(18.0 / 10.0, mDecimal.set(0.84799690655).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(19.0 / 10.0, mDecimal.set(0.92599941855).pow2(), Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenLogiThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(0, mDecimal.set(1).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(1, mDecimal.set(2).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(1, mDecimal.set(3).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2, mDecimal.set(4).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(2, mDecimal.set(7).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
        Float64ExpTestUtils.assertApproximately(3, mDecimal.set(8).log2i(), Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenPow2PiThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(8.824977827, mDecimal.set(3.14159265359).pow2(), Float64ExpTestUtils.POW_ACCURACY);
    }

    @Test
    public void whenPow2Pi100ThenValueIsApproximate() {
        Float64ExpTestUtils.assertApproximately(3.72702E+94, mDecimal.set(314.1592654).pow2(), Float64ExpTestUtils.POW_ACCURACY);
    }
}