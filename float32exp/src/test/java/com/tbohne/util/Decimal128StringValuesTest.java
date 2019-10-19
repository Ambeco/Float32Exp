package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.Decimal128SharedBase.DEFAULT_EXPONENT_TO_STRING;
import static com.tbohne.util.Decimal128SharedBase.DEFAULT_MAX_PRECISION;
import static com.tbohne.util.Decimal128SharedBase.DEFAULT_MIN_PRECISION;
import static com.tbohne.util.Decimal128SharedBase.DEFAULT_STRING_EXPONENT_MULTIPLE;
import static com.tbohne.util.Decimal128TestUtils.DOUBLE_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128StringValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

    @Test
    public void whenZeroThenToStringIsExact() {
        Assert.assertEquals("0", Decimal128ToString(0));
    }

    @Test
    public void whenSmallIntegerThenToStringIsExact() {
        Assert.assertEquals("1", Decimal128ToString(1));
        Assert.assertEquals("2", Decimal128ToString(2));
        Assert.assertEquals("3", Decimal128ToString(3));
        Assert.assertEquals("4", Decimal128ToString(4));
        Assert.assertEquals("5", Decimal128ToString(5));
    }

    @Test
    public void whenSmallNegativeIntegerThenToStringIsExact() {
        Assert.assertEquals("-1", Decimal128ToString(-1));
        Assert.assertEquals("-2", Decimal128ToString(-2));
        Assert.assertEquals("-3", Decimal128ToString(-3));
        Assert.assertEquals("-4", Decimal128ToString(-4));
        Assert.assertEquals("-5", Decimal128ToString(-5));
    }

    @Test
    public void whenSimpleLargeIntegerThenToStringIsExact() {
        Assert.assertEquals("0.5",  Decimal128ToString(0.5));
        Assert.assertEquals("9e6",  Decimal128ToString(9000000));
        Assert.assertEquals("3e8",  Decimal128ToString(300000000));
        Assert.assertEquals("-8e7", Decimal128ToString(-80000000));
    }

    @Test
    public void whenComplexNumberThenToStringIsApproximate() {
        toStringApproxRoundTrip(3.141592653589793238);
        toStringApproxRoundTrip(3141592653589793238L);
    }

    @Test
    public void whenEngFormatThenDecimalInRightPlace() {
        Assert.assertEquals("1.00",  Decimal128ToEngString(1));
        Assert.assertEquals("10.0",  Decimal128ToEngString(10));
        Assert.assertEquals("100",  Decimal128ToEngString(100));
        Assert.assertEquals("1.00e3",  Decimal128ToEngString(1000));
        Assert.assertEquals("10.0e3",  Decimal128ToEngString(10000));
        Assert.assertEquals("100e3",  Decimal128ToEngString(100000));
        Assert.assertEquals("1.00e6",  Decimal128ToEngString(1000000));
    }

    @Test
    public void whenEngFormatZeroesThenDecimalInRightPlace() {
        Assert.assertEquals("0.00",  Decimal128ToEngString(0));
    }

    @Test
    public void whenConstructFromSmallPositiveIntStringThenPrecise() {
        setAndAssertApproximately(1.0, "1", FULL_ACCURACY);
        setAndAssertApproximately(2.0, "2", FULL_ACCURACY);
        setAndAssertApproximately(3.0, "3", FULL_ACCURACY);
        setAndAssertApproximately(4.0, "4", FULL_ACCURACY);
        setAndAssertApproximately(5.0, "5", FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromSmallNegativeIntStringThenPrecise() {
        setAndAssertApproximately(-1.0, "-1", FULL_ACCURACY);
        setAndAssertApproximately(-2.0, "-2", FULL_ACCURACY);
        setAndAssertApproximately(-3.0, "-3", FULL_ACCURACY);
        setAndAssertApproximately(-4.0, "-4", FULL_ACCURACY);
        setAndAssertApproximately(-5.0, "-5", FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromZeroStringThenPrecise() {
        setAndAssertApproximately(0.0, "00.00", FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653, "31415.92653", FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLongFractionStringThenApproximate() {
        setAndAssertApproximately(31415.92653589793238, "31415.92653589793238", FULL_ACCURACY);
    }

    @Test
    public void whenConstructFromLargeStringThenApproximate() {
        setAndAssertApproximately(3141592653589793238.0, "3141592653589793238", FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromENotationThenApproximate() {
        setAndAssertApproximately(3E15, "3E15", FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromENotationThenApproximate() {
        setAndAssertApproximately(3.1415E15, "3.1415E15", FULL_ACCURACY);
    }

    @Test
    public void whenConstructIntFromBNotationThenApproximate() {
        setAndAssertApproximately(3*Math.pow(2.0,15), "3B15", FULL_ACCURACY);
    }

    @Test
    public void whenConstructDecimalFromBNotationThenApproximate() {
        setAndAssertApproximately(3.1415*Math.pow(2,15), "3.1415B15", FULL_ACCURACY);
    }

    private void setAndAssertApproximately(double expected, String input, int digits) {
        mDecimal.set(input);
        assertApproximately(expected, mDecimal, digits);
    }

    private String Decimal128ToString(long value) {
        StringBuilder sb = new StringBuilder();
        mDecimal.set(value).toString(sb, DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }

    private String Decimal128ToEngString(long value) {
        return mDecimal.set(value).toEngineeringString();
    }

    private String Decimal128ToString(double value) {
        StringBuilder sb = new StringBuilder();
        mDecimal.set(value).toString(sb, DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }

    private void toStringApproxRoundTrip(double value) {
        String string = Decimal128ToString(value);
        double result = Double.parseDouble(string);
        Assert.assertApproximately("Failed to round trip through '"+string+"'", value, result, FULL_ACCURACY);
    }
}