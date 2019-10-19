package com.tbohne.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A mutable fixed-precision signed decimal.
 * It works like double, but with a 32 binary significant digits, and a 32 bit base-2 exponent.
 * This equates to ~9.6 decimal digits for both parts.
 * Note that this is _wildly_ different than the IEEE 754 format, which is 110 and 17 bits. This
 * implementation is designed to be faster in software, and giving a far larger range, sacrificing
 * significant accuracy relative to IEEE 754.
 *
 * This somewhat mirrors BigDecimal, except since it's mutating, return types are not directly
 * assignable.
 * Also, it doesn't accept a MathContext, everything is fixed. Some functionality that's tied to
 * the BigDecimal internals are elided, like scale() and precision().
 */
public class ImmutableDecimal128 extends  Decimal128SharedBase {
    public static final ImmutableDecimal128 NEGATIVE_ONE = new ImmutableDecimal128(-1);
    public static final ImmutableDecimal128 ZERO = new ImmutableDecimal128(0);
    public static final ImmutableDecimal128 ONE = new ImmutableDecimal128(1);
    public static final ImmutableDecimal128 TWO = new ImmutableDecimal128(2);
    public static final ImmutableDecimal128 TEN = new ImmutableDecimal128(10);
    public static final ImmutableDecimal128 HUNDRED = new ImmutableDecimal128(100);
    public static final ImmutableDecimal128 THOUSAND = new ImmutableDecimal128(1000);

    public static final ImmutableDecimal128 NANO = new ImmutableDecimal128(0.000000001);
    public static final ImmutableDecimal128 MICRO = new ImmutableDecimal128(0.000001);
    public static final ImmutableDecimal128 MILI = new ImmutableDecimal128(0.001);
    public static final ImmutableDecimal128 KILO = THOUSAND;
    public static final ImmutableDecimal128 MEGA = new ImmutableDecimal128(1000000);
    public static final ImmutableDecimal128 GIGA = new ImmutableDecimal128(1000000000);
    public static final ImmutableDecimal128 TERA = new ImmutableDecimal128(1000000000000L);

    public static final ImmutableDecimal128 PI = new ImmutableDecimal128(3.14159265359);
    public static final ImmutableDecimal128 TAU = new ImmutableDecimal128(6.28318530718);
    public static final ImmutableDecimal128 E = new ImmutableDecimal128(2.71828182846);
    public static final ImmutableDecimal128 GOLDEN = new ImmutableDecimal128(1.618033988749);

    public static final ImmutableDecimal128 SQRT_2 = new ImmutableDecimal128(1.41421356237);

    public static final ImmutableDecimal128 LOG_2 = new ImmutableDecimal128(0.30102999566);
    public static final ImmutableDecimal128 LG_10 = new ImmutableDecimal128(3.32192809489);

    public ImmutableDecimal128(char[] in, int offset, int len) {set(in, offset, len);}
    public ImmutableDecimal128(char[] in) {set(in, 0,in.length);}
    public ImmutableDecimal128(String val) {set(val.toCharArray(), 0,val.length());}
    public ImmutableDecimal128(double val) {set(doubleToSignificand(val), doubleToExponent(val));}
    public ImmutableDecimal128(int val) {setNormalized(val, 0);}
    public ImmutableDecimal128(long val) {setNormalized(val, 0);}
    public ImmutableDecimal128(BigDecimal val) {set(val);}
    public ImmutableDecimal128(BigInteger val) {set(val);}
    public ImmutableDecimal128(IDecimal128 val) {set(val.significand(), val.exponent());}
    public ImmutableDecimal128(int significand, int exponent) {set(significand, exponent);}

    public static ImmutableDecimal128 getPowerOf10(int exponent) {
        long pow10Parts = Decimal128SharedBase.getPowerOf10Parts(exponent);
        return new ImmutableDecimal128((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    @Override
    public ImmutableDecimal128 toImmutable() {return this;}
}
