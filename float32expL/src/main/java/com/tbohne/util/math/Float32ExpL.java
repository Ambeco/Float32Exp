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
 * Note that this is _wildly_ different than the IEEE 754 format binary128, which is 113 and 15
 * bits, and also the IEEE 754 format float64Exp, which is 110 and 17 bits.
 *
 * This somewhat mirrors BigDecimal, except since it's mutating, return types are not directly
 * assignable.
 * Also, it doesn't accept a MathContext, everything is round-to-zero. Some functionality that's
 * tied to the BigDecimal internals are elided, like scale() and precision().
 *
 * This class is very small, very fast, and so, as a consequence, very inaccurate. exponents and
 * getPowerOf10 are wildly inaccurate, and as a result, .toString() and the String constructors are
 * also wildly inaccurate. They don't even come close to round-tripping. .toString() is fast and
 * simple, and therefore comes with no guarantees about being shortest length, or even not having
 * trailing zeroes. Trailing zeroes should be rare, but theoretically can occur, due to truncation.
 *
 * Virtually any menthod may throw a ArithmeticException, if the number is bigger or smaller than
 * this type can support. This should be rare unless playing with exponents.
 *
 * The return types are Float32ExpLChainedExpression, allowing you to chain operations, but not
 * directly reassign to a Float32ExpL. This prevents accidental mutation.
 *
 * Compiler error:
 *      Float32ExpL thing(Float32ExpL left, Float32ExpL right) {
 *          Float32ExpL result = left.multiply(right).add(3);
 *          return result;
*       }
 * Correct:
 *      void thing(Float32ExpL left, Float32ExpL right) {
 *          Float32ExpL result = new Float32ExpL(left);
 *          result.multiply(right).add(3);
 *          return result;
 *      }
 *
 * TODO: There seems to be more methods in BigDecimal to emulate
 */
public class Float32ExpL extends Number implements Float32ExpLChainedExpression, Float32ExpLHelpers.RemainderSettable {
    private long parts;

    public Float32ExpL() {parts = Float32ExpLHelpers.ZERO_PATTERN;}
    public Float32ExpL(char[] in, int offset, int len) {parts = cast(in, offset, len);}
    public Float32ExpL(char[] in) {parts = cast(in, 0, in.length);}
    public Float32ExpL(String val) {parts = cast(val.toCharArray(), 0, val.length());}
    public Float32ExpL(double val) {parts = getDoubleParts(val);}
    public Float32ExpL(int val) {parts = getLongParts(val);}
    public Float32ExpL(long val) {parts = getLongParts(val);}
    public Float32ExpL(BigDecimal val) {parts = cast(val);}
    public Float32ExpL(BigInteger val) {parts = cast(val);}
    public Float32ExpL(IFloat32ExpL val) {parts = val.getParts();}
    public Float32ExpL(long newParts, boolean ISolmnlySwearIKnowWhatImDoing) {parts = newParts;}
    public Float32ExpL(int significand, int exponent) {parts = getNormalizedParts(significand, exponent);}

    public static Float32ExpL getPowerOf10(int exponent) {
        return new Float32ExpL(Float32ExpLHelpers.getPowerOf10Parts(exponent), true);
    }

    public ImmutableFloat32ExpL toImmutable() {return new ImmutableFloat32ExpL(parts, true);}
    public long getParts() {return parts;}
    public int significand() {return (int)(parts >> INT_MAX_BITS);}
    public int exponent() {return (int)parts;}

    public Float32ExpLChainedExpression set(int val) {parts = getLongParts(val); return this;}
    public Float32ExpLChainedExpression set(long val) {parts = getLongParts(val); return this;}
    public Float32ExpLChainedExpression set(char[] in) {parts = cast(in, 0, in.length); return this;}
    public Float32ExpLChainedExpression set(String val) {parts = cast(val.toCharArray(), 0, val.length()); return this;}
    public Float32ExpLChainedExpression set(double val) {parts = getDoubleParts(val); return this;}
    public Float32ExpLChainedExpression set(char[] in, int offset, int len) {parts = cast(in,offset,len); return this;}
    public Float32ExpLChainedExpression set(BigDecimal val) {parts = cast(val); return this;}
    public Float32ExpLChainedExpression set(BigInteger val) {parts = cast(val); return this;}
    public Float32ExpLChainedExpression set(IFloat32ExpL val) {parts = val.getParts(); return this;}

    public Float32ExpLChainedExpression add(IFloat32ExpL val) {parts = Float32ExpLHelpers.add(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression add(long val) {parts = Float32ExpLHelpers.add(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression add(double val) {parts = Float32ExpLHelpers.add(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression subtract(IFloat32ExpL val) {parts = Float32ExpLHelpers.subtract(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression subtract(long val) {parts = Float32ExpLHelpers.subtract(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression subtract(double val) {parts = Float32ExpLHelpers.subtract(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression multiply(IFloat32ExpL val) {parts = Float32ExpLHelpers.multiply(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression multiply(long val) {parts = Float32ExpLHelpers.multiply(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression multiply(double val) {parts = Float32ExpLHelpers.multiply(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression divide(IFloat32ExpL val) {parts = Float32ExpLHelpers.divide(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression divide(long val) {parts = Float32ExpLHelpers.divide(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression divide(double val) {parts = Float32ExpLHelpers.divide(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, IFloat32ExpL div)
    {parts =  Float32ExpLHelpers.muldiv(parts, mul.getParts(), div.getParts()); return this;}
    public Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, long div)
    {parts =  Float32ExpLHelpers.muldiv(parts, mul.getParts(), getLongParts(div)); return this;}
    public Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, double div)
    {parts =  Float32ExpLHelpers.muldiv(parts, mul.getParts(), getDoubleParts(div)); return this;}
    public Float32ExpLChainedExpression muldiv(long mul, IFloat32ExpL div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getLongParts(mul), div.getParts()); return this;}
    public Float32ExpLChainedExpression muldiv(long mul, long div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getLongParts(mul), getLongParts(div)); return this;}
    public Float32ExpLChainedExpression muldiv(long mul, double div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getLongParts(mul), getDoubleParts(div)); return this;}
    public Float32ExpLChainedExpression muldiv(double mul, IFloat32ExpL div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getDoubleParts(mul), div.getParts()); return this;}
    public Float32ExpLChainedExpression muldiv(double mul, long div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getDoubleParts(mul), getLongParts(div)); return this;}
    public Float32ExpLChainedExpression muldiv(double mul, double div)
    {parts =  Float32ExpLHelpers.muldiv(parts, getDoubleParts(mul), getDoubleParts(div)); return this;}

    public Float32ExpLChainedExpression divideToIntegralValue(IFloat32ExpL val)
    {parts = Float32ExpLHelpers.divideToIntegralValue(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression divideToIntegralValue(long val)
    {parts = Float32ExpLHelpers.divideToIntegralValue(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression divideToIntegralValue(double val)
    {parts = Float32ExpLHelpers.divideToIntegralValue(parts, getDoubleParts(val)); return this;}
    
    public Float32ExpLChainedExpression remainder(IFloat32ExpL val) {parts = Float32ExpLHelpers.remainder(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression remainder(long val) {parts = Float32ExpLHelpers.remainder(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression remainder(double val) {parts = Float32ExpLHelpers.remainder(parts, getDoubleParts(val)); return this;}

    public void setRemainder(long remainder) {parts = remainder;}
    public Float32ExpLChainedExpression divideAndRemainder(IFloat32ExpL val, Float32ExpL outRemainder)
    {parts = Float32ExpLHelpers.divideAndRemainder(parts, val.getParts(), outRemainder); return this;}
    public Float32ExpLChainedExpression divideAndRemainder(long val, Float32ExpL outRemainder)
    {parts = Float32ExpLHelpers.divideAndRemainder(parts, getLongParts(val), outRemainder); return this;}
    public Float32ExpLChainedExpression divideAndRemainder(double val, Float32ExpL outRemainder)
    {parts = Float32ExpLHelpers.divideAndRemainder(parts, getDoubleParts(val), outRemainder); return this;}

    public Float32ExpLChainedExpression floor() {parts = Float32ExpLHelpers.floor(parts); return this;}
    public Float32ExpLChainedExpression floor(IFloat32ExpL val) {parts = Float32ExpLHelpers.floor(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression floor(long val) {parts = Float32ExpLHelpers.floor(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression floor(double val) {parts = Float32ExpLHelpers.floor(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression round() {parts = Float32ExpLHelpers.round(parts); return this;}
    public Float32ExpLChainedExpression round(IFloat32ExpL val) {parts = Float32ExpLHelpers.round(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression round(long val) {parts = Float32ExpLHelpers.round(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression round(double val) {parts = Float32ExpLHelpers.round(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression pow(IFloat32ExpL val) {parts = Float32ExpLHelpers.pow(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression pow(long val) {parts = Float32ExpLHelpers.pow(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression pow(double val) {parts = Float32ExpLHelpers.pow(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression pow2() {parts = Float32ExpLHelpers.pow2(parts); return this;}
    public Float32ExpLChainedExpression log2i() {parts = Float32ExpLHelpers.log2i(parts); return this;}
    public Float32ExpLChainedExpression log2() {parts = Float32ExpLHelpers.log2(parts); return this;}

    public Float32ExpLChainedExpression abs() {parts = Float32ExpLHelpers.abs(parts); return this;}
    public Float32ExpLChainedExpression negate() {parts = Float32ExpLHelpers.negate(parts); return this;}
    public Float32ExpLChainedExpression plus() { return this;}

    public Float32ExpLChainedExpression shiftLeft(int bits) {parts = Float32ExpLHelpers.shiftLeft(parts, bits); return this;}
    public Float32ExpLChainedExpression shiftRight(int bits) {parts = Float32ExpLHelpers.shiftRight(parts, bits); return this;}

    public Float32ExpLChainedExpression min(IFloat32ExpL val) {parts = Float32ExpLHelpers.min(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression min(long val) {parts = Float32ExpLHelpers.min(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression min(double val) {parts = Float32ExpLHelpers.min(parts, getDoubleParts(val)); return this;}

    public Float32ExpLChainedExpression max(IFloat32ExpL val) {parts = Float32ExpLHelpers.max(parts, val.getParts()); return this;}
    public Float32ExpLChainedExpression max(long val) {parts = Float32ExpLHelpers.max(parts, getLongParts(val)); return this;}
    public Float32ExpLChainedExpression max(double val) {parts = Float32ExpLHelpers.max(parts, getDoubleParts(val)); return this;}

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
