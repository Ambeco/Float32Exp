package com.tbohne.util.math;

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
public class ImmutableFloat64Exp extends  Float64ExpSharedBase {
    public static final ImmutableFloat64Exp NEGATIVE_ONE = new ImmutableFloat64Exp(-1);
    public static final ImmutableFloat64Exp ZERO = new ImmutableFloat64Exp(0);
    public static final ImmutableFloat64Exp ONE = new ImmutableFloat64Exp(1);
    public static final ImmutableFloat64Exp TWO = new ImmutableFloat64Exp(2);
    public static final ImmutableFloat64Exp TEN = new ImmutableFloat64Exp(10);
    public static final ImmutableFloat64Exp HUNDRED = new ImmutableFloat64Exp(100);
    public static final ImmutableFloat64Exp THOUSAND = new ImmutableFloat64Exp(1000);

    public static final ImmutableFloat64Exp NANO = new ImmutableFloat64Exp(0.000000001);
    public static final ImmutableFloat64Exp MICRO = new ImmutableFloat64Exp(0.000001);
    public static final ImmutableFloat64Exp MILI = new ImmutableFloat64Exp(0.001);
    public static final ImmutableFloat64Exp KILO = THOUSAND;
    public static final ImmutableFloat64Exp MEGA = new ImmutableFloat64Exp(1000000);
    public static final ImmutableFloat64Exp GIGA = new ImmutableFloat64Exp(1000000000);
    public static final ImmutableFloat64Exp TERA = new ImmutableFloat64Exp(1000000000000L);

    public static final ImmutableFloat64Exp PI = new ImmutableFloat64Exp(3.14159265359);
    public static final ImmutableFloat64Exp TAU = new ImmutableFloat64Exp(6.28318530718);
    public static final ImmutableFloat64Exp E = new ImmutableFloat64Exp(2.71828182846);
    public static final ImmutableFloat64Exp GOLDEN = new ImmutableFloat64Exp(1.618033988749);

    public static final ImmutableFloat64Exp SQRT_2 = new ImmutableFloat64Exp(1.41421356237);

    public static final ImmutableFloat64Exp LOG_2 = new ImmutableFloat64Exp(0.30102999566);
    public static final ImmutableFloat64Exp LG_10 = new ImmutableFloat64Exp(3.32192809489);

    public ImmutableFloat64Exp(char[] in, int offset, int len) {set(in, offset, len);}
    public ImmutableFloat64Exp(char[] in) {set(in, 0,in.length);}
    public ImmutableFloat64Exp(String val) {set(val.toCharArray(), 0,val.length());}
    public ImmutableFloat64Exp(double val) {set(doubleToSignificand(val), doubleToExponent(val));}
    public ImmutableFloat64Exp(int val) {setNormalized(val, 0);}
    public ImmutableFloat64Exp(long val) {setNormalized(val, 0);}
    public ImmutableFloat64Exp(BigDecimal val) {set(val);}
    public ImmutableFloat64Exp(BigInteger val) {set(val);}
    public ImmutableFloat64Exp(IFloat64Exp val) {set(val.significand(), val.exponent());}
    public ImmutableFloat64Exp(int significand, int exponent) {set(significand, exponent);}

    public static ImmutableFloat64Exp getPowerOf10(int exponent) {
        long pow10Parts = Float64ExpSharedBase.getPowerOf10Parts(exponent);
        return new ImmutableFloat64Exp((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    @Override
    public ImmutableFloat64Exp toImmutable() {return this;}
}
