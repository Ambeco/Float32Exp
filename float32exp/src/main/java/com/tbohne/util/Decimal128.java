package com.tbohne.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A mutable fixed-precision signed decimal.
 * It works like double, but with a 32 binary significant digits, and a 32 bit base-2 exponent.
 * This equates to ~9.6 decimal digits for both parts.
 * Note that this is _wildly_ different than the IEEE 754 format binary128, which is 113 and 15
 * bits, and also the IEEE 754 format decimal128, which is 110 and 17 bits.
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
 * The return types are Decimal128ChainedExpression, allowing you to chain operations, but not
 * directly reassign to a Decimal128. This prevents accidental mutation.
 *
 * Compiler error:
 *      Decimal128 thing(Decimal128 left, Decimal128 right) {
 *          Decimal128 result = left.multiply(right).add(3);
 *          return result;
*       }
 * Correct:
 *      void thing(Decimal128 left, Decimal128 right) {
 *          Decimal128 result = new Decimal128(left);
 *          result.multiply(right).add(3);
 *          return result;
 *      }
 *
 * TODO: There seems to be more methods in BigDecimal to emulate
 */
public class Decimal128 extends Decimal128SharedBase implements Decimal128ChainedExpression {
    public Decimal128() {set(0, ZERO_EXPONENT);}
    public Decimal128(char[] in, int offset, int len) {set(in, offset, len);}
    public Decimal128(char[] in) {
        set(in, 0, in.length);
    }
    public Decimal128(String val) {
        set(val.toCharArray(), 0, val.length());
    }
    public Decimal128(double val) {
        set(doubleToSignificand(val), doubleToExponent(val));
    }
    public Decimal128(int val) {setNormalized(val, 0);}
    public Decimal128(long val) {setNormalized(val, 0);}
    public Decimal128(BigDecimal val) {
        set(val);
    }
    public Decimal128(BigInteger val) {
        set(val);
    }
    public Decimal128(IDecimal128 val) {
        set(val.significand(), val.exponent());
    }
    public Decimal128(int significand, int exponent) {set(significand, exponent);}

    public static Decimal128 getPowerOf10(int exponent) {
        long pow10Parts = Decimal128.getPowerOf10Parts(exponent);
        return new Decimal128((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    public Decimal128ChainedExpression set(int val) {super.setNormalized(val, 0); return this;}
    public Decimal128ChainedExpression set(long val) {super.setNormalized(val, 0); return this;}
    public Decimal128ChainedExpression set(char[] in) {super.set(in, 0, in.length); return this;}
    public Decimal128ChainedExpression set(String val) {super.set(val.toCharArray(), 0, val.length()); return this;}
    public Decimal128ChainedExpression set(double val) {set(doubleToSignificand(val), doubleToExponent(val)); return this;}
    public Decimal128ChainedExpression set(char[] in, int offset, int len) {super.set(in,offset,len); return this;}
    public Decimal128ChainedExpression set(BigDecimal val) {super.set(val); return this;}
    public Decimal128ChainedExpression set(BigInteger val) {super.set(val); return this;}
    public Decimal128ChainedExpression set(IDecimal128 val) {super.set(val.significand(), val.exponent()); return this;}

    public Decimal128ChainedExpression add(IDecimal128 val) {add(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression add(long val) {add(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression add(double val) {add(doubleToSignificand(val), doubleToExponent(val)); return this;}
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
            long l = significand;
            l += otherSignificand >> diff;
            setNormalized(l, exponent);
        }
    }

    public Decimal128ChainedExpression subtract(IDecimal128 val) {subtract(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression subtract(long val) {subtract(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression subtract(double val) {subtract(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void subtract(int otherSignificand, int otherExponent) {
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
            long l = significand;
            l -= otherSignificand >> diff;
            setNormalized(l, exponent);
        }
    }

    public Decimal128ChainedExpression multiply(IDecimal128 val) {multiply(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression multiply(long val) {multiply(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression multiply(double val) {multiply(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void multiply(int otherSignificand, int otherExponent) {
        setNormalized(((long)significand) * otherSignificand, ((long) exponent) + otherExponent);}

    public Decimal128ChainedExpression divide(IDecimal128 val) {divide(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression divide(long val) {divide(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression divide(double val) {divide(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void divide(int otherSignificand, int otherExponent) {
        long sig = ((long) significand) << INT_MAX_BITS;
        setNormalized(sig / otherSignificand, ((long) exponent) - otherExponent - INT_MAX_BITS);
    }

    public Decimal128ChainedExpression muldiv(IDecimal128 mul, IDecimal128 div) {muldiv(mul.significand(), mul.exponent(), div.significand(), div.exponent()); return this;}
    public Decimal128ChainedExpression muldiv(IDecimal128 mul, long div) {muldiv(mul.significand(), mul.exponent(), longToSignificand(div), longToExponent(div)); return this;}
    public Decimal128ChainedExpression muldiv(IDecimal128 mul, double div) {muldiv(mul.significand(), mul.exponent(), doubleToSignificand(div), doubleToExponent(div)); return this;}
    public Decimal128ChainedExpression muldiv(long mul, IDecimal128 div) {muldiv(longToSignificand(mul), longToExponent(mul), div.significand(), div.exponent()); return this;}
    public Decimal128ChainedExpression muldiv(double mul, IDecimal128 div) {muldiv(doubleToSignificand(mul), doubleToExponent(mul), div.significand(), div.exponent()); return this;}
    public Decimal128ChainedExpression muldiv(long mul, long div) {muldiv(longToSignificand(mul), longToExponent(mul), longToSignificand(div), longToExponent(div)); return this;}
    private void muldiv(int mulSignificand, int mulExponent, int divSignificand, int divExponent) {
        setNormalized(((long) significand) * mulSignificand / divSignificand, ((long) exponent) + mulExponent - divExponent);
    }

    public Decimal128ChainedExpression divideToIntegralValue(IDecimal128 val)
    {divideToIntegralValue(val.significand(), val.exponent(), null); return this;}
    public Decimal128ChainedExpression divideToIntegralValue(long val)
    {divideToIntegralValue(longToSignificand(val), longToExponent(val), null); return this;}
    public Decimal128ChainedExpression divideToIntegralValue(double val)
    {divideToIntegralValue(doubleToSignificand(val), doubleToExponent(val), null); return this;}
    private void divideToIntegralValue(int otherSignificand, int otherExponent, Decimal128 outRemainder) {
        long sig = ((long) significand) << INT_MAX_BITS;
        sig /= otherSignificand;
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        if (sig == 0 || exp < -INT_MAX_BITS) {
            significand = 0;
            exponent = ZERO_EXPONENT;
        } else if (exp >= 0) {
            setNormalized(sig, exp);
        } else {
            setNormalized(sig >> -exp, 0);
        }
    }
    
    public Decimal128ChainedExpression remainder(IDecimal128 val) {remainder(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression remainder(long val) {remainder(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression remainder(double val) {remainder(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void remainder(int otherSignificand, int otherExponent) {
        long sig = ((long) significand) << INT_MAX_BITS;
        sig /= otherSignificand;
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        if (sig == 0 || exp < -INT_MAX_BITS) {
            return;
        } else if (exp >= 0) {
            significand = 0;
            exponent = ZERO_EXPONENT;
        } else {
            sig >>= -exp; //truncate
            sig <<= -exp;
            sig *= otherSignificand;
            exp += otherExponent;
            add(-longToSignificand(sig), (int)(longToExponent(sig) + exp));
        }
    }

    public Decimal128ChainedExpression divideAndRemainder(IDecimal128 val, Decimal128 outRemainder)
    {divideAndRemainder(val.significand(), val.exponent(), outRemainder); return this;}
    public Decimal128ChainedExpression divideAndRemainder(long val, Decimal128 outRemainder)
    {divideAndRemainder(longToSignificand(val), longToExponent(val), outRemainder); return this;}
    public Decimal128ChainedExpression divideAndRemainder(double val, Decimal128 outRemainder)
    {divideAndRemainder(doubleToSignificand(val), doubleToExponent(val), outRemainder); return this;}
    private void divideAndRemainder(int otherSignificand, int otherExponent,
            Decimal128 outRemainder) {
        long l = ((long) significand) << INT_MAX_BITS;
        l /= otherSignificand;
        exponent -= otherExponent + INT_MAX_BITS;
        if (INTERNAL_ASSERTS) {
            assertNormalized();
        }
        throw new UnsupportedOperationException("what about remainder");
    }

    //this^other can be defined as pow(2,other*log(this,2)),
    //intermediates probably require extra precision
    //http://www.netlib.org/fdlibm/e_pow.c for edge cases and notes
    public Decimal128ChainedExpression pow(IDecimal128 val) {pow(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression pow(long val) {pow(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression pow(double val) {pow(doubleToSignificand(val), doubleToExponent(val)); return this;}
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

    public Decimal128ChainedExpression pow2() {
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

    public Decimal128ChainedExpression log2i() {
        if (significand <= 0) {
            throw new IllegalArgumentException("nonpositive value" + this);
        }
        setNormalized((long)(exponent)+EXPONENT_BIAS, 0);
        return this;
    }

    public Decimal128ChainedExpression log2() {
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

    public Decimal128ChainedExpression abs() {
        if (significand == 0x80000000) {
            significand = 0x40000000;
            exponent += 1;
        } else if (significand < 0) {
            significand = -significand;
        }
        return this;
    }

    public Decimal128ChainedExpression negate() {
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

    public Decimal128ChainedExpression plus() { return this;}

    public Decimal128ChainedExpression round(IDecimal128 val) {round(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression round(long val) {round(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression round(double val) {round(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private Decimal128ChainedExpression round(int otherSignificand, int otherExponent) {
        throw new UnsupportedOperationException();
    }

    public Decimal128ChainedExpression shiftLeft(int val) {setNormalized(significand, ((long) exponent) + val); return this;}
    public Decimal128ChainedExpression shiftRight(int val) {setNormalized(significand, ((long) exponent) - val); return this;}

    public Decimal128ChainedExpression min(IDecimal128 val) {min(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression min(long val) {min(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression min(double val) {min(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void min(int otherSignificand, int otherExponent) {
        if (exponent > otherExponent ||
                (exponent == otherExponent && significand > otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
    }

    public Decimal128ChainedExpression max(IDecimal128 val) {max(val.significand(), val.exponent()); return this;}
    public Decimal128ChainedExpression max(long val) {max(longToSignificand(val), longToExponent(val)); return this;}
    public Decimal128ChainedExpression max(double val) {max(doubleToSignificand(val), doubleToExponent(val)); return this;}
    private void max(int otherSignificand, int otherExponent) {
        if (exponent < otherExponent ||
                (exponent == otherExponent && significand < otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
    }
}
