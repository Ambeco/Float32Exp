package com.tbohne.util.math;

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
 *      void thing(IFloat32ExpL left, IFloat32ExpL right) {
 *          return left.multiply(right);
*       }
 * Correct:
 *      void thing(IFloat32ExpL left, IFloat32ExpL right) {
 *          IFloat32ExpL result = new IFloat32ExpL(left);
 *          result.multiply(right);
 *          return result;
 *      }
 *
 */
public interface Float32ExpLChainedExpression extends IFloat32ExpL {
    Float32ExpLChainedExpression add(IFloat32ExpL val);
    Float32ExpLChainedExpression add(long val);
    Float32ExpLChainedExpression add(double val);

    Float32ExpLChainedExpression subtract(IFloat32ExpL val);
    Float32ExpLChainedExpression subtract(long val);
    Float32ExpLChainedExpression subtract(double val);

    Float32ExpLChainedExpression multiply(IFloat32ExpL val);
    Float32ExpLChainedExpression multiply(long val);
    Float32ExpLChainedExpression multiply(double val);

    Float32ExpLChainedExpression divide(IFloat32ExpL val);
    Float32ExpLChainedExpression divide(long val);
    Float32ExpLChainedExpression divide(double val);

    Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, IFloat32ExpL div);
    Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, long div);
    Float32ExpLChainedExpression muldiv(IFloat32ExpL mul, double div);
    Float32ExpLChainedExpression muldiv(long mul, IFloat32ExpL div);
    Float32ExpLChainedExpression muldiv(double mul, IFloat32ExpL div);
    Float32ExpLChainedExpression muldiv(long mul, long div);
    Float32ExpLChainedExpression muldiv(long mul, double div);
    Float32ExpLChainedExpression muldiv(double mul, long div);
    Float32ExpLChainedExpression muldiv(double mul, double div);

    Float32ExpLChainedExpression divideToIntegralValue(IFloat32ExpL val);
    Float32ExpLChainedExpression divideToIntegralValue(long val);
    Float32ExpLChainedExpression divideToIntegralValue(double val);

    Float32ExpLChainedExpression remainder(IFloat32ExpL val);
    Float32ExpLChainedExpression remainder(long val);
    Float32ExpLChainedExpression remainder(double val);

    Float32ExpLChainedExpression divideAndRemainder(IFloat32ExpL val, Float32ExpL outRemainder);
    Float32ExpLChainedExpression divideAndRemainder(long val, Float32ExpL outRemainder);
    Float32ExpLChainedExpression divideAndRemainder(double val, Float32ExpL outRemainder);

    Float32ExpLChainedExpression pow(IFloat32ExpL val);
    Float32ExpLChainedExpression pow(long val);
    Float32ExpLChainedExpression pow(double val);

    Float32ExpLChainedExpression log2();
    Float32ExpLChainedExpression log2i(); //only keeps the integer part
    Float32ExpLChainedExpression pow2(); //inverse of log2. DOES NOT SQUARE.

    Float32ExpLChainedExpression abs();
    Float32ExpLChainedExpression negate();
    Float32ExpLChainedExpression plus(); //does nothing

    Float32ExpLChainedExpression round();
    Float32ExpLChainedExpression round(IFloat32ExpL val);
    Float32ExpLChainedExpression round(long val);
    Float32ExpLChainedExpression round(double val);
    Float32ExpLChainedExpression floor();
    Float32ExpLChainedExpression floor(IFloat32ExpL val);
    Float32ExpLChainedExpression floor(long val);
    Float32ExpLChainedExpression floor(double val);

    Float32ExpLChainedExpression shiftLeft(int val);
    Float32ExpLChainedExpression shiftRight(int val);

    Float32ExpLChainedExpression min(IFloat32ExpL val);
    Float32ExpLChainedExpression min(long val);
    Float32ExpLChainedExpression min(double val);

    Float32ExpLChainedExpression max(IFloat32ExpL val);
    Float32ExpLChainedExpression max(long val);
    Float32ExpLChainedExpression max(double val);
}
