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
 * This mostly mirrors BigDecimal, except since it's mutating, most return types are void.
 * Also, it doesn't accept a MathContext, everything is fixed. Some functionality that's tied to
 * the BigDecimal internals are elided, like scale() and precision().
 *
 * Yes, it is irritating that the return types are void, but this prevents accidental mutation.
 * Compiler error:
 *      void thing(IDecimal128 left, IDecimal128 right) {
 *          return left.multiply(right);
*       }
 * Correct:
 *      void thing(IDecimal128 left, IDecimal128 right) {
 *          IDecimal128 result = new IDecimal128(left);
 *          result.multiply(right);
 *          return result;
 *      }
 *
 */
public interface Decimal128ChainedExpression extends IDecimal128 {
    Decimal128ChainedExpression add(IDecimal128 val);
    Decimal128ChainedExpression add(long val);
    Decimal128ChainedExpression add(double val);

    Decimal128ChainedExpression subtract(IDecimal128 val);
    Decimal128ChainedExpression subtract(long val);
    Decimal128ChainedExpression subtract(double val);

    Decimal128ChainedExpression multiply(IDecimal128 val);
    Decimal128ChainedExpression multiply(long val);
    Decimal128ChainedExpression multiply(double val);

    Decimal128ChainedExpression divide(IDecimal128 val);
    Decimal128ChainedExpression divide(long val);
    Decimal128ChainedExpression divide(double val);

    Decimal128ChainedExpression muldiv(IDecimal128 mul, IDecimal128 div);
    Decimal128ChainedExpression muldiv(IDecimal128 mul, long div);
    Decimal128ChainedExpression muldiv(IDecimal128 mul, double div);
    Decimal128ChainedExpression muldiv(long mul, IDecimal128 div);
    Decimal128ChainedExpression muldiv(double mul, IDecimal128 div);
    Decimal128ChainedExpression muldiv(long mul, long div);

    Decimal128ChainedExpression divideToIntegralValue(IDecimal128 val);
    Decimal128ChainedExpression divideToIntegralValue(long val);
    Decimal128ChainedExpression divideToIntegralValue(double val);

    Decimal128ChainedExpression remainder(IDecimal128 val);
    Decimal128ChainedExpression remainder(long val);
    Decimal128ChainedExpression remainder(double val);

    Decimal128ChainedExpression divideAndRemainder(IDecimal128 val, Decimal128 outRemainder);
    Decimal128ChainedExpression divideAndRemainder(long val, Decimal128 outRemainder);
    Decimal128ChainedExpression divideAndRemainder(double val, Decimal128 outRemainder);

    Decimal128ChainedExpression pow(IDecimal128 val);
    Decimal128ChainedExpression pow(long val);
    Decimal128ChainedExpression pow(double val);

    Decimal128ChainedExpression log2();
    Decimal128ChainedExpression log2i(); //only keeps the integer part
    Decimal128ChainedExpression pow2(); //inverse of log2. DOES NOT SQUARE.

    Decimal128ChainedExpression abs();
    Decimal128ChainedExpression negate();
    Decimal128ChainedExpression plus(); //does nothing
    int signum();
    boolean isZero();

    Decimal128ChainedExpression round(IDecimal128 val);
    Decimal128ChainedExpression round(long val);
    Decimal128ChainedExpression round(double val);

    Decimal128ChainedExpression shiftLeft(int val);
    Decimal128ChainedExpression shiftRight(int val);

    boolean equals(Object object);
    boolean equals(IDecimal128 val);
    boolean equals(long val);
    boolean equals(double val);

    boolean approximately(IDecimal128 val, int bitsSimilarCount);
    boolean approximately(long val, int bitsSimilarCount);
    boolean approximately(double val, int bitsSimilarCount);

    int compareTo(IDecimal128 val);
    int compareTo(long val);
    int compareTo(double val);

    boolean lessThan(IDecimal128 val);
    boolean lessThan(long val);
    boolean lessThan(double val);

    boolean lessOrEquals(IDecimal128 val);
    boolean lessOrEquals(long val);
    boolean lessOrEquals(double val);

    boolean greaterOrEquals(IDecimal128 val);
    boolean greaterOrEquals(long val);
    boolean greaterOrEquals(double val);

    boolean greaterThan(IDecimal128 val);
    boolean greaterThan(long val);
    boolean greaterThan(double val);

    Decimal128ChainedExpression min(IDecimal128 val);
    Decimal128ChainedExpression min(long val);
    Decimal128ChainedExpression min(double val);

    Decimal128ChainedExpression max(IDecimal128 val);
    Decimal128ChainedExpression max(long val);
    Decimal128ChainedExpression max(double val);

    String toString();
    StringBuilder toString(StringBuilder sb);
    String toEngineeringString();
    StringBuilder toEngineeringString(StringBuilder sb);
    StringBuilder toString(StringBuilder sb, int base, int digits, int exponentMultiple,
                           ExponentToStringInterface exponentToString);

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableDecimal128 toImmutable();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();
}
