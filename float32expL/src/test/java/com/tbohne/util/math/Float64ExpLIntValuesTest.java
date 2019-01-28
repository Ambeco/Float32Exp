package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpLIntValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32ExpL mDecimal = new Float32ExpL();

    @Test
    public void whenConstructedFromZeroThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(0, 0, Float64ExpLTestUtils.ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(1, 0x40000000, -30, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(2, 0x40000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(3, 0x60000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(4, 0x40000000, -28, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(5, 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveLongThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(1099511627776L, 0x40000000, 10, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(1649267441664L, 0x60000000, 10, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntThenInternalsAreCorrect() {
        Float64ExpLTestUtils.setAndAssertBits(-1, 0x80000000, -31, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-2, 0x80000000, -30, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-3, 0xA0000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-4, 0x80000000, -29, mDecimal);
        Float64ExpLTestUtils.setAndAssertBits(-5, 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenZeroThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(0, mDecimal.set(0).intValue());
    }

    @Test
    public void whenIntegerThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(5, mDecimal.set(5).intValue());
    }

    @Test
    public void whenBiggerThanMaxIntThenToIntThrows() {
        mDecimal.set(Integer.MAX_VALUE).add(1);
        exception.expect(IllegalArgumentException.class);
        mDecimal.intValue();
    }

    @Test
    public void whenNegativeIntegerThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(-5, mDecimal.set(-5).intValue());
    }

    @Test
    public void whenSmallerThanMinIntThenToIntThrows() {
        mDecimal.set(Integer.MIN_VALUE).subtract(1);
        exception.expect(IllegalArgumentException.class);
        mDecimal.intValue();
    }

    @Test
    public void whenNonIntegerThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(5, mDecimal.set(5.5).intValue());
    }

    @Test
    public void whenMaxIntThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(Integer.MAX_VALUE, mDecimal.set(Integer.MAX_VALUE).intValue());
    }

    @Test
    public void whenMinIntThenToIntIsCorrect() {
        Float64ExpLTestUtils.assertExactly(Integer.MIN_VALUE, mDecimal.set(Integer.MIN_VALUE).intValue());
    }
}