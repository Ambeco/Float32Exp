package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpDoubleValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32Exp mDecimal = new Float32Exp();

    @Test
    public void whenConstructedFromZeroDoubleThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(0.0, 0, Float64ExpTestUtils.ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntegerDoubleThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(1.0, 0x40000000, -30, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(2.0, 0x40000000, -29, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(3.0, 0x60000000, -29, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(4.0, 0x40000000, -28, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(5.0, 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveRealDoubleThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(3.141592653589793, 0x6487ED51, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntegerDoubleThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(-1.0, 0x80000000, -31, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(-2.0, 0x80000000, -30, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(-3.0, 0xA0000000, -29, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(-4.0, 0x80000000, -29, mDecimal);
        Float64ExpTestUtils.setAndAssertBits(-5.0, 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeRealDoubleThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(-3.141592653589793, 0x9B7812AF, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromDoubleMaxThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(Double.MAX_VALUE, 0x7FFFFFFF, 993, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveSubnormalThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(Float64ExpTestUtils.SUBNORMAL, 0x40000000, -1055, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeSubnormalThenInternalsAreCorrect() {
        Float64ExpTestUtils.setAndAssertBits(-Float64ExpTestUtils.SUBNORMAL, 0x80000000, -1056, mDecimal);
    }

    @Test
    public void whenZeroThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertExactly(0.0, mDecimal.set(0).doubleValue());
    }

    @Test
    public void whenPositiveIntegerThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertExactly(1.0, mDecimal.set(1).doubleValue());
        Float64ExpTestUtils.assertExactly(2.0, mDecimal.set(2).doubleValue());
        Float64ExpTestUtils.assertExactly(3.0, mDecimal.set(3).doubleValue());
        Float64ExpTestUtils.assertExactly(4.0, mDecimal.set(4).doubleValue());
        Float64ExpTestUtils.assertExactly(5.0, mDecimal.set(5).doubleValue());
    }

    @Test
    public void whenNegativeIntegerThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertExactly(-1.0, mDecimal.set(-1).doubleValue());
        Float64ExpTestUtils.assertExactly(-2.0, mDecimal.set(-2).doubleValue());
        Float64ExpTestUtils.assertExactly(-3.0, mDecimal.set(-3).doubleValue());
        Float64ExpTestUtils.assertExactly(-4.0, mDecimal.set(-4).doubleValue());
        Float64ExpTestUtils.assertExactly(-5.0, mDecimal.set(-5).doubleValue());
    }

    @Test
    public void whenPiThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertApproximately(Math.PI, mDecimal.set(Math.PI).doubleValue(), Float64ExpTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenDoubleMaxThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertApproximately(Double.MAX_VALUE, mDecimal.set(Double.MAX_VALUE).doubleValue(), Float64ExpTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenBiggerThanMaxDoubleThenToDoubleReturnsInf() {
        mDecimal.set(Double.MAX_VALUE).add(Float64ExpTestUtils.MAX_OFFSET);
        Float64ExpTestUtils.assertExactly(Double.POSITIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenNegativeDoubleMaxThenToDoubleIsCorrect() {
        Float64ExpTestUtils.assertApproximately(-Double.MAX_VALUE, mDecimal.set(-Double.MAX_VALUE).doubleValue(), Float64ExpTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenSmallerThanNegativeMaxDoubleThenToDoubleReturnsNegInf() {
        mDecimal.set(-Double.MAX_VALUE).add(-Float64ExpTestUtils.MAX_OFFSET);
        Float64ExpTestUtils.assertExactly(Double.NEGATIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenSubNormalThenToDoubleStillWorks() {
        Float64ExpTestUtils.assertApproximately(Float64ExpTestUtils.SUBNORMAL, mDecimal.set(Float64ExpTestUtils.SUBNORMAL).doubleValue(), Float64ExpTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenSubSubNormalThenToDoubleReturnsZero() {
        mDecimal.set(Double.MIN_VALUE).divide(8);
        Float64ExpTestUtils.assertExactly(0.0, mDecimal.doubleValue());
    }
}