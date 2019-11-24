package com.tbohne.util.math;

import com.tbohne.util.Assert;
import com.tbohne.util.math.IFloat32ExpL.ExponentToStringInterface;
import com.tbohne.util.math.IFloat32ExpL.StringFormatParams;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is the shared logic of Float32ExpL and ImmutableFloat32ExpL.
 */
public class Float32ExpLHelpers {
    static final int INT_MAX_BITS = 32;
    private static final int EXPONENT_BIAS = 30;
    private static final int ZERO_EXPONENT = Integer.MIN_VALUE;
    private static final double INV_LOG10 = 0.30102999566398114; // 1/lg(10)
    private static final boolean INTERNAL_ASSERTS = false;

    static final long ZERO_PATTERN = (ZERO_EXPONENT & 0xFFFFFFFFL);
    private static final long HALF_PATTERN = getDoubleParts(0.5);
    private static final long ONE_PATTERN = getLongParts(1);
    private static final long TWO_PATTERN = getLongParts(2);
    private static final long TEN_PATTERN = getLongParts(10);
    private static final long NEG_ONE_PATTERN = getLongParts(-1);

    public static class DefaultExponentToString implements ExponentToStringInterface {
        @Override
        public void addExponent(StringBuilder stringBuilder, int exponent) {
            if (exponent != 0) {
                stringBuilder.append('E').append(exponent);
            }
        }
    }

    public static final DefaultExponentToString DEFAULT_EXPONENT_TO_STRING = new DefaultExponentToString();

    public static final int DEFAULT_MIN_PRECISION = 1;
    public static final int DEFAULT_MAX_PRECISION = 12;
    public static final int DEFAULT_STRING_EXPONENT_MULTIPLE = 1;
    public static final StringFormatParams DEFAULT_STRING_PARAMS = new StringFormatParams(DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION, DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);

    public static final int ENG_MIN_PRECISION = 3;
    public static final int ENG_MAX_PRECISION = 12;
    public static final int ENG_STRING_EXPONENT_MULTIPLE = 3;
    public static final StringFormatParams ENG_STRING_PARAMS = new StringFormatParams(ENG_MIN_PRECISION, ENG_MAX_PRECISION, ENG_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);

    private static final int[] pow10s = new int[]{
            0x50000000, -27, //1e1
            0x64000000, -24, //1e2
            0x4E200000, -17, //1e4
            0x5F5E1000, -4, //1e8
            0x470DE4DF, 23, //1e16
            0x4EE2D6D4, 76, //1e32
            0x613C0FA4, 182, //1e64
            0x49DD23E4, 395, //1e128
            0x553F75FD, 820, //1e256
            0x718CD057, 1670, //1e512
            0x64BB3AC3, 3371, //1e1024
            0x4F459DAE, 6773, //1e2048
            0x62302901, 13576, //1e4096
            0x4B51D0E8, 27183, //1e8192
            0x58A42A38, 54396, //1e16384
            0x7AC51935, 108822, //1e32768
            0x75C0E78C, 217675, //1e65536
            0x6C53CA05, 435381, //1e131072
            0x5BAD9BC3, 870793, //1e262144
            0x41A9C4F1, 1741617, //1e524288
            0x435E9A61, 3483264, //1e1048576
            0x46EA9F12, 6966558, //1e2097152
            0x4E949814, 13933146, //1e4194304
            0x607B8BB3, 27866322, //1e8388608
            0x48B9C8CD, 55732675, //1e16777216
            0x52A41F1C, 111465380, //1e33554432
            0x6AB63498, 222930790, //1e67108864
            0x58F6CA7A, 445861611, //1e134217728
            0x7BAA6478, 891723252, //1e268435456
            0x777A5BEB, 1783446535, //1e536870912
    };

    public static long cast(char[] in, int offset, int len) {
        //TODO Parse HexString
        int end = offset + len;
        boolean negative = false;
        long sigDec = 0; //initial digits
        int sig10Offset = 0; //times pow(10, sig10Offset)
        long sig2Offset = 0; //times pow(2, sig10Offset)
        //sign
        if (offset < end && in[offset] == '-') {
            negative = true;
            ++offset;
        }
        //parse initial easy digits into sigDec
        while(offset < end && Character.isDigit(in[offset]) && sigDec <= Integer.MAX_VALUE) {
            sigDec = sigDec * 10 + (in[offset] - '0');
            ++offset;
        }
        //If there's more digits, just ignore them, and increase sig10Offset
        while(offset < end && Character.isDigit(in[offset])) {
            ++sig10Offset;
            ++offset;
        }
        //If there's a decimal point, keep going and decrease sig10Offset
        if (offset < end && in[offset] == '.') {
            ++offset;
            while(offset < end && Character.isDigit(in[offset]) && sigDec <= Integer.MAX_VALUE) {
                sigDec = sigDec * 10 + (in[offset] - '0');
                --sig10Offset;
                ++offset;
            }
        }
        //If there's more digits, just ignore them
        while(offset < end && Character.isDigit(in[offset])) {
            ++offset;
        }
        //If there's an e-notation suffix for base10 or base2, then increase those
        if (offset < end && (in[offset] == 'e' || in[offset] == 'E')) {
            ++offset;
            sig10Offset += Integer.parseInt(new String(in, offset, end-offset));
        } else if (offset < end && (in[offset] == 'b' || in[offset] == 'B')) {
            ++offset;
            sig2Offset = Long.parseLong(new String(in, offset, end-offset));
        }
        //assign the initial digits and take care of easy base2 power
        if (negative) {
            sigDec = -sigDec;
        }
        long parts1 = getNormalizedParts(sigDec, sig2Offset);
        long significand = parts1 >> INT_MAX_BITS;
        long exponent = (int) parts1;
        // if there's a base10 power, adjust for that
        if (sig10Offset != 0) {
            long pow10parts = getPowerOf10Parts(sig10Offset);
            long scaleSig = pow10parts >> INT_MAX_BITS;
            long scaleExp = (int) pow10parts;
            long parts = getNormalizedParts(significand * scaleSig, scaleExp + exponent);
            significand = parts >> INT_MAX_BITS;
            exponent = (int) parts;
        }
        return assembleParts(significand, exponent);
    }

    public static long cast(BigDecimal val) {
        long value = cast(val.unscaledValue());
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        long pow10parts = getPowerOf10Parts(val.scale());
        long scaleSig = pow10parts >> INT_MAX_BITS;
        long scaleExp = (int) pow10parts;
        long newSig = significand << (INT_MAX_BITS-2);
        newSig /= scaleSig;
        return getNormalizedParts(newSig, ((long) exponent) - scaleExp - EXPONENT_BIAS);
    }

    public static long cast(BigInteger val) {
        int bits = val.bitLength() - INT_MAX_BITS + 1;
        if (bits < 0) {
            return getLongParts(val.longValue());
        } else {
            val = val.shiftRight(bits);
            return getNormalizedParts((long) val.intValue(), (long) bits);
        }
    }

    public static long getPowerOf10Parts(int initial_exp) {
        boolean negative = initial_exp < 0;
        initial_exp = negative ? -initial_exp : initial_exp;
        long significand = 0x40000000; //initialize to 1.0
        long exponent = -EXPONENT_BIAS;
        if (initial_exp > (1<<(pow10s.length/2))) {
            throw new ArithmeticException("Power 10 Exponent " + initial_exp + " out of range");
        }
        for(int i = 0; i < INT_MAX_BITS; i++) {
            int mask = 1<<i;
            if ((initial_exp & mask) == mask) {
                long guess_sig = significand * pow10s[i * 2];
                long guess_exp = exponent + pow10s[i * 2 + 1];
                //normalize. We lose slight accuracy here, but without it we overflow.  :(
                long parts = getNormalizedParts(guess_sig, guess_exp);
                significand = (int) (parts >> INT_MAX_BITS);
                exponent = (int) parts;
            }
        }
        if (!negative) {
            return assembleParts(significand, exponent);
        } else {//for a negative power of ten, invert the the result (1/x)
            long guess_sig = 0x4000000000000000L / significand;
            long guess_exp = -EXPONENT_BIAS - INT_MAX_BITS - exponent;
            // and normalize _again_ :(
            return getNormalizedParts(guess_sig, guess_exp);
        }
    }

    public static long add(long value, long other) {
        long exponent = (int) value;
        long otherExponent = (int) other;
        if (exponent < otherExponent) {
            long ti = otherExponent; //swap exponents
            otherExponent = exponent;
            exponent = ti;
            long tl = other; //swap inputs
            other = value;
            value = tl;
        }
        long significand = value >> INT_MAX_BITS;
        long otherSignificand = other >> INT_MAX_BITS;
        long diff = exponent - otherExponent;
        if (diff < INT_MAX_BITS) {
            long l = (significand << diff) + otherSignificand;
            return getNormalizedParts(l, exponent - diff);
        } else {
            return value;
        }
    }

    public static long subtract(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent >= otherExponent) {
            long diff = exponent - otherExponent;
            if (diff < INT_MAX_BITS) {
                long l = (significand << diff) - otherSignificand;
                return getNormalizedParts(l, (long) otherExponent);
            } else {
                return value;
            }
        } else { // if (otherExponent > exponent)
            long diff = otherExponent - exponent;
            if (significand == 0) {
                return getNormalizedParts(-otherSignificand, otherExponent);
            } else if (diff < INT_MAX_BITS) {
                long l = significand - (otherSignificand << diff);
                return getNormalizedParts(l, exponent);
            } else {
                return other;
            }
        }
    }

    public static long multiply(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        return getNormalizedParts(significand * otherSignificand, exponent + otherExponent);
    }

    public static long divide(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        long sig = significand << INT_MAX_BITS;
        return getNormalizedParts(sig / otherSignificand, exponent - otherExponent - INT_MAX_BITS);
    }

    public static long muldiv(long value, long mul, long div) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long mulSignificand = mul >> INT_MAX_BITS;
        long mulExponent = (int) mul;
        long divSignificand = div >> INT_MAX_BITS;
        long divExponent = (int) div;
        return getNormalizedParts(significand * mulSignificand / divSignificand, exponent + mulExponent - divExponent);
    }

    public static long divideToIntegralValue(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        long exp = exponent - otherExponent - INT_MAX_BITS;
        long origSig = significand << INT_MAX_BITS;
        long quotient = origSig / otherSignificand; //regular division
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            return ZERO_PATTERN;
        } else if (exp >= 0) {
            return getNormalizedParts(quotient, exp); // result has no fractional bits
        } else {
            long shift = 1L << -exp;
            return getLongParts(quotient / shift); //truncated to integer at target scale
        }
    }

    public static long remainder(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        long exp = exponent - otherExponent - INT_MAX_BITS;
        long origSig = significand << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            return getNormalizedParts(quotient, exp); // result has no fractional bits
        } else if (exp >= 0) {
            return ZERO_PATTERN;
        } else {
            long shift = 1L << -exp;
            long truncQuot = quotient / shift * shift * otherSignificand; //truncated to integer at same scale
            return getNormalizedParts(origSig - truncQuot, exponent - INT_MAX_BITS);
        }
    }

    interface RemainderSettable {
        void setRemainder(long remainder);
    }
    public static long divideAndRemainder(long value, long other, RemainderSettable outRemainder) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        long exp = exponent - otherExponent - INT_MAX_BITS;
        long origSig = significand << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            outRemainder.setRemainder(value);
            return ZERO_PATTERN;
        } else if (exp >= 0) {
            outRemainder.setRemainder(ZERO_PATTERN);
            return getNormalizedParts(quotient, exp); // result has no fractional bits
        } else {
            long shift = 1L << -exp;
            long intQutot = quotient / shift;
            long truncQuot = intQutot * shift * otherSignificand; //truncated to integer at same scale
            long parts = getLongParts(intQutot);
            long outParts = getNormalizedParts(origSig - truncQuot, exponent - INT_MAX_BITS);
            outRemainder.setRemainder(outParts);
            return parts;
        }
    }

    public static long floor(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (exponent < (-EXPONENT_BIAS - 1)) {
            return significand >= 0 ? ZERO_PATTERN : NEG_ONE_PATTERN;
        } else if (exponent < 0) {
            significand = significand >> -exponent << -exponent;
            if (significand == 0) {
                exponent = ZERO_EXPONENT;
            }
            return assembleParts(significand, exponent);
        } else {
            return value;
        }
    }

    public static long floor(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (otherSignificand == 0x80000000) { //if param is negative, then negate it.
            otherSignificand = 0x40000000;
            otherExponent += 1;
        } else if (otherSignificand < 0) {
            otherSignificand = -otherSignificand;
        }
        //this.subtract(new Float32ExpL(this).modulo(other))
        long exp = exponent - otherExponent - INT_MAX_BITS;
        long origSig = significand << INT_MAX_BITS;
        long quotient = origSig / otherSignificand;
        if (quotient == 0 || exp < -(INT_MAX_BITS*2)) {
            long parts = getNormalizedParts(quotient, exp); // result has no fractional bits
            return subtract(value, parts);
        } else if (exp < 0) {
            long shift = 1L << -exp;
            long truncQuot = quotient / shift * shift * otherSignificand; //truncated to integer at same scale
            long parts = getNormalizedParts(origSig - truncQuot, exponent - INT_MAX_BITS);
            return subtract(value, parts);
        } else {
            return value; //otherwise it was already a multiple. do nothing.
        }
    }

    public static long round(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (exponent < (-EXPONENT_BIAS - 1)) { //entirely fractional bits. Round to zero
            return ZERO_PATTERN;
        } else if (exponent < 0) { // some fractional bits. Add 0.5, then floor it
            value = add(value, HALF_PATTERN);
            significand = value >> INT_MAX_BITS;
            exponent = (int)value;
            if (exponent >= -EXPONENT_BIAS) {
                significand = significand >> -exponent << -exponent; //floor
            } else { // [0 to -1] rounds the wrong way. Force it the right way
                significand = 0x80000000;
                exponent = -EXPONENT_BIAS - 1;
            }
            return assembleParts(significand, exponent);
        } //else no fractional bits
        return value;
    }

    public static long round(long value, long other) {
        long halfSignificand = other >> INT_MAX_BITS;
        if (halfSignificand == 0) {
            throw new ArithmeticException("/ by zero");
        }
        // A normalized value will never trigger long underflow
        @SuppressWarnings("IntLongMath")
        long halfExponent = (int) other - 1;
        long half = assembleParts(halfSignificand, halfExponent);
        value = add(value, half);
        return floor(value, other);
    }

    public static long pow(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        int otherSignificand = (int) (other >> INT_MAX_BITS);
        int otherExponent = (int) other;
        if (other == ZERO_PATTERN) { //X^0 = 1
            return ONE_PATTERN;
        } else if (value == ZERO_PATTERN) { //0^X = 0
            if (otherSignificand < 0) {
                throw new ArithmeticException("0 raised to a negative value");
            }
            return value; //do nothing
        } else if (value == TEN_PATTERN) { //10^X
            long pow10exp = ((long) otherSignificand) << otherExponent;
            if (pow10exp > (1<<(pow10s.length/2))) {
                throw new ArithmeticException("Power 10 Exponent " + pow10exp + " out of range");
            }
            return getPowerOf10Parts((int) pow10exp);
        } else if (other == TWO_PATTERN) { //X^2 = X*X
            return multiply(value, value);
        } else { //N^X
            boolean negateResult = false;
            if (significand < 0) {
                //negative base requires integer exponent, so this throws if other isn't an integer.
                //if it passes, this returns true if the result will be negative.
                negateResult = verifyNegativeSig(otherSignificand, otherExponent);
                //This knowledge allows us to make this always positive, so which is a prereq of complex_pow
                value = negate(value);
            }
            value = complex_pow(value, other);
            if (negateResult) {
                return negate(value);
            }
            return value;
        }
    }

    // this.pow(other) = this.log2().multiply(other).pow2()
    public static long complex_pow(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
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
        significand = bits >> INT_MAX_BITS;
        exponent = (int) bits + exp;
        if (exponent > Integer.MAX_VALUE) {
            throw new ArithmeticException("Exponent " + exponent + " out of range");
        }
        return assembleParts(significand, exponent);
    }

    private static boolean verifyNegativeSig(int otherSignificand, int otherExponent) {
        if (otherExponent <= -INT_MAX_BITS) { //exponent definitely not integer
            throw new IllegalArgumentException("exponent for negative base must be an integer");
        } else if (otherExponent <= 0) { //exponent might not be integer
            long diff = INT_MAX_BITS + (long) otherExponent;
            if ((otherSignificand << diff) != 0) { //exponent isn't integer
                throw new IllegalArgumentException("exponent for negative base must be an integer");
            }
            if ((otherSignificand << (diff - 1)) == 0x80000000) { //exponent is odd
                return true;
            }
            return false;
        } else /* exponent definitely integer */ {
            return false;
        }
    }

    public static long pow2(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        if (exponent > 0) {
            StringBuilder sb = new StringBuilder();
            toString(value, sb, DEFAULT_STRING_PARAMS).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        } else if (significand == 0 && exponent == ZERO_EXPONENT) {
            significand = 1 << (INT_MAX_BITS - 2);
            exponent = -EXPONENT_BIAS;
            return assembleParts(significand, exponent);
        }
        int integer_part;
        double pre_fractional_double;
        if (exponent > -INT_MAX_BITS) {
            int fractional_bits = -exponent;
            int int_bits = INT_MAX_BITS - fractional_bits - 1;
            integer_part = (int) (significand >> fractional_bits);
            long pre_fraction_long = (significand & ((1 << fractional_bits) - 1)) << int_bits;
            pre_fractional_double = pre_fraction_long * Math.pow(2, -31);
        } else {
            integer_part = 0;
            pre_fractional_double = significand * Math.pow(2,exponent);
        }
        double post_fraction_double = Math.pow(2, pre_fractional_double);
        //assign
        long bits = getDoubleParts(post_fraction_double);
        significand = (int) (bits >> INT_MAX_BITS);
        //TODO: Check for overflows
        exponent = integer_part + (int) bits;
        return assembleParts(significand, exponent);
    }

    public static long log2i(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (significand <= 0) {
            StringBuilder builder = new StringBuilder("nonpositive value ");
            toString(value, builder, DEFAULT_STRING_PARAMS);
            throw new IllegalArgumentException(builder.toString());
        }
        return getLongParts(exponent + EXPONENT_BIAS);
    }

    public static long log2(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (significand <= 0) {
            StringBuilder builder = new StringBuilder("nonpositive value ");
            toString(value, builder, DEFAULT_STRING_PARAMS);
            throw new IllegalArgumentException(builder.toString());
        } else if (value == ONE_PATTERN) { // lg(1) == 0.0
            return ZERO_PATTERN;
        } else if (significand == 0x40000000) {// lg(power-of-2) == exponent
            value = exponent + EXPONENT_BIAS;
            return getLongParts(value);
        }
        long integer_bits = exponent + EXPONENT_BIAS;
        double pre_fractional_double = significand * Math.pow(2,-31);
        double post_fractional_double = Math.log(pre_fractional_double)/Math.log(2) + 1;
        value = getDoubleParts(post_fractional_double);
        value = add(value, getLongParts(integer_bits));
        if (INTERNAL_ASSERTS) {
            significand = value >> INT_MAX_BITS;
            exponent = (int)value;
            double max = Double.MAX_VALUE / significand;
            double maxExp = Math.log(max)/Math.log(2);
            if (exponent < maxExp) {
                assertApproximately(significand * Math.pow(2, exponent), value, 30);
            }
        }
        return value;
    }

    public static long abs(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (significand == 0x80000000) {
            significand = 0x40000000;
            exponent += 1;
        } else if (significand < 0) {
            significand = -significand;
        }
        return assembleParts(significand, exponent);
    }

    public static long negate(long value) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        if (significand == 0x40000000) {
            significand = 0x80000000;
            exponent -=1;
        } else if (significand == 0x80000000) {
            significand = 0x40000000;
            exponent += 1;
        } else{
            significand = -significand;
        }
        return assembleParts(significand, exponent);
    }

    public static long shiftLeft(long value, int bits) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        return getNormalizedParts(significand, exponent + bits);
    }

    public static long shiftRight(long value, int bits) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        return getNormalizedParts(significand, exponent - bits);
    }

    public static long min(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent > otherExponent ||
                (exponent == otherExponent && significand > otherSignificand)) {
            return other;
        }
        return value;
    }

    public static long max(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent < otherExponent ||
                (exponent == otherExponent && significand < otherSignificand)) {
            return other;
        }
        return value;
    }

    public static int signum(long value) {
        int significand = (int) (value >> INT_MAX_BITS);
        return Integer.compare(significand, 0);
    }

    public static boolean isZero(long value) {
        return value == ZERO_PATTERN;
    }

    public static boolean equals(long value, Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof IFloat32ExpL) {
            return value == ((IFloat32ExpL)object).getParts();
        }
        if (object instanceof Integer) {
            return value == getLongParts((Integer)object);
        }
        if (object instanceof Long) {
            return value == getLongParts((Long)object);
        }
        if (object instanceof Float) {
            return value == getDoubleParts((Float)object);
        }
        if (object instanceof Double) {
            return value == getDoubleParts((Double)object);
        }
        return false;
    }

    /*
     * Returns true if the values are within [bitsSimilarCount] bits of each other.
     */
    public static boolean approximately(long value, long other, int bitsSimilarCount) {
        if (bitsSimilarCount < 0) {
            throw new IllegalArgumentException("bitsSimilarCount("+bitsSimilarCount+") must be at least 0");
        } else if (bitsSimilarCount > 31) {
            throw new IllegalArgumentException("bitsSimilarCount("+bitsSimilarCount+") must be at most 31");
        }
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        long max = otherSignificand + (1L << (INT_MAX_BITS - bitsSimilarCount));
        long min = otherSignificand - (1L << (INT_MAX_BITS - bitsSimilarCount));
        if (exponent > otherExponent) {
            if (exponent - otherExponent != 1) {
                return false;
            }
            min >>= 1;
            max >>= 1;
        } else if (exponent < otherExponent) {
            if (otherExponent - exponent != 1) {
                return false;
            }
            significand <<= 1;
        }
        return significand >= min && significand <= max;
    }

    public static int compareTo(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent != otherExponent) {
            return exponent > otherExponent ? 1 : -1;
        } else if (significand != otherSignificand) {
            return significand > otherSignificand ? 1 : -1;
        } else {
            return 0;
        }
    }

    public static boolean lessThan(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand < otherSignificand;
    }

    public static boolean lessOrEquals(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        int otherExponent = (int) other;
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand <= otherSignificand;
    }

    public static boolean greaterOrEquals(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent != otherExponent) {
            return exponent > otherExponent;
        }
        return significand >= otherSignificand;
    }

    public static boolean greaterThan(long value, long other) {
        long significand = value >> INT_MAX_BITS;
        long exponent = (int)value;
        long otherSignificand = other >> INT_MAX_BITS;
        long otherExponent = (int) other;
        if (exponent != otherExponent) {
            return exponent > otherExponent;
        }
        return significand > otherSignificand;
    }

    public static StringBuilder toString(long value, StringBuilder sb, StringFormatParams params) {
        int exponent = (int) value;
        int workingSig = (int) (value >> INT_MAX_BITS);
        int workingExp = (int) value;
        if (workingSig < 0) {
            sb.append('-');
            if (workingSig == 0x80000000) {
                workingSig = 0x40000000;
                workingExp += 1;
            } else{
                workingSig = -workingSig;
            }
        }
        long base10Exp = 0; //initialize with results for 0
        long minSig = 0;
        long maxSig = 0;
        long oneSig = 1;
        if (workingSig != 0) {
            //find next smallest power of 10
            base10Exp =  (long) ((((long) exponent) + EXPONENT_BIAS) * INV_LOG10);
            if (base10Exp > Integer.MAX_VALUE) {
                return sb.append("INF");
            }
            long pow10Parts = getPowerOf10Parts((int) base10Exp);
            long pow10Sig = pow10Parts >> INT_MAX_BITS;
            long pow10Exp = (int) pow10Parts;
            //inlined division of this/pow10, with round_half_up. should result in-ish to [1-10) with some exponent
            //TODO: for Float32ExpL(512), this ends up one bit too small, even with rounding. Why?
            long longSig = (((long) workingSig) << (INT_MAX_BITS - 1)) + (pow10Sig / 2);
            longSig = (longSig / pow10Sig) << 1;
            int longExp = (int) (workingExp - pow10Exp - EXPONENT_BIAS - 2);
            //find 1.0 and 10.0 at this same exponent
            int expDiff = EXPONENT_BIAS + longExp + 32;
            oneSig = 0x4000000000000000L >> expDiff; //this is 1.0 using the same longExp exponent
            // bring the value into [1-10) if we missed up above.
            if (longSig >= oneSig * 10) {
                oneSig *= 10;
                ++base10Exp;
                if (INTERNAL_ASSERTS) {
                    Assert.assertLess(oneSig * 10, longSig);
                }
            } else if (longSig < oneSig) {
                longSig *= 10;
                --base10Exp;
                if (INTERNAL_ASSERTS) {
                    Assert.assertAtLeast(oneSig, longSig);
                }
            }
            maxSig = longSig + 1; //longSig was crafted to have an unused bit on the right
            minSig = longSig - 1; //giving us room for interact with "an extra bit" of rounding
        }
        return toStringImpl(sb,
                params,
                (int) base10Exp,
                minSig,
                maxSig,
                oneSig);
    }

    private static StringBuilder toStringImpl(StringBuilder sb,
            StringFormatParams params,
            int base10Exp,
            long minSig,
            long maxSig,
            long oneSig) {
        //calculate display exponent and digit counts
        int digitsBeforeDecimal = base10Exp % params.exponentMultiple + 1;
        int displayExponent = base10Exp - digitsBeforeDecimal + 1;
        int minDigitsAfterDecimal = params.minDigits - digitsBeforeDecimal;
        int maxDigitsAfterDecimal = params.maxDigits - digitsBeforeDecimal;
        //show digits before decimal
        while(digitsBeforeDecimal > 0) {
            char minDigit = (char) (minSig /  oneSig);
            char maxDigit = (char) (maxSig /  oneSig);
            sb.append((char) ('0' + maxDigit));
            if (minDigit == maxDigit) {
                minSig = minSig % oneSig * 10;
                maxSig = maxSig % oneSig * 10;
            } else {
                maxSig = 0;
            }
            --digitsBeforeDecimal;
        }
        if (minDigitsAfterDecimal > 0 ||
                (maxDigitsAfterDecimal > 0 && maxSig != 0)) {
            sb.append('.');
        }
        //show digits after decimal
        while(minDigitsAfterDecimal > 0 ||
                (maxDigitsAfterDecimal > 0 && maxSig != 0)) {
            char minDigit = (char) (minSig /  oneSig);
            char maxDigit = (char) (maxSig /  oneSig);
            sb.append((char) ('0' + maxDigit));
            if (minDigit == maxDigit) {
                minSig = minSig % oneSig * 10;
                maxSig = maxSig % oneSig * 10;
            } else {
                maxSig = 0;
            }
            --minDigitsAfterDecimal;
            --maxDigitsAfterDecimal;
        }
        //show exponent
        params.exponentToString.addExponent(sb, displayExponent);
        return sb;
    }

    public static StringBuilder toBNotationString(long value, StringBuilder sb) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        return sb.append(significand).append('B').append(exponent);
    }

    public static StringBuilder toHexString(long value, StringBuilder sb) {
        int significand = (int) (value >> INT_MAX_BITS);
        int exponent = (int)value;
        return sb.append("0x")
                .append(Integer.toHexString(significand))
                .append('P')
                .append(Integer.toHexString(exponent));
    }

    /**
     * Returns this {@code Float32ExpL} as a big integer instance. A fractional
     * part is discarded.
     */
    public static BigInteger toBigInteger(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        if (exponent > 0) {
            return BigInteger.valueOf(significand).shiftLeft(exponent);
        } else {
            return BigInteger.valueOf(significand).shiftRight(exponent);
        }
    }

    /**
     * Returns this {@code Float32ExpL} as a big decimal instance.
     */
    public static BigDecimal toBigDecimal(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        return BigDecimal.valueOf(significand).multiply(BigDecimal.TEN.pow(exponent));
    }

    public static int intValue(long value) {
        int significand = (int) (value >> INT_MAX_BITS);
        int exponent = (int)value;
        if (significand == 0) {
            return 0;
        } else if (exponent > 0 || exponent <= -INT_MAX_BITS) {
            StringBuilder sb = new StringBuilder();
            toString(value, sb, DEFAULT_STRING_PARAMS).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        }
        return significand >> -exponent;
    }

    public static long longValue(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        if (significand == 0) {
            return 0;
        } else if (exponent >= INT_MAX_BITS || exponent <= -INT_MAX_BITS) {
            StringBuilder sb = new StringBuilder();
            toString(value, sb, DEFAULT_STRING_PARAMS).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        } else if (exponent < 0) {
            return significand >> -exponent;
        } else { //if exponent > 0
            return significand << exponent;
        }
    }

    public static float floatValue(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
      return (float)(significand * Math.pow(2,exponent));
    }

    public static double doubleValue(long value) {
        long significand = value >> INT_MAX_BITS;
        int exponent = (int)value;
        return significand * Math.pow(2,exponent);
    }

    public static long getNormalizedParts(long significand, long exponent) {
        if (significand == 0) {
            return ZERO_PATTERN;
        }
        long bitpattern = significand >= 0 ? significand : ~significand;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        int shiftLeft = zeroes - INT_MAX_BITS - 1;
        if (shiftLeft > 0) {
            significand = (int)(significand << shiftLeft);
        } else {
            significand = (int) (significand >> -shiftLeft);
        }
        exponent += INT_MAX_BITS + 1 - zeroes;
        return assembleParts(significand, exponent);
    }

    public static long assembleParts(long significand, long exponent) {
        if (exponent > Integer.MAX_VALUE || exponent < Integer.MIN_VALUE) {
            throw new ArithmeticException("Exponent " + exponent + " out of range");
        }
        if (INTERNAL_ASSERTS) {
            assertNormalized(significand, exponent);
        }
        return (significand << INT_MAX_BITS) | (exponent & 0xFFFFFFFFL);
    }

    public static long getLongParts(long v) {
        if (v == 0) {
            return ZERO_PATTERN;
        }
        long bitpattern = v >= 0 ? v : ~v;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        int exp = INT_MAX_BITS + 1 - zeroes;
        long sig = (v << (-exp + INT_MAX_BITS)) & 0xFFFFFFFF00000000L;
        return sig | (exp & 0xFFFFFFFFL);
    }

    public static long getDoubleParts(double val) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            throw new UnsupportedOperationException("Float32ExpL doesn't support INF or NAN");
        }
        if (val == 0.0) {
            return ZERO_PATTERN;
        }
        long bits = Double.doubleToRawLongBits(val);
        int exponent_bits = (int) ((bits & 0x7ff0000000000000L) >> 52);
        long mantissa_bits = bits & 0x000fffffffffffffL;
        int mantissa_value = (int) (mantissa_bits >> 22 | 0x40000000);
        long sig;
        int exp;
        if (val >= Double.MIN_NORMAL) { //regular positive number
            sig = mantissa_value;
            exp = exponent_bits - 1024 - 29;
        } else if (val <= -Double.MIN_NORMAL) { //regular negative number
            mantissa_value = -mantissa_value;
            int zeroes = Integer.numberOfLeadingZeros(~mantissa_value);
            sig = ((long) mantissa_value) << (zeroes - 1);
            exp = exponent_bits - 1024 - 28 - zeroes;
        } else if (val > 0){ //subnormal positive
            int zeroes = Long.numberOfLeadingZeros(mantissa_bits);
            if (zeroes < INT_MAX_BITS + 1) {
                sig = mantissa_bits >> (INT_MAX_BITS - zeroes + 1);
            } else {
                sig = mantissa_bits << (INT_MAX_BITS - zeroes - 3);
            }
            exp = -1041 - zeroes;
        } else { // subnormal negative
            long mantissa = -mantissa_bits;
            int zeroes = Long.numberOfLeadingZeros(~mantissa);
            if (zeroes < INT_MAX_BITS) {
                sig = mantissa >> (INT_MAX_BITS - zeroes + 1);
            } else {
                sig = mantissa << (zeroes - 1);
            }
            exp = -1041 - zeroes;
        }
        return assembleParts(sig, exp);
    }

    //TODO: Remove custom Assert dependency
    private static void assertNormalized(long significand, long exponent) {
        if (significand > Integer.MAX_VALUE || significand < Integer.MIN_VALUE) {
            Assert.fail("significand %s is out of range", significand);
        } else  if (exponent > Integer.MAX_VALUE || exponent < Integer.MIN_VALUE) {
            Assert.fail("exponent %s is out of range", exponent);
        } else if (significand > 0) {
            Assert.assertEqualsHex("MSB not set", significand | 0x40000000, significand);
        } else if (significand < 0) {
            Assert.assertEqualsHex("MSB not unset", significand & ~0x40000000, significand);
        } else {
            Assert.assertEqualsHex("zero has has wrong exponent", ZERO_EXPONENT, (int) exponent);
        }
    }

    public static void assertApproximately(long expected, long actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }


    public static void assertApproximately(String message, long expected, long actual, int bitsSimilarCount) {
        if(!approximately(actual, expected, bitsSimilarCount)) {
            Assert.failComparison("Expected approximately: ", message, expected, actual);
        }
    }

    public static void assertApproximately(double expected, long actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }

    public static void assertApproximately(String message, double expected, long actual, int bitsSimilarCount) {
        if(!approximately(actual, getDoubleParts(expected), bitsSimilarCount)) {
            Assert.failComparison("Expected approximately: ", message, expected, actual);
        }
    }
}
