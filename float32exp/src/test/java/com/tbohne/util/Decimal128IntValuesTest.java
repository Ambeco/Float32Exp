package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.Decimal128TestUtils.ZERO_EXPONENT;
import static com.tbohne.util.Decimal128TestUtils.assertExactly;
import static com.tbohne.util.Decimal128TestUtils.setAndAssertBits;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128IntValuesTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

    @Test
    public void whenConstructedFromZeroThenInternalsAreCorrect() {
        setAndAssertBits(0, 0, ZERO_EXPONENT, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveIntThenInternalsAreCorrect() {
        setAndAssertBits(1, 0x40000000, -30, mDecimal);
        setAndAssertBits(2, 0x40000000, -29, mDecimal);
        setAndAssertBits(3, 0x60000000, -29, mDecimal);
        setAndAssertBits(4, 0x40000000, -28, mDecimal);
        setAndAssertBits(5, 0x50000000, -28, mDecimal);
    }

    @Test
    public void whenConstructedFromPositiveLongThenInternalsAreCorrect() {
        setAndAssertBits(1099511627776L, 0x40000000, 10, mDecimal);
        setAndAssertBits(1649267441664L, 0x60000000, 10, mDecimal);
    }

    @Test
    public void whenConstructedFromNegativeIntThenInternalsAreCorrect() {
        setAndAssertBits(-1, 0x80000000, -31, mDecimal);
        setAndAssertBits(-2, 0x80000000, -30, mDecimal);
        setAndAssertBits(-3, 0xA0000000, -29, mDecimal);
        setAndAssertBits(-4, 0x80000000, -29, mDecimal);
        setAndAssertBits(-5, 0xB0000000, -28, mDecimal);
    }

    @Test
    public void whenZeroThenToIntIsCorrect() {
        assertExactly(0, mDecimal.set(0).intValue());
    }

    @Test
    public void whenIntegerThenToIntIsCorrect() {
        assertExactly(5, mDecimal.set(5).intValue());
    }

    @Test
    public void whenBiggerThanMaxIntThenToIntThrows() {
        mDecimal.set(Integer.MAX_VALUE).add(1);
        exception.expect(IllegalArgumentException.class);
        mDecimal.intValue();
    }

    @Test
    public void whenNegativeIntegerThenToIntIsCorrect() {
        assertExactly(-5, mDecimal.set(-5).intValue());
    }

    @Test
    public void whenSmallerThanMinIntThenToIntThrows() {
        mDecimal.set(Integer.MIN_VALUE).subtract(1);
        exception.expect(IllegalArgumentException.class);
        mDecimal.intValue();
    }

    @Test
    public void whenNonIntegerThenToIntIsCorrect() {
        assertExactly(5, mDecimal.set(5.5).intValue());
    }

    @Test
    public void whenMaxIntThenToIntIsCorrect() {
        assertExactly(Integer.MAX_VALUE, mDecimal.set(Integer.MAX_VALUE).intValue());
    }

    @Test
    public void whenMinIntThenToIntIsCorrect() {
        assertExactly(Integer.MIN_VALUE, mDecimal.set(Integer.MIN_VALUE).intValue());
    }
}