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
public class ImmutableFloat32Exp extends Float32ExpSharedBase {
    public static final ImmutableFloat32Exp NEGATIVE_ONE = new ImmutableFloat32Exp(-1);
    public static final ImmutableFloat32Exp ZERO = new ImmutableFloat32Exp(0);
    public static final ImmutableFloat32Exp ONE = new ImmutableFloat32Exp(1);
    public static final ImmutableFloat32Exp TWO = new ImmutableFloat32Exp(2);
    public static final ImmutableFloat32Exp TEN = new ImmutableFloat32Exp(10);
    public static final ImmutableFloat32Exp HUNDRED = new ImmutableFloat32Exp(100);
    public static final ImmutableFloat32Exp THOUSAND = new ImmutableFloat32Exp(1000);

    public static final ImmutableFloat32Exp NANO = new ImmutableFloat32Exp(0.000000001);
    public static final ImmutableFloat32Exp MICRO = new ImmutableFloat32Exp(0.000001);
    public static final ImmutableFloat32Exp MILI = new ImmutableFloat32Exp(0.001);
    public static final ImmutableFloat32Exp KILO = THOUSAND;
    public static final ImmutableFloat32Exp MEGA = new ImmutableFloat32Exp(1000000);
    public static final ImmutableFloat32Exp GIGA = new ImmutableFloat32Exp(1000000000);
    public static final ImmutableFloat32Exp TERA = new ImmutableFloat32Exp(1000000000000L);

    public static final ImmutableFloat32Exp PI = new ImmutableFloat32Exp(3.14159265359);
    public static final ImmutableFloat32Exp TAU = new ImmutableFloat32Exp(6.28318530718);
    public static final ImmutableFloat32Exp E = new ImmutableFloat32Exp(2.71828182846);
    public static final ImmutableFloat32Exp GOLDEN = new ImmutableFloat32Exp(1.618033988749);

    public static final ImmutableFloat32Exp SQRT_2 = new ImmutableFloat32Exp(1.41421356237);

    public static final ImmutableFloat32Exp LOG_2 = new ImmutableFloat32Exp(0.30102999566);
    public static final ImmutableFloat32Exp LG_10 = new ImmutableFloat32Exp(3.32192809489);

    public ImmutableFloat32Exp(char[] in, int offset, int len) {set(in, offset, len);}
    public ImmutableFloat32Exp(char[] in) {set(in, 0,in.length);}
    public ImmutableFloat32Exp(String val) {set(val.toCharArray(), 0,val.length());}
    public ImmutableFloat32Exp(double val) {super(getDoubleParts(val));}
    public ImmutableFloat32Exp(int val) {super(getLongParts(val));}
    public ImmutableFloat32Exp(long val) {super(getLongParts(val));}
    public ImmutableFloat32Exp(BigDecimal val) {set(val);}
    public ImmutableFloat32Exp(BigInteger val) {set(val);}
    public ImmutableFloat32Exp(IFloat32Exp val) {super(val.significand(), val.exponent());}
    public ImmutableFloat32Exp(int significand, int exponent) {super(significand, exponent);}

    public static ImmutableFloat32Exp getPowerOf10(int exponent) {
        long pow10Parts = Float32ExpSharedBase.getPowerOf10Parts(exponent);
        return new ImmutableFloat32Exp((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    @Override
    public ImmutableFloat32Exp toImmutable() {return this;}
}
