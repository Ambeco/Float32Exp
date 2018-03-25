package com.tbohne.util.math;

import com.tbohne.util.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_EXPONENT_TO_STRING;
import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_MAX_PRECISION;
import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_MIN_PRECISION;
import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_STRING_EXPONENT_MULTIPLE;
import static com.tbohne.util.math.Float64ExpLTestUtils.setAndAssertBits;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpLStringValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32ExpL mDecimal = new Float32ExpL();

    @Test
    public void whenConstructedFromZeroThenInternalsAreCorrect() {
        setAndAssertBits("0", 0, Float64ExpLTestUtils.ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntThenInternalsAreCorrect() {
        setAndAssertBits("1", 0x40000000, -30, mDecimal);
        setAndAssertBits("2", 0x40000000, -29, mDecimal);
        setAndAssertBits("3", 0x60000000, -29, mDecimal);
        setAndAssertBits("4", 0x40000000, -28, mDecimal);
        setAndAssertBits("5", 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntThenInternalsAreCorrect() {
        setAndAssertBits("-1", 0x80000000, -31, mDecimal);
        setAndAssertBits("-2", 0x80000000, -30, mDecimal);
        setAndAssertBits("-3", 0xA0000000, -29, mDecimal);
        setAndAssertBits("-4", 0x80000000, -29, mDecimal);
        setAndAssertBits("-5", 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPowLongThenInternalsAreCorrect() {
        setAndAssertBits("8", 0x40000000, -27, mDecimal);
        setAndAssertBits("64", 0x40000000, -24, mDecimal);
        setAndAssertBits("512", 0x40000000, -21, mDecimal);
        setAndAssertBits("8192", 0x40000000, -17, mDecimal);
        setAndAssertBits("65536", 0x40000000, -14, mDecimal);
    }

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

    // Numbers in this range are held precisely, but do not .toString precisely :(
    // One inner calculation (this/pow10) sometimes rounds the wrong way, causing it to be off by a single bit.
    // So these values are precisely chosen, rather than arbitrary. Very cheety.
    @Test
    public void whenMultiDigitIntegerThenToStringIsExact() {
        Assert.assertEquals("8", Float64ExpToString(8));
        Assert.assertEquals("6.4E1", Float64ExpToString(64));
        Assert.assertEquals("5.13E2", Float64ExpToString(513));
        Assert.assertEquals("8.191E3", Float64ExpToString(8191));
        Assert.assertEquals("6.5536E4", Float64ExpToString(65536));
    }

    @Test
    public void whenSimpleLargeIntegerThenToStringIsExact() {
        Assert.assertEquals("5E-1",  Float64ExpToString(0.5));
        Assert.assertEquals("9E6",  Float64ExpToString(9000000));
        Assert.assertEquals("3E8",  Float64ExpToString(300000000));
        Assert.assertEquals("-8E7", Float64ExpToString(-80000000));
    }

    @Test
    public void whenPreciseNumberThenToStringIsApproximate() {
        toStringApproxRoundTrip(0.0003141592653589793238, Float64ExpLTestUtils.FULL_ACCURACY);
        toStringApproxRoundTrip(3.141592653589793238, Float64ExpLTestUtils.FULL_ACCURACY);
        toStringApproxRoundTrip(3141592653589793238L, Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenEngFormatThenDecimalInRightPlace() {
        Assert.assertEquals("1.00",  Float64ExpToEngString(1));
        Assert.assertEquals("10.0",  Float64ExpToEngString(10));
        Assert.assertEquals("100",  Float64ExpToEngString(100));
        Assert.assertEquals("1.00E3",  Float64ExpToEngString(1000));
        Assert.assertEquals("10.0E3",  Float64ExpToEngString(10000));
        Assert.assertEquals("100E3",  Float64ExpToEngString(100000));
        Assert.assertEquals("1.00E6",  Float64ExpToEngString(1000000));
    }

    @Test
    public void whenEngFormatZeroesThenDecimalInRightPlace() {
        Assert.assertEquals("0.00",  Float64ExpToEngString(0));
    }

    @Test
    public void whenConstructFromSmallPositiveIntStringThenPrecise() {
        setAndAssertApproximately(1.0, "1", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(2.0, "2", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(3.0, "3", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(4.0, "4", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(5.0, "5", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromSmallNegativeIntStringThenPrecise() {
        setAndAssertApproximately(-1.0, "-1", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-2.0, "-2", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-3.0, "-3", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-4.0, "-4", Float64ExpLTestUtils.FULL_ACCURACY);
        setAndAssertApproximately(-5.0, "-5", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromZeroStringThenPrecise() {
        setAndAssertApproximately(0.0, "00.00", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653, "31415.92653", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLongFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653589793238, "31415.92653589793238", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLargeStringThenApproximate() {
        setAndAssertApproximately(3141592653589793238.0, "3141592653589793238", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromENotationThenApproximate() {
        setAndAssertApproximately(3E15, "3E15", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromENotationThenApproximate() {
        setAndAssertApproximately(3.1415E15, "3.1415E15", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromBNotationThenApproximate() {
        setAndAssertApproximately(3*Math.pow(2.0,15), "3B15", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromBNotationThenApproximate() {
        setAndAssertApproximately(3.1415*Math.pow(2,15), "3.1415B15", Float64ExpLTestUtils.FULL_ACCURACY);
    }

    private void setAndAssertApproximately(double expected, String input, int digits) {
        mDecimal.set(input);
        Float64ExpLTestUtils.assertApproximately(expected, mDecimal, digits);
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

    private void toStringApproxRoundTrip(double value, int bitsSimilarCount) {
        String string = Float64ExpToString(value);
        double result = Double.parseDouble(string);
        Assert.assertApproximately("Failed to round trip through '"+string+"'", value, result, bitsSimilarCount);
    }
}