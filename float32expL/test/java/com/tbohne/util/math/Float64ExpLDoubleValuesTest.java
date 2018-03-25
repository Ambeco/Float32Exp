package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpLDoubleValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32ExpL mDecimal = new Float32ExpL();

    @Test
    public void whenConstructedFromZeroDoubleThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(0.0, 0, Float64ExpLTestUtils.ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntegerDoubleThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(1.0, 0x40000000, -30, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(2.0, 0x40000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(3.0, 0x60000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(4.0, 0x40000000, -28, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(5.0, 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveRealDoubleThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(3.141592653589793238462643383279, 0x6487ED51, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntegerDoubleThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(-1.0, 0x80000000, -31, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-2.0, 0x80000000, -30, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-3.0, 0xA0000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-4.0, 0x80000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-5.0, 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeRealDoubleThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(-3.141592653589793238462643383279, 0x9B7812AF, -29, mDecimal);
    }

    @Test
    public void whenConstructedFromDoubleMaxThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(Double.MAX_VALUE, 0x7FFFFFFF, 993, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveSubnormalThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(Float64ExpLTestUtils.SUBNORMAL, 0x40000000, -1055, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeSubnormalThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(-Float64ExpLTestUtils.SUBNORMAL, 0x80000000, -1056, mDecimal);
    }

    @Test
    public void whenZeroThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertExactly(0.0, mDecimal.set(0).doubleValue());
    }

    @Test
    public void whenPositiveIntegerThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertExactly(1.0, mDecimal.set(1).doubleValue());
        Float64ExpLTestUtils.assertExactly(2.0, mDecimal.set(2).doubleValue());
        Float64ExpLTestUtils.assertExactly(3.0, mDecimal.set(3).doubleValue());
        Float64ExpLTestUtils.assertExactly(4.0, mDecimal.set(4).doubleValue());
        Float64ExpLTestUtils.assertExactly(5.0, mDecimal.set(5).doubleValue());
    }

    @Test
    public void whenNegativeIntegerThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertExactly(-1.0, mDecimal.set(-1).doubleValue());
        Float64ExpLTestUtils.assertExactly(-2.0, mDecimal.set(-2).doubleValue());
        Float64ExpLTestUtils.assertExactly(-3.0, mDecimal.set(-3).doubleValue());
        Float64ExpLTestUtils.assertExactly(-4.0, mDecimal.set(-4).doubleValue());
        Float64ExpLTestUtils.assertExactly(-5.0, mDecimal.set(-5).doubleValue());
    }

    @Test
    public void whenPiThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertApproximately(Math.PI, mDecimal.set(Math.PI).doubleValue(), Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenDoubleMaxThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertApproximately(Double.MAX_VALUE, mDecimal.set(Double.MAX_VALUE).doubleValue(), Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenBiggerThanMaxDoubleThenToDoubleReturnsInf() {
        mDecimal.set(Double.MAX_VALUE).add(Float64ExpLTestUtils.MAX_OFFSET);
        Float64ExpLTestUtils.assertExactly(Double.POSITIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenNegativeDoubleMaxThenToDoubleIsCorrect() {
        Float64ExpLTestUtils.assertApproximately(-Double.MAX_VALUE, mDecimal.set(-Double.MAX_VALUE).doubleValue(), Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenSmallerThanNegativeMaxDoubleThenToDoubleReturnsNegInf() {
        mDecimal.set(-Double.MAX_VALUE).add(-Float64ExpLTestUtils.MAX_OFFSET);
        Float64ExpLTestUtils.assertExactly(Double.NEGATIVE_INFINITY, mDecimal.doubleValue());
    }

    @Test
    public void whenSubNormalThenToDoubleStillWorks() {
        Float64ExpLTestUtils.assertApproximately(Float64ExpLTestUtils.SUBNORMAL, mDecimal.set(Float64ExpLTestUtils.SUBNORMAL).doubleValue(), Float64ExpLTestUtils.DOUBLE_ACCURACY);
    }

    @Test
    public void whenSubSubNormalThenToDoubleReturnsZero() {
        mDecimal.set(Double.MIN_VALUE).divide(8);
        Float64ExpLTestUtils.assertExactly(0.0, mDecimal.doubleValue());
    }
}