package com.tbohne.util.math;

import com.tbohne.util.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.math.Float64ExpSharedBase.DEFAULT_EXPONENT_TO_STRING;
import static com.tbohne.util.math.Float64ExpSharedBase.DEFAULT_MAX_PRECISION;
import static com.tbohne.util.math.Float64ExpSharedBase.DEFAULT_MIN_PRECISION;
import static com.tbohne.util.math.Float64ExpSharedBase.DEFAULT_STRING_EXPONENT_MULTIPLE;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpStringValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float64Exp mDecimal = new Float64Exp();

    @Test
    public void whenZeroThenToStringIsExact() {
        Assert.assertEquals("0", Float64ExpToString(0));
    }

    @Test
    public void whenSmallIntegerThenToStringIsExact() {
        Assert.assertEquals("1", Float64ExpToString(1));
        Assert.assertEquals("2", Float64ExpToString(2));
        Assert.assertEquals("3", Float64ExpToString(3));
        Assert.assertEquals("4", Float64ExpToString(4));
        Assert.assertEquals("5", Float64ExpToString(5));
    }

    @Test
    public void whenSmallNegativeIntegerThenToStringIsExact() {
        Assert.assertEquals("-1", Float64ExpToString(-1));
        Assert.assertEquals("-2", Float64ExpToString(-2));
        Assert.assertEquals("-3", Float64ExpToString(-3));
        Assert.assertEquals("-4", Float64ExpToString(-4));
        Assert.assertEquals("-5", Float64ExpToString(-5));
    }

    @Test
    public void whenSimpleLargeIntegerThenToStringIsExact() {
        Assert.assertEquals("0.5",  Float64ExpToString(0.5));
        Assert.assertEquals("9e6",  Float64ExpToString(9000000));
        Assert.assertEquals("3e8",  Float64ExpToString(300000000));
        Assert.assertEquals("-8e7", Float64ExpToString(-80000000));
    }

    @Test
    public void whenComplexNumberThenToStringIsApproximate() {
        toStringApproxRoundTrip(3.141592653589793238);
        toStringApproxRoundTrip(3141592653589793238L);
    }

    @Test
    public void whenEngFormatThenDecimalInRightPlace() {
        Assert.assertEquals("1.00",  Float64ExpToEngString(1));
        Assert.assertEquals("10.0",  Float64ExpToEngString(10));
        Assert.assertEquals("100",  Float64ExpToEngString(100));
        Assert.assertEquals("1.00e3",  Float64ExpToEngString(1000));
        Assert.assertEquals("10.0e3",  Float64ExpToEngString(10000));
        Assert.assertEquals("100e3",  Float64ExpToEngString(100000));
        Assert.assertEquals("1.00e6",  Float64ExpToEngString(1000000));
    }

    @Test
    public void whenEngFormatZeroesThenDecimalInRightPlace() {
        Assert.assertEquals("0.00",  Float64ExpToEngString(0));
    }

    @Test
    public void whenConstructFromSmallPositiveIntStringThenPrecise() {
        setAndAssertApproximately(1.0, "1", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(2.0, "2", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(3.0, "3", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(4.0, "4", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(5.0, "5", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromSmallNegativeIntStringThenPrecise() {
        setAndAssertApproximately(-1.0, "-1", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-2.0, "-2", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-3.0, "-3", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-4.0, "-4", Float64ExpTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-5.0, "-5", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromZeroStringThenPrecise() {
        setAndAssertApproximately(0.0, "00.00", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653, "31415.92653", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLongFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653589793238, "31415.92653589793238", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLargeStringThenApproximate() {
        setAndAssertApproximately(3141592653589793238.0, "3141592653589793238", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromENotationThenApproximate() {
        setAndAssertApproximately(3E15, "3E15", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromENotationThenApproximate() {
        setAndAssertApproximately(3.1415E15, "3.1415E15", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromBNotationThenApproximate() {
        setAndAssertApproximately(3*Math.pow(2.0,15), "3B15", Float64ExpTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromBNotationThenApproximate() {
        setAndAssertApproximately(3.1415*Math.pow(2,15), "3.1415B15", Float64ExpTestUtils.FULL_ACCURACY);
    }

    private void setAndAssertApproximately(double expected, String input, int digits) {
        mDecimal.set(input);
        Float64ExpTestUtils.assertApproximately(expected, mDecimal, digits);
    }

    private String Float64ExpToString(long value) {
        StringBuilder sb = new StringBuilder();
        mDecimal.set(value).toString(sb, DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }

    private String Float64ExpToEngString(long value) {
        return mDecimal.set(value).toEngineeringString();
    }

    private String Float64ExpToString(double value) {
        StringBuilder sb = new StringBuilder();
        mDecimal.set(value).toString(sb, DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }

    private void toStringApproxRoundTrip(double value) {
        String string = Float64ExpToString(value);
        double result = Double.parseDouble(string);
        Assert.assertApproximately("Failed to round trip through '"+string+"'", value, result, Float64ExpTestUtils.FULL_ACCURACY);
    }
}