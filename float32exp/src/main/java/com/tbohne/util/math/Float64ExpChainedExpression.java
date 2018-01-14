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
 * This mostly mirrors BigDecimal, except since it's mutating, most return types are void.
 * Also, it doesn't accept a MathContext, everything is fixed. Some functionality that's tied to
 * the BigDecimal internals are elided, like scale() and precision().
 *
 * Yes, it is irritating that the return types are void, but this prevents accidental mutation.
 * Compiler error:
 *      void thing(IFloat64Exp left, IFloat64Exp right) {
 *          return left.multiply(right);
*       }
 * Correct:
 *      void thing(IFloat64Exp left, IFloat64Exp right) {
 *          IFloat64Exp result = new IFloat64Exp(left);
 *          result.multiply(right);
 *          return result;
 *      }
 *
 */
public interface Float64ExpChainedExpression extends IFloat64Exp {
    Float64ExpChainedExpression add(IFloat64Exp val);
    Float64ExpChainedExpression add(long val);
    Float64ExpChainedExpression add(double val);

    Float64ExpChainedExpression subtract(IFloat64Exp val);
    Float64ExpChainedExpression subtract(long val);
    Float64ExpChainedExpression subtract(double val);

    Float64ExpChainedExpression multiply(IFloat64Exp val);
    Float64ExpChainedExpression multiply(long val);
    Float64ExpChainedExpression multiply(double val);

    Float64ExpChainedExpression divide(IFloat64Exp val);
    Float64ExpChainedExpression divide(long val);
    Float64ExpChainedExpression divide(double val);

    Float64ExpChainedExpression muldiv(IFloat64Exp mul, IFloat64Exp div);
    Float64ExpChainedExpression muldiv(IFloat64Exp mul, long div);
    Float64ExpChainedExpression muldiv(IFloat64Exp mul, double div);
    Float64ExpChainedExpression muldiv(long mul, IFloat64Exp div);
    Float64ExpChainedExpression muldiv(double mul, IFloat64Exp div);
    Float64ExpChainedExpression muldiv(long mul, long div);

    Float64ExpChainedExpression divideToIntegralValue(IFloat64Exp val);
    Float64ExpChainedExpression divideToIntegralValue(long val);
    Float64ExpChainedExpression divideToIntegralValue(double val);

    Float64ExpChainedExpression remainder(IFloat64Exp val);
    Float64ExpChainedExpression remainder(long val);
    Float64ExpChainedExpression remainder(double val);

    Float64ExpChainedExpression divideAndRemainder(IFloat64Exp val, Float64Exp outRemainder);
    Float64ExpChainedExpression divideAndRemainder(long val, Float64Exp outRemainder);
    Float64ExpChainedExpression divideAndRemainder(double val, Float64Exp outRemainder);

    Float64ExpChainedExpression pow(IFloat64Exp val);
    Float64ExpChainedExpression pow(long val);
    Float64ExpChainedExpression pow(double val);

    Float64ExpChainedExpression log2();
    Float64ExpChainedExpression log2i(); //only keeps the integer part
    Float64ExpChainedExpression pow2(); //inverse of log2. DOES NOT SQUARE.

    Float64ExpChainedExpression abs();
    Float64ExpChainedExpression negate();
    Float64ExpChainedExpression plus(); //does nothing
    int signum();
    boolean isZero();

    Float64ExpChainedExpression round(IFloat64Exp val);
    Float64ExpChainedExpression round(long val);
    Float64ExpChainedExpression round(double val);

    Float64ExpChainedExpression shiftLeft(int val);
    Float64ExpChainedExpression shiftRight(int val);

    boolean equals(Object object);
    boolean equals(IFloat64Exp val);
    boolean equals(long val);
    boolean equals(double val);

    boolean approximately(IFloat64Exp val, int bitsSimilarCount);
    boolean approximately(long val, int bitsSimilarCount);
    boolean approximately(double val, int bitsSimilarCount);

    int compareTo(IFloat64Exp val);
    int compareTo(long val);
    int compareTo(double val);

    boolean lessThan(IFloat64Exp val);
    boolean lessThan(long val);
    boolean lessThan(double val);

    boolean lessOrEquals(IFloat64Exp val);
    boolean lessOrEquals(long val);
    boolean lessOrEquals(double val);

    boolean greaterOrEquals(IFloat64Exp val);
    boolean greaterOrEquals(long val);
    boolean greaterOrEquals(double val);

    boolean greaterThan(IFloat64Exp val);
    boolean greaterThan(long val);
    boolean greaterThan(double val);

    Float64ExpChainedExpression min(IFloat64Exp val);
    Float64ExpChainedExpression min(long val);
    Float64ExpChainedExpression min(double val);

    Float64ExpChainedExpression max(IFloat64Exp val);
    Float64ExpChainedExpression max(long val);
    Float64ExpChainedExpression max(double val);

    String toString();
    StringBuilder toString(StringBuilder sb);
    String toEngineeringString();
    StringBuilder toEngineeringString(StringBuilder sb);
    StringBuilder toString(StringBuilder sb, int base, int digits, int exponentMultiple,
                           ExponentToStringInterface exponentToString);

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableFloat64Exp toImmutable();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();
}
