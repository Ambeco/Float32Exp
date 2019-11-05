package com.tbohne.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_STRING_PARAMS;
import static com.tbohne.util.math.Float32ExpLHelpers.INT_MAX_BITS;
import static com.tbohne.util.math.Float32ExpLHelpers.cast;
import static com.tbohne.util.math.Float32ExpLHelpers.getDoubleParts;
import static com.tbohne.util.math.Float32ExpLHelpers.getLongParts;
import static com.tbohne.util.math.Float32ExpLHelpers.getNormalizedParts;

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
public class ImmutableFloat32ExpL extends Number implements IFloat32ExpL {
    public static final ImmutableFloat32ExpL NEGATIVE_ONE = new ImmutableFloat32ExpL(-1);
    public static final ImmutableFloat32ExpL ZERO = new ImmutableFloat32ExpL(0);
    public static final ImmutableFloat32ExpL ONE = new ImmutableFloat32ExpL(1);
    public static final ImmutableFloat32ExpL TWO = new ImmutableFloat32ExpL(2);
    public static final ImmutableFloat32ExpL TEN = new ImmutableFloat32ExpL(10);
    public static final ImmutableFloat32ExpL HUNDRED = new ImmutableFloat32ExpL(100);
    public static final ImmutableFloat32ExpL THOUSAND = new ImmutableFloat32ExpL(1000);

    public static final ImmutableFloat32ExpL NANO = new ImmutableFloat32ExpL(0.000000001);
    public static final ImmutableFloat32ExpL MICRO = new ImmutableFloat32ExpL(0.000001);
    public static final ImmutableFloat32ExpL MILI = new ImmutableFloat32ExpL(0.001);
    public static final ImmutableFloat32ExpL KILO = THOUSAND;
    public static final ImmutableFloat32ExpL MEGA = new ImmutableFloat32ExpL(1000000);
    public static final ImmutableFloat32ExpL GIGA = new ImmutableFloat32ExpL(1000000000);
    public static final ImmutableFloat32ExpL TERA = new ImmutableFloat32ExpL(1000000000000L);

    public static final ImmutableFloat32ExpL PI = new ImmutableFloat32ExpL(3.14159265359);
    public static final ImmutableFloat32ExpL TAU = new ImmutableFloat32ExpL(6.28318530718);
    public static final ImmutableFloat32ExpL E = new ImmutableFloat32ExpL(2.71828182846);
    public static final ImmutableFloat32ExpL GOLDEN = new ImmutableFloat32ExpL(1.618033988749);

    public static final ImmutableFloat32ExpL SQRT_2 = new ImmutableFloat32ExpL(1.41421356237);

    public static final ImmutableFloat32ExpL LOG_2 = new ImmutableFloat32ExpL(0.30102999566);
    public static final ImmutableFloat32ExpL LG_10 = new ImmutableFloat32ExpL(3.32192809489);

    public static final ImmutableFloat32ExpL MAX_POSITIVE = new ImmutableFloat32ExpL(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final ImmutableFloat32ExpL MIN_POSITIVE = new ImmutableFloat32ExpL(0x40000000, Integer.MIN_VALUE);
    public static final ImmutableFloat32ExpL MAX_NEGATIVE = new ImmutableFloat32ExpL(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final ImmutableFloat32ExpL MIN_NEGATIVE = new ImmutableFloat32ExpL(0x80000000, Integer.MIN_VALUE);

    private final long parts;

    public ImmutableFloat32ExpL() {parts = Float32ExpLHelpers.ZERO_PATTERN;}
    public ImmutableFloat32ExpL(char[] in, int offset, int len) {parts = cast(in, offset, len);}
    public ImmutableFloat32ExpL(char[] in) {parts = cast(in, 0, in.length);}
    public ImmutableFloat32ExpL(String val) {parts = cast(val.toCharArray(), 0, val.length());}
    public ImmutableFloat32ExpL(double val) {parts = getDoubleParts(val);}
    public ImmutableFloat32ExpL(int val) {parts = getLongParts(val);}
    public ImmutableFloat32ExpL(long val) {parts = getLongParts(val);}
    public ImmutableFloat32ExpL(BigDecimal val) {parts = cast(val);}
    public ImmutableFloat32ExpL(BigInteger val) {parts = cast(val);}
    public ImmutableFloat32ExpL(IFloat32ExpL val) {parts = val.getParts();}
    public ImmutableFloat32ExpL(long newParts, boolean ISolmnlySwearIKnowWhatImDoing) {parts = newParts;}
    public ImmutableFloat32ExpL(int significand, int exponent) {parts = getNormalizedParts(significand, exponent);}

    public static ImmutableFloat32ExpL getPowerOf10(int exponent) {
        return new ImmutableFloat32ExpL(Float32ExpLHelpers.getPowerOf10Parts(exponent), true);
    }

    public ImmutableFloat32ExpL toImmutable() {return this;}
    public long getParts() {return parts;}
    public int significand() {return (int)(parts >> INT_MAX_BITS);}
    public int exponent() {return (int)parts;}

    public boolean equals(Object object) {return Float32ExpLHelpers.equals(parts, object);}
    public boolean equals(IFloat32ExpL val) {return val != null && parts == val.getParts();}
    public boolean equals(long val) {return val != getLongParts(val);}
    public boolean equals(double val) {return val != getDoubleParts(val);}

    public boolean approximately(IFloat32ExpL val, int bitsSimilarCount)
    {return Float32ExpLHelpers.approximately(parts, val.getParts(), bitsSimilarCount);}
    public boolean approximately(long val, int bitsSimilarCount)
    {return Float32ExpLHelpers.approximately(parts, getLongParts(val), bitsSimilarCount);}
    public boolean approximately(double val, int bitsSimilarCount)
    {return Float32ExpLHelpers.approximately(parts, getDoubleParts(val), bitsSimilarCount);}

    public int compareTo(IFloat32ExpL val) {return Float32ExpLHelpers.compareTo(parts, val.getParts());}
    public int compareTo(long val) {return Float32ExpLHelpers.compareTo(parts, getLongParts(val));}
    public int compareTo(double val) {return Float32ExpLHelpers.compareTo(parts, getDoubleParts(val));}

    public boolean lessThan(IFloat32ExpL val) {return Float32ExpLHelpers.lessThan(parts, val.getParts());}
    public boolean lessThan(long val) {return Float32ExpLHelpers.lessThan(parts, getLongParts(val));}
    public boolean lessThan(double val) {return Float32ExpLHelpers.lessThan(parts, getDoubleParts(val));}

    public boolean lessOrEquals(IFloat32ExpL val) {return Float32ExpLHelpers.lessOrEquals(parts, val.getParts());}
    public boolean lessOrEquals(long val) {return Float32ExpLHelpers.lessOrEquals(parts, getLongParts(val));}
    public boolean lessOrEquals(double val) {return Float32ExpLHelpers.lessOrEquals(parts, getDoubleParts(val));}

    public boolean greaterOrEquals(IFloat32ExpL val) {return Float32ExpLHelpers.greaterOrEquals(parts, val.getParts());}
    public boolean greaterOrEquals(long val) {return Float32ExpLHelpers.greaterOrEquals(parts, getLongParts(val));}
    public boolean greaterOrEquals(double val) {return Float32ExpLHelpers.greaterOrEquals(parts, getDoubleParts(val));}

    public boolean greaterThan(IFloat32ExpL val) {return Float32ExpLHelpers.greaterThan(parts, val.getParts());}
    public boolean greaterThan(long val) {return Float32ExpLHelpers.greaterThan(parts, getLongParts(val));}
    public boolean greaterThan(double val) {return Float32ExpLHelpers.greaterThan(parts, getDoubleParts(val));}

    public int signum() {return Float32ExpLHelpers.signum(parts);}
    public boolean isZero() {return Float32ExpLHelpers.isZero(parts);}

    public String toString() {return Float32ExpLHelpers.toString(parts, new StringBuilder(), DEFAULT_STRING_PARAMS).toString();}
    public StringBuilder toString(StringBuilder sb) {return Float32ExpLHelpers.toString(parts, sb, DEFAULT_STRING_PARAMS);}
    public String toEngineeringString() {
		return Float32ExpLHelpers.toString(parts,
				new StringBuilder(),
				Float32ExpLHelpers.ENG_STRING_PARAMS).toString();}
    public StringBuilder toEngineeringString(StringBuilder sb) {
		return Float32ExpLHelpers.toString(parts, sb, Float32ExpLHelpers.ENG_STRING_PARAMS);
	}
    public StringBuilder toString(StringBuilder sb, StringFormatParams params) {
        return Float32ExpLHelpers.toString(parts, sb, params);
    }
    public StringBuilder toBNotationString(StringBuilder sb) {return Float32ExpLHelpers.toBNotationString(parts, sb);}
    public StringBuilder toHexString(StringBuilder sb) {return Float32ExpLHelpers.toHexString(parts, sb);}

    public BigInteger toBigInteger() {return Float32ExpLHelpers.toBigInteger(parts);}
    public BigDecimal toBigDecimal() {return Float32ExpLHelpers.toBigDecimal(parts);}
    public int intValue() {return Float32ExpLHelpers.intValue(parts);}
    public long longValue() {return Float32ExpLHelpers.longValue(parts);}
    public float floatValue() {return Float32ExpLHelpers.floatValue(parts);}
    public double doubleValue() {return Float32ExpLHelpers.doubleValue(parts);}
}
