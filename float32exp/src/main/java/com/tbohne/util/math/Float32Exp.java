package com.tbohne.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A mutable fixed-precision signed decimal.
 *
 * @deprecated use FLoat32ExpL instead
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
    public Float32Exp() {super(0, ZERO_EXPONENT);}
    public Float32Exp(char[] in, int offset, int len) {set(in, offset, len);}
    public Float32Exp(char[] in) {set(in, 0, in.length);}
    public Float32Exp(String val) {set(val.toCharArray(), 0, val.length());}
    public Float32Exp(double val) {super(getDoubleParts(val));}
    public Float32Exp(int val) {super(getLongParts(val));}
    public Float32Exp(long val) {super(getLongParts(val));}
    public Float32Exp(BigDecimal val) {set(val);}
    public Float32Exp(BigInteger val) {set(val);}
    public Float32Exp(IFloat32Exp val) {super(val.significand(), val.exponent());}
    public Float32Exp(int significand, int exponent) {super(significand, exponent);}

    public static Float32Exp getPowerOf10(int exponent) {
        long pow10Parts = Float32Exp.getPowerOf10Parts(exponent);
        return new Float32Exp((int)(pow10Parts >> INT_MAX_BITS), (int)pow10Parts);
    }

    public Float32ExpChainedExpression set(int val) {
        long parts = getLongParts((long) val);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;}
    public Float32ExpChainedExpression set(long val) {
        long parts = getLongParts(val);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;}
    public Float32ExpChainedExpression set(char[] in) {super.set(in, 0, in.length); return this;}
    public Float32ExpChainedExpression set(String val) {super.set(val.toCharArray(), 0, val.length()); return this;}
    public Float32ExpChainedExpression set(double val) {setImpl(getDoubleParts(val)); return this;}
    public Float32ExpChainedExpression set(char[] in, int offset, int len) {super.set(in,offset,len); return this;}
    public Float32ExpChainedExpression set(BigDecimal val) {super.set(val); return this;}
    public Float32ExpChainedExpression set(BigInteger val) {super.set(val); return this;}
    public Float32ExpChainedExpression set(IFloat32Exp val) {setImpl(val.significand(), val.exponent()); return this;}

    public Float32ExpChainedExpression add(IFloat32Exp val) {return addImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression add(long val) {return addImpl(getLongParts(val));}
    public Float32ExpChainedExpression add(double val) {return addImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression addImpl(long parts) {return addImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression addImpl(int otherSignificand, int otherExponent) {
        if (exponent < otherExponent) {
            int t = otherSignificand;
            otherSignificand = significand;
            significand = t;
            t = otherExponent;
            otherExponent = exponent;
            exponent = t;
        }
        long diff = ((long) exponent) - otherExponent;
        if (diff < INT_MAX_BITS) {
            long l = (((long) significand) << diff) + otherSignificand;
            long parts = getNormalizedParts(l, ((long) exponent) - diff);
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
        }
        return this;
    }

    public Float32ExpChainedExpression subtract(IFloat32Exp val) {return subtractImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression subtract(long val) {return subtractImpl(getLongParts(val));}
    public Float32ExpChainedExpression subtract(double val) {return subtractImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression subtractImpl(long parts) {return subtractImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression subtractImpl(int otherSignificand, int otherExponent) {
        if (exponent >= otherExponent) {
            int diff = exponent - otherExponent;
            if (diff < INT_MAX_BITS) {
                long l = (((long) significand) << diff) - otherSignificand;
                long parts = getNormalizedParts(l, (long) otherExponent);
                this.significand = (int) (parts >> INT_MAX_BITS);
                this.exponent = (int) parts;
            }
        } else { // if (otherExponent > exponent)
            int diff = otherExponent - exponent;
            if (significand == 0) {
                long v = -(long)otherSignificand;
                long parts = getNormalizedParts(v, (long) otherExponent);
                this.significand = (int) (parts >> INT_MAX_BITS);
                this.exponent = (int) parts;
            } else if (diff < INT_MAX_BITS) {
                long l = significand - (((long) otherSignificand) << diff);
                long parts = getNormalizedParts(l, (long) exponent);
                this.significand = (int) (parts >> INT_MAX_BITS);
                this.exponent = (int) parts;
            } else {
                significand = otherSignificand;
                exponent = otherExponent;
            }
        }
        return this;
    }

    public Float32ExpChainedExpression multiply(IFloat32Exp val) {return multiplyImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression multiply(long val) {return multiplyImpl(getLongParts(val));}
    public Float32ExpChainedExpression multiply(double val) {return multiplyImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression multiplyImpl(long parts) {return multiplyImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression multiplyImpl(int otherSignificand, int otherExponent) {
        long parts = getNormalizedParts(((long)significand) * otherSignificand, ((long) exponent) + otherExponent);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;
    }

    public Float32ExpChainedExpression divide(IFloat32Exp val) {return divideImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression divide(long val) {return divideImpl(getLongParts(val));}
    public Float32ExpChainedExpression divide(double val) {return divideImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression divideImpl(long parts) {return divideImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression divideImpl(int otherSignificand, int otherExponent) {
        long sig = ((long) significand) << INT_MAX_BITS;
        long parts = getNormalizedParts(sig / otherSignificand, ((long) exponent) - otherExponent - INT_MAX_BITS);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;
    }

    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, IFloat32Exp div)
    {return muldivImpl(mul.significand(), mul.exponent(), div.significand(), div.exponent());}
    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, long div)
    {return muldivImpl(mul.significand(), mul.exponent(), getLongParts(div));}
    public Float32ExpChainedExpression muldiv(IFloat32Exp mul, double div)
    {return muldivImpl(mul.significand(), mul.exponent(), getDoubleParts(div));}
    public Float32ExpChainedExpression muldiv(long mul, IFloat32Exp div)
    {return muldivImpl(getLongParts(mul), div.significand(), div.exponent());}
    public Float32ExpChainedExpression muldiv(long mul, long div)
    {return muldivImpl(getLongParts(mul), getLongParts(div));}
    public Float32ExpChainedExpression muldiv(long mul, double div)
    {return muldivImpl(getLongParts(mul), getDoubleParts(div));}
    public Float32ExpChainedExpression muldiv(double mul, IFloat32Exp div)
    {return muldivImpl(getDoubleParts(mul), div.significand(), div.exponent());}
    public Float32ExpChainedExpression muldiv(double mul, long div)
    {return muldivImpl(getDoubleParts(mul), getLongParts(div));}
    public Float32ExpChainedExpression muldiv(double mul, double div)
    {return muldivImpl(getDoubleParts(mul), getDoubleParts(div));}
    private Float32ExpChainedExpression muldivImpl(long mulParts, long divParts)
    {return muldivImpl((int)(mulParts >> INT_MAX_BITS), (int)mulParts, (int)(divParts >> INT_MAX_BITS), (int)divParts);}
    private Float32ExpChainedExpression muldivImpl(int mulSignificand, int mulExponent, long divParts)
    {return muldivImpl(mulSignificand, mulExponent, (int)(divParts >> INT_MAX_BITS), (int)divParts);}
    private Float32ExpChainedExpression muldivImpl(long mulParts, int divSignificand, int divExponent)
    {return muldivImpl((int)(mulParts >> INT_MAX_BITS), (int)mulParts, divSignificand, divExponent);}
    private Float32ExpChainedExpression muldivImpl(int mulSignificand, int mulExponent, int divSignificand, int divExponent) {
        long parts = getNormalizedParts(((long) significand) * mulSignificand / divSignificand, ((long) exponent) + mulExponent - divExponent);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;
    }

    public Float32ExpChainedExpression divideToIntegralValue(IFloat32Exp val)
    {return divideToIntegralValueImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression divideToIntegralValue(long val)
    {return divideToIntegralValueImpl(getLongParts(val));}
    public Float32ExpChainedExpression divideToIntegralValue(double val)
    {return divideToIntegralValueImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression divideToIntegralValueImpl(long parts)
    {return divideToIntegralValueImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression divideToIntegralValueImpl(int otherSignificand, int otherExponent) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand; //regular division
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else if (exp >= 0) {
            long parts = getNormalizedParts(quotient, exp); // result has no fractional bits
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
        } else {
            long shift = 1L << -exp;
            long parts = getLongParts(quotient / shift); //truncated to integer at target scale
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
        }
        return this;
    }
    
    public Float32ExpChainedExpression remainder(IFloat32Exp val) {return remainderImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression remainder(long val) {return remainderImpl(getLongParts(val));}
    public Float32ExpChainedExpression remainder(double val) {return remainderImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression remainderImpl(long parts) {return remainderImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression remainderImpl(int otherSignificand, int otherExponent) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            long parts = getNormalizedParts(quotient, exp); // result has no fractional bits
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
        } else if (exp >= 0) {
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else {
            long shift = 1L << -exp;
            long truncQuot = quotient / shift * shift * otherSignificand; //truncated to integer at same scale
            long parts = getNormalizedParts(origSig - truncQuot, (long) (exponent - INT_MAX_BITS));
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
        }
        return this;
    }

    public Float32ExpChainedExpression divideAndRemainder(IFloat32Exp val, Float32Exp outRemainder)
    {return divideAndRemainderImpl(val.significand(), val.exponent(), outRemainder);}
    public Float32ExpChainedExpression divideAndRemainder(long val, Float32Exp outRemainder)
    {return divideAndRemainderImpl(getLongParts(val), outRemainder);}
    public Float32ExpChainedExpression divideAndRemainder(double val, Float32Exp outRemainder)
    {return divideAndRemainderImpl(getDoubleParts(val), outRemainder);}
    private Float32ExpChainedExpression divideAndRemainderImpl(long parts, Float32Exp outRemainder)
    {return divideAndRemainderImpl((int)(parts >> INT_MAX_BITS), (int)parts, outRemainder);}
    private Float32ExpChainedExpression divideAndRemainderImpl(int otherSignificand, int otherExponent, Float32Exp outRemainder) {
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            outRemainder.setImpl(significand, exponent);
            significand = 0; // result is zero
            exponent = ZERO_EXPONENT;
        } else if (exp >= 0) {
            long parts = getNormalizedParts(quotient, exp); // result has no fractional bits
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
            outRemainder.setImpl(0, ZERO_EXPONENT);
        } else {
            long shift = 1L << -exp;
            long intQutot = quotient / shift;
            long truncQuot = intQutot * shift * otherSignificand; //truncated to integer at same scale
            long parts = getLongParts(intQutot);
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
            long outParts = getNormalizedParts(origSig - truncQuot, (long) (exponent - INT_MAX_BITS));
            outRemainder.significand = (int) (outParts >> INT_MAX_BITS);
            outRemainder.exponent = (int) outParts;
        }
        return this;
    }

    public Float32ExpChainedExpression floor() {
        if (exponent < (-EXPONENT_BIAS - 1)) {
            if (significand >= 0) {
                significand = 0;
                exponent = ZERO_EXPONENT;
            } else {
                significand = 0x80000000; // -1
                exponent = -EXPONENT_BIAS - 1;
            }
        } else if (exponent < 0) {
            significand = significand >> -exponent << -exponent;
            if (significand == 0) {
                exponent = ZERO_EXPONENT;
            }
        }
        return this;
    }

    public Float32ExpChainedExpression floor(IFloat32Exp val) {return floorImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression floor(long val) {return floorImpl(getLongParts(val));}
    public Float32ExpChainedExpression floor(double val) {return floorImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression floorImpl(long parts) {return floorImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression floorImpl(int otherSignificand, int otherExponent) {
        if (otherSignificand == 0x80000000) { //if param is negative, then negate it.
            otherSignificand = 0x40000000;
            otherExponent += 1;
        } else if (otherSignificand < 0) {
            otherSignificand = -otherSignificand;
        }
        //this.subtract(new Float32Exp(this).modulo(other))
        long exp = ((long) exponent) - otherExponent - INT_MAX_BITS;
        long origSig = ((long) significand) << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            long parts = getNormalizedParts(quotient, exp); // result has no fractional bits
            subtractImpl((int) (parts >> INT_MAX_BITS), (int) parts);
        } else if (exp < 0) {
            long shift = 1L << -exp;
            long truncQuot = quotient / shift * shift * otherSignificand; //truncated to integer at same scale
            long parts = getNormalizedParts(origSig - truncQuot, (long) (exponent - INT_MAX_BITS));
            subtractImpl((int) (parts >> INT_MAX_BITS), (int) parts);
        } //otherwise it was already a multiple. do nothing.
        return this;
    }

    public Float32ExpChainedExpression round() {
        if (exponent < (-EXPONENT_BIAS - 1)) { //entirely fractional bits. Round to zero
            significand = 0;
            exponent = ZERO_EXPONENT;
        } else if (exponent < 0) { // some fractional bits. Add 0.5, then floor it
            addImpl(0x40000000, -EXPONENT_BIAS - 1);
            if (exponent >= -EXPONENT_BIAS) {
                significand = significand >> -exponent << -exponent; //floor
            } else { // [0 to -1] rounds the wrong way. Force it the right way
                significand = 0x80000000;
                exponent = -EXPONENT_BIAS - 1;
            }
        } //else no fractional bits
        return this;
    }

    public Float32ExpChainedExpression round(IFloat32Exp val) {return roundImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression round(long val) {return roundImpl(getLongParts(val));}
    public Float32ExpChainedExpression round(double val) {return roundImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression roundImpl(long parts) {return roundImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression roundImpl(int otherSignificand, int otherExponent) {
        int origSig = significand;
        int origExp = exponent;
        boolean rollback = true;
        try {
            addImpl(otherSignificand, otherExponent - 1);
            floorImpl(otherSignificand, otherExponent);
            rollback = false;
        } finally {
            if (rollback) {
                significand = origSig;
                exponent = origExp;
            }
        }
        return this;
    }

    //this^other can be defined as pow(2,other*log(this,2)),
    //intermediates probably require extra precision
    //http://www.netlib.org/fdlibm/e_pow.c for edge cases and notes
    public Float32ExpChainedExpression pow(IFloat32Exp val) {powImpl(val.significand(), val.exponent()); return this;}
    public Float32ExpChainedExpression pow(long val) {powImpl(getLongParts(val)); return this;}
    public Float32ExpChainedExpression pow(double val) {powImpl(getDoubleParts(val)); return this;}
    private Float32ExpChainedExpression powImpl(long parts) {return powImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression powImpl(int otherSignificand, int otherExponent) {
        if (otherSignificand == 0) { //X^0 = 1
            significand = 0x40000000;
            exponent = -EXPONENT_BIAS;
        } else if (significand == 0) { //0^X = 0
            if (otherSignificand < 0) {
                throw new ArithmeticException("0 raised to a negative value");
            }
            //do nothing
        } else if (significand == 0x50000000 && exponent == -27) { //10^X
            long pow10 = getPowerOf10Parts(otherSignificand << otherExponent); //check for overflows
            significand = (int) (pow10 >> INT_MAX_BITS);
            exponent = (int) pow10;
        } else if (otherSignificand == 0x40000000 && otherExponent == -29 ) { //X^2 = X*X
            multiplyImpl(significand, exponent);
        } else { //N^X
            final int origSig = significand;
            final int origExp = exponent;
            boolean rollback = true;
            try {
                boolean negateResult = false;
                if (significand < 0) {
                    //negative base requires integer exponent, so this throws if other isn't an integer.
                    //if it passes, this returns true if the result will be negative.
                    negateResult = verifyNegativeSig(otherSignificand, otherExponent);
                    //This knowledge allows us to make this always positive, so which is a prereq of complex_pow
                    negate();
                }
                complex_pow(otherSignificand, otherExponent);
                if (negateResult) {
                    negate();
                }
                rollback = false;
            } finally {
                if (rollback) {
                    significand = origSig;
                    exponent = origExp;
                }
            }
        }
        return this;
    }

    // this.pow(other) = this.log2().multiply(other).pow2()
    private void complex_pow(int otherSignificand, int otherExponent) {
        //log2()
        long integer_bits = exponent + EXPONENT_BIAS;
        double pre_log_fract_double = significand * Math.pow(2, -31);
        double post_log_fract_double = Math.log(pre_log_fract_double) / Math.log(2) + 1;
        double log2 = post_log_fract_double + integer_bits;
        //multiply(other)
        double mult = log2 * otherSignificand * Math.pow(2,otherExponent);
        //pow2()
        long exp = (long) mult;
        double pre_pow_fract_double = mult - exp;
        double post_pow_fract_double = Math.pow(2, pre_pow_fract_double);
        //assign
        long bits = getDoubleParts(post_pow_fract_double);
        long exponent = (int) bits + exp;
        if (exponent > Integer.MAX_VALUE) {
            throw new ArithmeticException("Exponent " + exponent + " out of range");
        }
        this.significand = (int) (bits >> INT_MAX_BITS);
        this.exponent = (int) exponent;
    }

    private boolean verifyNegativeSig(int otherSignificand, int otherExponent) {
        if (otherExponent <= -INT_MAX_BITS) { //exponent definitely not integer
            throw new IllegalArgumentException("exponent for negative base must be an integer");
        } else if (otherExponent <= 0) { //exponent might not be integer
            int diff = INT_MAX_BITS + otherExponent;
            if ((otherSignificand << diff) != 0) { //exponent isn't integer
                throw new IllegalArgumentException("exponent for negative base must be an integer");
            }
            if ((otherSignificand << (diff - 1)) == 0x80000000) { //exponent is odd
                return true;
            }
        }
        return false;
    }

    public Float32ExpChainedExpression pow2() {
        if (exponent > 0) {
            StringBuilder sb = new StringBuilder();
            toString(sb).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        } else if (significand == 0 && exponent == ZERO_EXPONENT) {
            setImpl(1 << (INT_MAX_BITS - 2), -EXPONENT_BIAS);
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
        //assign
        long bits = getDoubleParts(post_fraction_double);
        this.significand = (int) (bits >> INT_MAX_BITS);
        this.exponent = integer_part + (int) bits;
        return this;
    }

    public Float32ExpChainedExpression log2i() {
        if (significand <= 0) {
            throw new IllegalArgumentException("nonpositive value" + this);
        }
        long parts = getLongParts((long)(exponent)+EXPONENT_BIAS);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
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
            long parts = getLongParts(value);
            this.significand = (int) (parts >> INT_MAX_BITS);
            this.exponent = (int) parts;
            return this;
        }
        long integer_bits = exponent + EXPONENT_BIAS;
        double pre_fractional_double = significand * Math.pow(2,-31);
        double post_fractional_double = Math.log(pre_fractional_double)/Math.log(2) + 1;
        setImpl(getDoubleParts(post_fractional_double));
        addImpl(getLongParts(integer_bits));
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

    public Float32ExpChainedExpression shiftLeft(int val) {
        long parts = getNormalizedParts((long) significand, ((long) exponent) + val);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;}
    public Float32ExpChainedExpression shiftRight(int val) {
        long parts = getNormalizedParts((long) significand, ((long) exponent) - val);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
        return this;}

    public Float32ExpChainedExpression min(IFloat32Exp val) {return minImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression min(long val) {return minImpl(getLongParts(val));}
    public Float32ExpChainedExpression min(double val) {return minImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression minImpl(long parts) {return minImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression minImpl(int otherSignificand, int otherExponent) {
        if (exponent > otherExponent ||
                (exponent == otherExponent && significand > otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
        return this;
    }

    public Float32ExpChainedExpression max(IFloat32Exp val) {return maxImpl(val.significand(), val.exponent());}
    public Float32ExpChainedExpression max(long val) {return maxImpl(getLongParts(val));}
    public Float32ExpChainedExpression max(double val) {return maxImpl(getDoubleParts(val));}
    private Float32ExpChainedExpression maxImpl(long parts) {return maxImpl((int)(parts >> INT_MAX_BITS), (int)parts);}
    private Float32ExpChainedExpression maxImpl(int otherSignificand, int otherExponent) {
        if (exponent < otherExponent ||
                (exponent == otherExponent && significand < otherSignificand)) {
            significand = otherSignificand;
            exponent = otherExponent;
        }
        return this;
    }
}
