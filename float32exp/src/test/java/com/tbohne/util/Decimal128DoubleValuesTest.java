package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.Decimal128TestUtils.DOUBLE_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.MAX_OFFSET;
import static com.tbohne.util.Decimal128TestUtils.SUBNORMAL;
import static com.tbohne.util.Decimal128TestUtils.ZERO_EXPONENT;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;
import static com.tbohne.util.Decimal128TestUtils.assertExactly;
import static com.tbohne.util.Decimal128TestUtils.setAndAssertBits;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128DoubleValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

    @Test
    public void whenConstructedFromZeroDoubleThenInternalsAreCorrect() {
        setAndAssertBits(0.0, 0, ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntegerDoubleThenInternalsAreCorrect() {
        setAndAssertBits(1.0, 0x40000000, -30, mDecimal);
        setAndAssertBits(2.0, 0x40000000, -29, mDecimal);
        setAndAssertBits(3.0, 0x60000000, -29, mDecimal);
        setAndAssertBits(4.0, 0x40000000, -28, mDecimal);
        setAndAssertBits(5.0, 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveRealDoubleThenInternalsAreCorrect() {
        setAndAssertBits(3.141592653589793, 0x6487ED51, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntegerDoubleThenInternalsAreCorrect() {
        setAndAssertBits(-1.0, 0x80000000, -31, mDecimal);
        setAndAssertBits(-2.0, 0x80000000, -30, mDecimal);
        setAndAssertBits(-3.0, 0xA0000000, -29, mDecimal);
        setAndAssertBits(-4.0, 0x80000000, -29, mDecimal);
        setAndAssertBits(-5.0, 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeRealDoubleThenInternalsAreCorrect() {
        setAndAssertBits(-3.141592653589793, 0x9B7812AF, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromDoubleMaxThenInternalsAreCorrect() {
        setAndAssertBits(Double.MAX_VALUE, 0x7FFFFFFF, 993, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveSubnormalThenInternalsAreCorrect() {
        setAndAssertBits(SUBNORMAL, 0x40000000, -1025, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeSubnormalThenInternalsAreCorrect() {
        setAndAssertBits(-SUBNORMAL, 0x80000000, -1025, mDecimal);
    }

    @Test
    public void whenZeroThenToDoubleIsCorrect() {
        assertExactly(0.0, mDecimal.set(0).doubleValue());
    }

    @Test
    public void whenPositiveIntegerThenToDoubleIsCorrect() {
        assertExactly(1.0, mDecimal.set(1).doubleValue());
        assertExactly(2.0, mDecimal.set(2).doubleValue());
        assertExactly(3.0, mDecimal.set(3).doubleValue());
        assertExactly(4.0, mDecimal.set(4).doubleValue());
        assertExactly(5.0, mDecimal.set(5).doubleValue());
    }

    @Test
    public void whenNegativeIntegerThenToDoubleIsCorrect() {
        assertExactly(-1.0, mDecimal.set(-1).doubleValue());
        assertExactly(-2.0, mDecimal.set(-2).doubleValue());
        assertExactly(-3.0, mDecimal.set(-3).doubleValue());
        assertExactly(-4.0, mDecimal.set(-4).doubleValue());
        assertExactly(-5.0, mDecimal.set(-5).doubleValue());
    }

    @Test
    public void whenPiThenToDoubleIsCorrect() {
        assertApproximately(Math.PI, mDecimal.set(Math.PI).doubleValue(), DOUBLE_ACCURACY);
    }

    @Test
    public void whenDoubleMaxThenToDoubleIsCorrect() {
        assertApproximately(Double.MAX_VALUE, mDecimal.set(Double.MAX_VALUE).doubleValue(), DOUBLE_ACCURACY);
    }

    @Test
    public void whenBiggerThanMaxDoubleThenToDoubleReturnsInf() {
        mDecimal.set(Double.MAX_VALUE).add(MAX_OFFSET);
        assertExactly(Double.POSITIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenNegativeDoubleMaxThenToDoubleIsCorrect() {
        assertApproximately(-Double.MAX_VALUE, mDecimal.set(-Double.MAX_VALUE).doubleValue(), DOUBLE_ACCURACY);
    }

    @Test
    public void whenSmallerThanNegativeMaxDoubleThenToDoubleReturnsNegInf() {
        mDecimal.set(-Double.MAX_VALUE).add(-MAX_OFFSET);
        assertExactly(Double.NEGATIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenSubNormalThenToDoubleStillWorks() {
        assertApproximately(SUBNORMAL, mDecimal.set(SUBNORMAL).doubleValue(), DOUBLE_ACCURACY);
    }

    @Test
    public void whenSubSubNormalThenToDoubleReturnsZero() {
        mDecimal.set(Double.MIN_VALUE).divide(8);
        assertExactly(0.0, mDecimal.doubleValue());
    }
}