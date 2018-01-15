package com.tbohne.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

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
 * The return types are Float32ExpChainedExpression, allowing you to chain operations, but not
 * directly reassign to a Float32Exp. This prevents accidental mutation.
 *
 * Compiler error:
 *      Float32Exp thing(Float32Exp left, Float32Exp right) {
 *          Float32Exp result = left.multiply(right).add(3);
 *          return result;
*       }
 * Correct:
 *      void thing(Float32Exp left, Float32Exp right) {
 *          Float32Exp result = new Float32Exp(left);
 *          result.multiply(right).add(3);
 *          return result;
 *      }
 *
 * TODO: There seems to be more methods in BigDecimal to emulate
 */
public class Float32Exp extends Float32ExpSharedBase implements Float32ExpChainedExpression {
    public Float32Exp() {set(0, ZERO_EXPONENT);}
    public Float32Exp(char[] in, int offset, int len) {set(in, offset, len);}
    public Float32Exp(char[] in) {
        set(in, 0, in.length);
    }
    public Float32Exp(String val) {
        set(val.toCharArray(), 0, val.length());
    }
    public Float32Exp(double val) {
        set(doubleToSignificand(val), doubleToExponent(val));
    }
    public Float32Exp(int val) {setNormalized(val, 0);}
    public Float32Exp(long val) {setNormalized(val, 0);}
    public Float32Exp(BigDecimal val) {
        set(val);
    }
    public Float32Exp(BigInteger val) {
        set(val);
    }
    public Float32Exp(IFloat32Exp val) {
        set(val.significand(), val.exponent());
    }
    public Float32Exp(int significand, int exponent) {set(significand, exponent);}

    public static Float32Exp getPowerOf10(int exponent) {
        long pow10Parts = Float32Exp.getPowerOf10Parts(exponent);
        return new Float32Exp((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    public Float32ExpChainedExpression set(int val) {super.setNormalized(val, 0); return this;}
    public Float32ExpChainedExpression set(long val) {super.setNormalized(val, 0); return this;}
    public Float32ExpChainedExpression set(char[] in) {super.set(in, 0, in.length); return this;}
    public Float32ExpChainedExpression set(String val) {super.set(val.toCharArray(), 0, val.length()); return this;}
    public Float32ExpChainedExpression set(double val) {set(doubleToSignificand(val), doubleToExponent(val)); return this;}
    public Float32ExpChainedExpression set(char[] in, int offset, int len) {super.set(in,offset,len); return this;}
    public Float32ExpChainedExpression set(BigDecimal val) {super.set(val); return this;}
    public Float32ExpChainedExpression set(BigInteger val) {super.set(val); return this;}
    public Float32ExpChainedExpression set(IFloat32Exp val) {super.set(val.significand(), val.exponent()); return this;}

    public Float32ExpChainedExpression add(IFloat32Exp val) {add(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression add(long val) {add(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression add(double val) {add(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void add(int otherSignificand, int otherExponent) {
        if (exponent < otherExponent) {
            int t = otherSignificand;
            otherSignificand = significand;
            significand = t;
            t = otherExponent;
            otherExponent = exponent;
            exponent = t;
        }
        int diff = exponent - otherExponent;
        if (diff < INT_MAX_BITS) {
            long l = (((long) significand) << diff) + otherSignificand;
            setNormalized(l, ((long) exponent) - diff);
        }
    }

    public Float32ExpChainedExpression subtract(IFloat32Exp val) {subtract(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression subtract(long val) {subtract(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression subtract(double val) {subtract(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void subtract(int otherSignificand, int otherExponent) {
        if (exponent >= otherExponent) {
            int diff = exponent - otherExponent;
            if (diff < INT_MAX_BITS) {
                long l = (((long) significand) << diff) - otherSignificand;
                setNormalized(l, otherExponent);
            }
        } else { // if (otherExponent > exponent)
            int diff = otherExponent - exponent;
            if (significand == 0) {
                setNormalized(-(long)otherSignificand, otherExponent);
            } else if (diff < INT_MAX_BITS) {
                long l = significand - (((long) otherSignificand) << diff);
                setNormalized(l, exponent);
            } else {
                significand = otherSignificand;
                exponent = otherExponent;
            }
        }
    }

    public Float32ExpChainedExpression multiply(IFloat32Exp val) {multiply(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression multiply(long val) {multiply(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression multiply(double val) {multiply(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void multiply(int otherSignificand, int otherExponent) {
        setNormalized(((long)significand) * otherSignificand, ((long) exponent) + otherExponent);}

    public Float32ExpChainedExpression divide(IFloat32Exp val) {divide(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression divide(long val) {divide(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression divide(double val) {divide(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void divide(int otherSignificand, int otherExponent) {
        long sig = ((long) significand) << INT_MAX_BITS;
        setNormalized(sig / otherSignificand, ((long) exponent) - otherExponent - INT_MAX_BITS);
    }

    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, IFloat32Exp div) {muldiv(mul.significand(), mul.exponent(), div.significand(), div.exponent()); return this;}
    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, long div) {muldiv(mul.significand(), mul.exponent(), longToSignificand(div), longToExponent(div)); return this;}
    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, double div) {muldiv(mul.significand(), mul.exponent(), doubleToSignificand(div), doubleToExponent(div)); return this;}
    public Float32ExpChainedExpression muldiv(long mul, IFloat32Exp div) {muldiv(longToSignificand(mul), longToExponent(mul), div.significand(), div.exponent()); return this;}
    public Float32ExpChainedExpression muldiv(double mul, IFloat32Exp div) {muldiv(doubleToSignificand(mul), doubleToExponent(mul), div.significand(), div.exponent()); return this;}
    public Float32ExpChainedExpression muldiv(long mul, long div) {muldiv(longToSignificand(mul), longToExponent(mul), longToSignificand(div), longToExponent(div)); return this;}
    private void muldiv(int mulSignificand, int mulExponent, int divSignificand, int divExponent) {
        setNormalized(((long) significand) * mulSignificand / divSignificand, ((long) exponent) + mulExponent - divExponent);
    }

    public Float32ExpChainedExpression divideToIntegralValue(IFloat32Exp val)
    {divideToIntegralValue(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression divideToIntegralValue(long val)
    {divideToIntegralValue(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression divideToIntegralValue(double val)
    {divideToIntegralValue(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void divideToIntegralValue(int otherSignificand, int otherExponent) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand; //regular division
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else if (exp >= 0) {
            setNormalized(quotient, exp); // result has no fractional bits
        } else {
            long shift = 1L << -exp;
            setNormalized(quotient / shift, 0); //truncated to integer at target scale
        }
    }
    
    public Float32ExpChainedExpression remainder(IFloat32Exp val) {remainder(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression remainder(long val) {remainder(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression remainder(double val) {remainder(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void remainder(int otherSignificand, int otherExponent) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            setNormalized(quotient, exp); // result has no fractional bits
        } else if (exp >= 0) {
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else {
            long shift = 1L << -exp;
            long truncQuot = quotient / shift * shift * otherSignificand; //truncated to integer at same scale
            setNormalized(origSig - truncQuot, exponent - INT_MAX_BITS);
        }
    }

    public Float32ExpChainedExpression divideAndRemainder(IFloat32Exp val, Float32Exp outRemainder)
    {divideAndRemainder(val.significand(), val.exponent(), outRemainder); return this;}
    public Float32ExpChainedExpression divideAndRemainder(long val, Float32Exp outRemainder)
    {divideAndRemainder(longToSignificand(val), longToExponent(val), outRemainder); return this;}
    public Float32ExpChainedExpression divideAndRemainder(double val, Float32Exp outRemainder)
    {divideAndRemainder(doubleToSignificand(val), doubleToExponent(val), outRemainder); return this;}
    private void divideAndRemainder(int otherSignificand, int otherExponent, Float32Exp outRemainder) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            outRemainder.set(significand, exponent);
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else if (exp >= 0) {
            setNormalized(quotient, exp); // result has no fractional bits
            outRemainder.set(0, ZERO_EXPONENT);
        } else {
            long shift = 1L << -exp;
            long intQutot = quotient / shift;
            long truncQuot = intQutot * shift * otherSignificand; //truncated to integer at same scale
            setNormalized(intQutot, 0);
            outRemainder.setNormalized(origSig - truncQuot, exponent - INT_MAX_BITS);
        }
    }

    //this^other can be defined as pow(2,other*log(this,2)),
    //intermediates probably require extra precision
    //http://www.netlib.org/fdlibm/e_pow.c for edge cases and notes
    public Float32ExpChainedExpression pow(IFloat32Exp val) {pow(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression pow(long val) {pow(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression pow(double val) {pow(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void pow(int otherSignificand, int otherExponent) {
        if (significand == 0) { //0^X
            //do nothing
        } else if (otherSignificand == 0) { //X^0
            significand = 0x40000000;
            exponent = -EXPONENT_BIAS;
        } else if (significand == 0x40000000) { // (2^N)^X
            long exp = ((long) exponent) + otherExponent - EXPONENT_BIAS;
            if (exp > Integer.MAX_VALUE || exp < Integer.MIN_VALUE) {
                throw new ArithmeticException("Exponent " + exponent + " out of range");
            }
            exponent = (int) exp;
        } else if (significand == 0x50000000 && exponent == -27) { //10^X
            long pow10 = getPowerOf10Parts(otherSignificand << otherExponent); //check for overflows
            significand = (int) (pow10 >> INT_MAX_BITS);
            exponent = (int) pow10;
        } else if (otherSignificand == 0x40000000 && otherExponent == -29 ) { //X^2
            multiply(significand, exponent);
        } else { //N^X
            log2();
            multiply(otherSignificand, otherExponent);
            pow2();
        }
    }

    public Float32ExpChainedExpression pow2() {
        if (exponent > 0) {
            StringBuilder sb = new StringBuilder();
            toString(sb).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        } else if (significand == 0 && exponent == ZERO_EXPONENT) {
            set(1 << (INT_MAX_BITS - 2), -EXPONENT_BIAS);
            return this;
        } else if (exponent >= 0) {
            setNormalized(1, 0);
            return this;
        }
        int integer_part;
        double pre_fractional_double;
        if (exponent > -INT_MAX_BITS) {
            int fractional_bits = (exponent < -INT_MAX_BITS) ? INT_MAX_BITS : -exponent;
            int int_bits = INT_MAX_BITS - fractional_bits - 1;
            integer_part = (significand >> fractional_bits);
            long pre_fraction_long = (significand & ((1 << fractional_bits) - 1)) << int_bits;
            pre_fractional_double = pre_fraction_long * Math.pow(2, -31);
        } else {
            integer_part = 0;
            pre_fractional_double = significand * Math.pow(2,exponent);
        }
        double post_fraction_double = Math.pow(2, pre_fractional_double);
        int exp = integer_part + doubleToExponent(post_fraction_double);
        int sig = doubleToSignificand(post_fraction_double);
        set(sig, exp);
        return this;
    }

    public Float32ExpChainedExpression log2i() {
        if (significand <= 0) {
            throw new IllegalArgumentException("nonpositive value" + this);
        }
        setNormalized((long)(exponent)+EXPONENT_BIAS, 0);
        return this;
    }

    public Float32ExpChainedExpression log2() {
        if (significand <= 0) {
            throw new IllegalArgumentException("nonpositive value " + this);
        } else if (significand == 0x40000000 && exponent == -EXPONENT_BIAS) { // lg(1) == 0.0
            significand = 0;
            exponent = ZERO_EXPONENT;
            return this;
        } else if (significand == 0x40000000) {// lg(power-of-2) == exponent
            long value = exponent + EXPONENT_BIAS;
            setNormalized(value,0);
            return this;
        }
        long integer_bits = exponent + EXPONENT_BIAS;
        double pre_fractional_double = significand * Math.pow(2,-31);
        double post_fractional_double = Math.log(pre_fractional_double)/Math.log(2) + 1;
        set(doubleToSignificand(post_fractional_double), doubleToExponent(post_fractional_double));
        add(longToSignificand(integer_bits),longToExponent(integer_bits));
        if (INTERNAL_ASSERTS) {
            double max = Double.MAX_VALUE / significand;
            double maxExp = Math.log(max)/Math.log(2);
            if (exponent < maxExp) {
                assertApproximately(significand * Math.pow(2, exponent), this, 30);
            }
        }
        return this;
    }

    public Float32ExpChainedExpression abs() {
        if (significand == 0x80000000) {
            significand = 0x40000000;
            exponent += 1;
        } else if (significand < 0) {
            significand = -significand;
        }
        return this;
    }

    public Float32ExpChainedExpression negate() {
        if (significand == 0x40000000) {
            significand = 0x80000000;
            exponent -=1;
        } else if (significand == 0x80000000) {
            significand = 0x40000000;
            exponent += 1;
        } else{
            significand = -significand;
        }
        return this;
    }

    public Float32ExpChainedExpression plus() { return this;}

    public Float32ExpChainedExpression round(IFloat32Exp val) {round(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression round(long val) {round(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression round(double val) {round(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private Float32ExpChainedExpression round(int otherSignificand, int otherExponent) {
        //TODO: Implement round
        throw new UnsupportedOperationException();
    }

    public Float32ExpChainedExpression shiftLeft(int val) {setNormalized(significand, ((long) exponent) + val); return this;}
    public Float32ExpChainedExpression shiftRight(int val) {setNormalized(significand, ((long) exponent) - val); return this;}

    public Float32ExpChainedExpression min(IFloat32Exp val) {min(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression min(long val) {min(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression min(double val) {min(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void min(int otherSignificand, int otherExponent) {
        if (exponent > otherExponent ||
                (exponent == otherExponent && significand > otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
    }

    public Float32ExpChainedExpression max(IFloat32Exp val) {max(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression max(long val) {max(longToSignificand(val), longToExponent(val)); return this;}
    public Float32ExpChainedExpression max(double val) {max(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void max(int otherSignificand, int otherExponent) {
        if (exponent < otherExponent ||
                (exponent == otherExponent && significand < otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
    }
}
