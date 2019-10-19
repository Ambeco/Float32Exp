package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.LOG_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.POW_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;
import static com.tbohne.util.Decimal128TestUtils.assertExactly;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128PowersTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

    @Test
    public void whenLogPower2ThenInternalsAreCorrect() {
        mDecimal.set(1).log2();
        assertExactly(0, mDecimal);
        mDecimal.set(2).log2();
        assertExactly(1, mDecimal);
        mDecimal.set(4).log2();
        assertExactly(2, mDecimal);
        mDecimal.set(8).log2();
        assertExactly(3, mDecimal);
        mDecimal.set(16).log2();
        assertExactly(4, mDecimal);
    }

    @Test
    public void whenIntegerPow2ThenInternalsAreCorrect() {
        mDecimal.set(0).pow2();
        assertExactly(1, mDecimal);
        mDecimal.set(1).pow2();
        assertExactly(2, mDecimal);
        mDecimal.set(2).pow2();
        assertExactly(4, mDecimal);
        mDecimal.set(3).pow2();
        assertExactly(8, mDecimal);
        mDecimal.set(4).pow2();
        assertExactly(16, mDecimal);
    }

    @Test
    public void whenLogNonPower2ThenValueIsApproximate() {
        assertApproximately(1.58496250072, mDecimal.set(3).log2(), FULL_ACCURACY);
        assertApproximately(2.32192809489, mDecimal.set(5).log2(), FULL_ACCURACY);
        assertApproximately(2.58496250072, mDecimal.set(6).log2(), FULL_ACCURACY);
        assertApproximately(2.80735492206, mDecimal.set(7).log2(), FULL_ACCURACY);
        assertApproximately(3.16992500144, mDecimal.set(9).log2(), FULL_ACCURACY);
    }

    @Test
    public void whenPow2ToIntegerThenValueIsApproximate() {
        assertApproximately(3, mDecimal.set(1.58496250072).pow2(), FULL_ACCURACY);
        assertApproximately(5, mDecimal.set(2.32192809489).pow2(), FULL_ACCURACY);
        assertApproximately(6, mDecimal.set(2.58496250072).pow2(), FULL_ACCURACY);
        assertApproximately(7, mDecimal.set(2.80735492206).pow2(), FULL_ACCURACY);
        assertApproximately(9, mDecimal.set(3.16992500144).pow2(), FULL_ACCURACY);
    }

    @Test
    public void whenLogFractionLessThanOneThenValueIsApproximate() {
        assertApproximately(-3.32192809489, mDecimal.set(1.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-2.32192809489, mDecimal.set(2.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-1.73696559417, mDecimal.set(3.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-1.32192809489, mDecimal.set(4.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-1.00000000000, mDecimal.set(5.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-0.73696559416, mDecimal.set(6.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-0.51457317283, mDecimal.set(7.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-0.32192809488, mDecimal.set(8.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(-0.15200309344, mDecimal.set(9.0 / 10.0).log2(), LOG_ACCURACY);
    }

    @Test
    public void whenNegativePow2ThenValueIsApproximate() {
        assertApproximately(1.0 / 10.0, mDecimal.set(-3.32192809489).pow2(), FULL_ACCURACY);
        assertApproximately(2.0 / 10.0, mDecimal.set(-2.32192809489).pow2(), FULL_ACCURACY);
        assertApproximately(3.0 / 10.0, mDecimal.set(-1.73696559417).pow2(), FULL_ACCURACY);
        assertApproximately(4.0 / 10.0, mDecimal.set(-1.32192809489).pow2(), FULL_ACCURACY);
        assertApproximately(5.0 / 10.0, mDecimal.set(-1.00000000000).pow2(), FULL_ACCURACY);
        assertApproximately(6.0 / 10.0, mDecimal.set(-0.73696559416).pow2(), FULL_ACCURACY);
        assertApproximately(7.0 / 10.0, mDecimal.set(-0.51457317283).pow2(), FULL_ACCURACY);
        assertApproximately(8.0 / 10.0, mDecimal.set(-0.32192809488).pow2(), FULL_ACCURACY);
        assertApproximately(9.0 / 10.0, mDecimal.set(-0.15200309344).pow2(), FULL_ACCURACY);
    }

    @Test
    public void whenLogFractionGreaterThanOneThenValueIsApproximate() {
        assertApproximately(0.13750352375, mDecimal.set(11.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.26303440583, mDecimal.set(12.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.37851162325, mDecimal.set(13.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.48542682717, mDecimal.set(14.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.58496250072, mDecimal.set(15.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.67807190511, mDecimal.set(16.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.76553474636, mDecimal.set(17.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.84799690655, mDecimal.set(18.0 / 10.0).log2(), LOG_ACCURACY);
        assertApproximately(0.92599941855, mDecimal.set(19.0 / 10.0).log2(), LOG_ACCURACY);
    }
    @Test
    public void whenPow2BetweenZeroAndOneThenValueIsApproximate() {
        assertApproximately(11.0 / 10.0, mDecimal.set(0.13750352375).pow2(), FULL_ACCURACY);
        assertApproximately(12.0 / 10.0, mDecimal.set(0.26303440583).pow2(), FULL_ACCURACY);
        assertApproximately(13.0 / 10.0, mDecimal.set(0.37851162325).pow2(), FULL_ACCURACY);
        assertApproximately(14.0 / 10.0, mDecimal.set(0.48542682717).pow2(), FULL_ACCURACY);
        assertApproximately(15.0 / 10.0, mDecimal.set(0.58496250072).pow2(), FULL_ACCURACY);
        assertApproximately(16.0 / 10.0, mDecimal.set(0.67807190511).pow2(), FULL_ACCURACY);
        assertApproximately(17.0 / 10.0, mDecimal.set(0.76553474636).pow2(), FULL_ACCURACY);
        assertApproximately(18.0 / 10.0, mDecimal.set(0.84799690655).pow2(), FULL_ACCURACY);
        assertApproximately(19.0 / 10.0, mDecimal.set(0.92599941855).pow2(), FULL_ACCURACY);
    }

    @Test
    public void whenLogiThenValueIsApproximate() {
        assertApproximately(0, mDecimal.set(1).log2i(), FULL_ACCURACY);
        assertApproximately(1, mDecimal.set(2).log2i(), FULL_ACCURACY);
        assertApproximately(1, mDecimal.set(3).log2i(), FULL_ACCURACY);
        assertApproximately(2, mDecimal.set(4).log2i(), FULL_ACCURACY);
        assertApproximately(2, mDecimal.set(7).log2i(), FULL_ACCURACY);
        assertApproximately(3, mDecimal.set(8).log2i(), FULL_ACCURACY);
    }

    @Test
    public void whenPow2PiThenValueIsApproximate() {
        assertApproximately(8.824977827, mDecimal.set(3.14159265359).pow2(), POW_ACCURACY);
    }

    @Test
    public void whenPow2Pi100ThenValueIsApproximate() {
        assertApproximately(3.72702E+94, mDecimal.set(314.1592654).pow2(), POW_ACCURACY);
    }
}