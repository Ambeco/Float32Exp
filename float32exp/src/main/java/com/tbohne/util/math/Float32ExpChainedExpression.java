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
 *      void thing(IFloat32Exp left, IFloat32Exp right) {
 *          return left.multiply(right);
*       }
 * Correct:
 *      void thing(IFloat32Exp left, IFloat32Exp right) {
 *          IFloat32Exp result = new IFloat32Exp(left);
 *          result.multiply(right);
 *          return result;
 *      }
 *
 */
public interface Float32ExpChainedExpression extends IFloat32Exp {
    Float32ExpChainedExpression add(IFloat32Exp val);
    Float32ExpChainedExpression add(long val);
    Float32ExpChainedExpression add(double val);

    Float32ExpChainedExpression subtract(IFloat32Exp val);
    Float32ExpChainedExpression subtract(long val);
    Float32ExpChainedExpression subtract(double val);

    Float32ExpChainedExpression multiply(IFloat32Exp val);
    Float32ExpChainedExpression multiply(long val);
    Float32ExpChainedExpression multiply(double val);

    Float32ExpChainedExpression divide(IFloat32Exp val);
    Float32ExpChainedExpression divide(long val);
    Float32ExpChainedExpression divide(double val);

    Float32ExpChainedExpression muldiv(IFloat32Exp mul, IFloat32Exp div);
    Float32ExpChainedExpression muldiv(IFloat32Exp mul, long div);
    Float32ExpChainedExpression muldiv(IFloat32Exp mul, double div);
    Float32ExpChainedExpression muldiv(long mul, IFloat32Exp div);
    Float32ExpChainedExpression muldiv(double mul, IFloat32Exp div);
    Float32ExpChainedExpression muldiv(long mul, long div);

    Float32ExpChainedExpression divideToIntegralValue(IFloat32Exp val);
    Float32ExpChainedExpression divideToIntegralValue(long val);
    Float32ExpChainedExpression divideToIntegralValue(double val);

    Float32ExpChainedExpression remainder(IFloat32Exp val);
    Float32ExpChainedExpression remainder(long val);
    Float32ExpChainedExpression remainder(double val);

    Float32ExpChainedExpression divideAndRemainder(IFloat32Exp val, Float32Exp outRemainder);
    Float32ExpChainedExpression divideAndRemainder(long val, Float32Exp outRemainder);
    Float32ExpChainedExpression divideAndRemainder(double val, Float32Exp outRemainder);

    Float32ExpChainedExpression pow(IFloat32Exp val);
    Float32ExpChainedExpression pow(long val);
    Float32ExpChainedExpression pow(double val);

    Float32ExpChainedExpression log2();
    Float32ExpChainedExpression log2i(); //only keeps the integer part
    Float32ExpChainedExpression pow2(); //inverse of log2. DOES NOT SQUARE.

    Float32ExpChainedExpression abs();
    Float32ExpChainedExpression negate();
    Float32ExpChainedExpression plus(); //does nothing
    int signum();
    boolean isZero();

    Float32ExpChainedExpression round(IFloat32Exp val);
    Float32ExpChainedExpression round(long val);
    Float32ExpChainedExpression round(double val);

    Float32ExpChainedExpression shiftLeft(int val);
    Float32ExpChainedExpression shiftRight(int val);

    boolean equals(Object object);
    boolean equals(IFloat32Exp val);
    boolean equals(long val);
    boolean equals(double val);

    boolean approximately(IFloat32Exp val, int bitsSimilarCount);
    boolean approximately(long val, int bitsSimilarCount);
    boolean approximately(double val, int bitsSimilarCount);

    int compareTo(IFloat32Exp val);
    int compareTo(long val);
    int compareTo(double val);

    boolean lessThan(IFloat32Exp val);
    boolean lessThan(long val);
    boolean lessThan(double val);

    boolean lessOrEquals(IFloat32Exp val);
    boolean lessOrEquals(long val);
    boolean lessOrEquals(double val);

    boolean greaterOrEquals(IFloat32Exp val);
    boolean greaterOrEquals(long val);
    boolean greaterOrEquals(double val);

    boolean greaterThan(IFloat32Exp val);
    boolean greaterThan(long val);
    boolean greaterThan(double val);

    Float32ExpChainedExpression min(IFloat32Exp val);
    Float32ExpChainedExpression min(long val);
    Float32ExpChainedExpression min(double val);

    Float32ExpChainedExpression max(IFloat32Exp val);
    Float32ExpChainedExpression max(long val);
    Float32ExpChainedExpression max(double val);

    String toString();
    StringBuilder toString(StringBuilder sb);
    String toEngineeringString();
    StringBuilder toEngineeringString(StringBuilder sb);
    StringBuilder toString(StringBuilder sb, int base, int digits, int exponentMultiple,
                           ExponentToStringInterface exponentToString);

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableFloat32Exp toImmutable();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();
}
