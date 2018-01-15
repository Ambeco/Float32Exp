package com.tbohne.util.math;

import com.tbohne.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is the shared logic of Float64Exp and ImmutableFloat64Exp.
 */
/*package*/ class Float64ExpSharedBase extends Number implements IFloat64Exp {
    private static final long serialVersionUID = 1L;
    /*package*/ static final int INT_MAX_BITS = 32;
    /*package*/ static final int LONG_MAX_BITS = 64;
    /*package*/ static final int EXPONENT_BIAS = 30;
    /*package*/ static final int ZERO_EXPONENT = Integer.MIN_VALUE;
    private static final double INV_LOG10 = 0.30102999566398114; // 1/lg(10)
    /*package*/ static final boolean INTERNAL_ASSERTS = true;

    public static class DefaultExponentToString implements  ExponentToStringInterface {
        @Override
        public void addExponent(StringBuilder stringBuilder, int exponent) {
            if (exponent != 0) {
                stringBuilder.append('e').append(exponent);
            }
        }
    }
    public static final DefaultExponentToString DEFAULT_EXPONENT_TO_STRING = new DefaultExponentToString();

    public static final int DEFAULT_STRING_BASE = 10;
    public static final int DEFAULT_MIN_PRECISION = 1;
    public static final int DEFAULT_MAX_PRECISION = 12;
    public static final int DEFAULT_STRING_EXPONENT_MULTIPLE = 1;

    public static final int ENG_MIN_PRECISION = 3;
    public static final int ENG_MAX_PRECISION = 12;
    public static final int ENG_STRING_EXPONENT_MULTIPLE = 3;

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

    /*package*/ int significand;
    /*package*/ int exponent;

    /*package*/ Float64ExpSharedBase() {significand = 0; exponent = ZERO_EXPONENT;}

    /*package*/ IFloat64Exp set(char[] in, int offset, int len) {
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
        setNormalized(sigDec, sig2Offset);
        // if there's a base10 power, adjust for that
        if (sig10Offset != 0) {
            long pow10parts = getPowerOf10Parts(sig10Offset);
            long scaleSig = pow10parts >> INT_MAX_BITS;
            long scaleExp = (int) pow10parts;
            setNormalized(((long) significand) * scaleSig, scaleExp + exponent);
        }
        return this;
    }

    /*package*/ IFloat64Exp set(BigDecimal val) {
        set(val.unscaledValue());
        long pow10parts = getPowerOf10Parts(val.scale());
        long scaleSig = pow10parts >> INT_MAX_BITS;
        long scaleExp = (int) pow10parts;
        long newSig = ((long) significand) << (INT_MAX_BITS-2);
        newSig /= scaleSig;
        setNormalized(newSig, ((long) exponent) - scaleExp - EXPONENT_BIAS);
        return this;
    }

    /*package*/ IFloat64Exp set(BigInteger val) {
        int bits = val.bitLength() - INT_MAX_BITS + 1;
        if (bits < 0) {
            setNormalized(val.intValue(), 0);
        } else {
            val = val.shiftRight(bits);
            setNormalized(val.intValue(), bits);
        }
        return this;
    }

    /*package*/ IFloat64Exp set(int significand, int exponent) {
        if (INTERNAL_ASSERTS) {
            assertNormalized(significand, exponent);
        }
        this.significand = significand;
        this.exponent = exponent;
        return this;
    }

    /*package*/ static long getPowerOf10Parts(int initial_exp) {
        boolean negative = initial_exp < 0;
        initial_exp = negative ? -initial_exp : initial_exp;
        long significand = 0x40000000; //initialize to 1.0
        long exponent = -EXPONENT_BIAS;
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
            return (significand << INT_MAX_BITS) | (exponent & 0xFFFFFFFFL);
        } else {//for a negative power of ten, invert the the result (1/x)
            long guess_sig = 0x4000000000000000L / significand;
            long guess_exp = -EXPONENT_BIAS - INT_MAX_BITS - exponent;
            // and normalize _again_ :(
            return getNormalizedParts(guess_sig, guess_exp);
        }
    }

    public int signum() {
        if (significand < 0) {
            return -1;
        } else if (significand == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public boolean isZero() {
        return significand == 0;
    }

    @Override
    public int significand() {
        return significand;
    }

    @Override
    public int exponent() {
        return exponent;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof IFloat64Exp) {
            IFloat64Exp other = (IFloat64Exp) object;
            return equals(other.significand(), other.exponent());
        }
        if (object instanceof Integer) {
            return equals(longToSignificand((Integer)object), longToExponent((Integer)object));
        }
        if (object instanceof Long) {
            return equals(longToSignificand((Long)object), longToExponent((Long)object));
        }
        if (object instanceof Float) {
            return equals(doubleToSignificand((Float)object), doubleToExponent((Float)object));
        }
        if (object instanceof Double) {
            return equals(doubleToSignificand((Double)object), doubleToExponent((Double)object));
        }
        return false;
    }
    public boolean equals(IFloat64Exp val) {
        return equals(val.significand(), val.exponent());
    }
    public boolean equals(long val) {
        return equals(longToSignificand(val), longToExponent(val));
    }
    public boolean equals(double val) {
        return equals(doubleToSignificand(val), doubleToExponent(val));
    }
    private boolean equals(int otherSignificand, int otherExponent) {
        return significand==otherSignificand && exponent == otherExponent;
    }

    /*
     * Returns true if the values are within ~22 bits of each other.
     */
    public boolean approximately(IFloat64Exp val, int bitsSimilarCount)
    {return approximately(val.significand(), val.exponent(), bitsSimilarCount);}
    public boolean approximately(long val, int bitsSimilarCount)
    {return approximately(longToSignificand(val), longToExponent(val), bitsSimilarCount);}
    public boolean approximately(double val, int bitsSimilarCount)
    {return approximately(doubleToSignificand(val), doubleToExponent(val), bitsSimilarCount);}
    private boolean approximately(long otherSignificand, int otherExponent, int bitsSimilarCount) {
        if (bitsSimilarCount < 0) {
            throw new IllegalArgumentException("bitsSimilarCount("+bitsSimilarCount+") must be at least 0");
        } else if (bitsSimilarCount > 31) {
            throw new IllegalArgumentException("bitsSimilarCount("+bitsSimilarCount+") must be at most 31");
        }
        long significand = this.significand;
        int exponent = this.exponent;
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

    @Override
    public int compareTo(IFloat64Exp val) {return compareTo(val.significand(), val.exponent());}
    public int compareTo(long val)
    {return compareTo(longToSignificand(val), longToExponent(val));}
    public int compareTo(double val)
    {return compareTo(doubleToSignificand(val), doubleToExponent(val));}
    private int compareTo(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent > otherExponent ? 1 : -1;
        } else if (significand != otherSignificand) {
            return significand > otherSignificand ? 1 : -1;
        } else {
            return 0;
        }
    }

    public boolean lessThan(IFloat64Exp val)
    {return lessThan(val.significand(), val.exponent());}
    public boolean lessThan(long val)
    {return lessThan(longToSignificand(val), longToExponent(val));}
    public boolean lessThan(double val)
    {return lessThan(doubleToSignificand(val), doubleToExponent(val));}
    private boolean lessThan(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand < otherSignificand;
    }

    public boolean lessOrEquals(IFloat64Exp val)
    {return lessOrEquals(val.significand(), val.exponent());}
    public boolean lessOrEquals(long val)
    {return lessOrEquals(longToSignificand(val), longToExponent(val));}
    public boolean lessOrEquals(double val)
    {return lessOrEquals(doubleToSignificand(val), doubleToExponent(val));}
    private boolean lessOrEquals(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand <= otherSignificand;
    }

    public boolean greaterOrEquals(IFloat64Exp val)
    {return greaterOrEquals(val.significand(), val.exponent());}
    public boolean greaterOrEquals(long val)
    {return greaterOrEquals(longToSignificand(val), longToExponent(val));}
    public boolean greaterOrEquals(double val)
    {return greaterOrEquals(doubleToSignificand(val), doubleToExponent(val));}
    private boolean greaterOrEquals(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent > otherExponent;
        }
        return significand >= otherSignificand;
    }

    public boolean greaterThan(IFloat64Exp val)
    {return greaterThan(val.significand(), val.exponent());}
    public boolean greaterThan(long val)
    {return greaterThan(longToSignificand(val), longToExponent(val));}
    public boolean greaterThan(double val)
    {return greaterThan(doubleToSignificand(val), doubleToExponent(val));}
    private boolean greaterThan(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent > otherExponent;
        }
        return significand > otherSignificand;
    }

    @Override
    public int hashCode() {
        return significand + 0x9e3779b9 + (exponent<<6) + (exponent>>2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb = toString(sb, DEFAULT_STRING_BASE, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }
    public StringBuilder toString(StringBuilder sb) {
        return toString(sb, DEFAULT_MIN_PRECISION, DEFAULT_MAX_PRECISION,
                DEFAULT_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
    }
    public String toEngineeringString() {
        StringBuilder sb = new StringBuilder();
        sb = toString(sb, ENG_MIN_PRECISION, ENG_MAX_PRECISION,
                ENG_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
        return sb.toString();
    }
    public StringBuilder toEngineeringString(StringBuilder sb) {
        return toString(sb, ENG_MIN_PRECISION, ENG_MAX_PRECISION,
                ENG_STRING_EXPONENT_MULTIPLE, DEFAULT_EXPONENT_TO_STRING);
    }
    public StringBuilder toString(StringBuilder sb, int min_digits, int max_digits, int exponentMultiple,
                                        ExponentToStringInterface exponentToString) {
        int workingSig = significand;
        int workingExp = exponent;if (workingSig < 0) {
            sb.append('-');
            if (workingSig == 0x80000000) {
                workingSig = 0x40000000;
                workingExp += 1;
            } else{
                workingSig = -workingSig;
            }
        }
        long base10Exp = 0;
        long longSig = 0;
        long oneSig = 1;
        if (workingSig != 0) {
            //find next smallest power of 10
            base10Exp = (long) ((exponent + EXPONENT_BIAS) * INV_LOG10);
            if (base10Exp > Integer.MAX_VALUE) {
                return sb.append("INF");
            }
            long pow10Parts = getPowerOf10Parts((int) base10Exp);
            long pow10Sig = pow10Parts >> INT_MAX_BITS;
            int pow10Exp = (int) pow10Parts;
            if (INTERNAL_ASSERTS && !lessOrEquals(workingSig, workingExp)) {
                Assert.fail("{}<={}", this, new Float64ExpSharedBase().set(significand, exponent));
            }
            //inlined division of this/pow10
            longSig = ((long) workingSig) << INT_MAX_BITS;
            longSig /= pow10Sig;
            int longExp = workingExp - pow10Exp - EXPONENT_BIAS - 2;
            int expDiff = EXPONENT_BIAS + longExp + 4;
            oneSig = 0x400000000L >> expDiff; //this is 1.0 using the same longExp exponent
            long maxSig = oneSig * 10; // This is 10.0 using this same longExp exponent
            // bring the value down to <maxSig. Should take 0 loops most of the time.
            while (longSig >= maxSig) {
                maxSig *= 10;
                oneSig *= 10;
                ++base10Exp;
            }
        }
        //calculate display exponent and digit counts
        int digitsBeforeDecimal = (int) base10Exp % exponentMultiple + 1;
        int displayExponent = (int) base10Exp - digitsBeforeDecimal + 1;
        int minDigitsAfterDecimal = min_digits - digitsBeforeDecimal;
        int maxDigitsAfterDecimal = max_digits - digitsBeforeDecimal;
        //show digits before decimal
        while(digitsBeforeDecimal > 0) {
            char digit = (char) (longSig /  oneSig);
            long remain = longSig % oneSig;
            sb.append((char) ('0' + digit));
            longSig = remain * 10;
            --digitsBeforeDecimal;
        }
        if (minDigitsAfterDecimal > 0 ||
                (maxDigitsAfterDecimal > 0 && longSig != 0)) {
            sb.append('.');
        }
        //show digits after decimal
        while(minDigitsAfterDecimal > 0 ||
                (maxDigitsAfterDecimal > 0 && longSig != 0)) {
            long digit = longSig /  oneSig;
            long remain = longSig % oneSig;
            sb.append((char)('0' + digit));
            longSig = remain * 10;
            --minDigitsAfterDecimal;
            --maxDigitsAfterDecimal;
        }
        //show exponent
        exponentToString.addExponent(sb, displayExponent);
        return sb;
    }

    public StringBuilder toBNotationString(StringBuilder sb) {
        return sb.append(significand).append('B').append(exponent);
    }

    public StringBuilder toHexString(StringBuilder sb) {
        return sb.append("0x")
                .append(Integer.toHexString(significand))
                .append('P')
                .append(Integer.toHexString(exponent));
    }

    /**
     * Returns this {@code Float64Exp} as a big integer instance. A fractional
     * part is discarded.
     */
    public BigInteger toBigInteger() {
        if (exponent > 0) {
            return BigInteger.valueOf(significand).shiftLeft(exponent);
        } else {
            return BigInteger.valueOf(significand).shiftRight(exponent);
        }
    }

    /**
     * Returns this {@code Float64Exp} as a big decimal instance.
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(significand).multiply(BigDecimal.TEN.pow(exponent));
    }

    @Override
    public ImmutableFloat64Exp toImmutable() {
        return new ImmutableFloat64Exp(significand, exponent);
    }

    @Override
    public int intValue() {
        if (significand == 0) {
            return 0;
        } else if (exponent > 0 || exponent <= -INT_MAX_BITS) {
            StringBuilder sb = new StringBuilder();
            toString(sb).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        }
        return significand >> -exponent;
    }

    @Override
    public long longValue() {
        if (significand == 0) {
            return 0;
        } else if (exponent >= INT_MAX_BITS || exponent <= -INT_MAX_BITS) {
            StringBuilder sb = new StringBuilder();
            toString(sb).append(" out of range");
            throw new IllegalArgumentException(sb.toString());
        } else if (exponent < 0) {
            return significand >> -exponent;
        } else { //if exponent > 0
            return ((long) significand) << exponent;
        }
    }

    @Override
    public float floatValue() {
      return (float)(significand * Math.pow(2,exponent));
    }

    @Override
    public double doubleValue() {
        return significand * Math.pow(2,exponent);
    }

    private static int shiftLeftToNormalize(long v) {
        long bitpattern = v >= 0 ? v : ~v;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        return zeroes - INT_MAX_BITS - 1;
    }

    private static long getNormalizedParts(long significand, long exponent) {
        if (significand == 0) {
            exponent = ZERO_EXPONENT;
        } else {
            long bitpattern = significand >= 0 ? significand : ~significand;
            int zeroes = Long.numberOfLeadingZeros(bitpattern);
            int shiftLeft = zeroes - INT_MAX_BITS - 1;
            if (shiftLeft > 0) {
                significand = (int)(significand << shiftLeft);
            } else {
                significand = (int) (significand >> -shiftLeft);
            }
            exponent += INT_MAX_BITS + 1 - zeroes;
            if (INTERNAL_ASSERTS) {
                assertNormalized((int) significand, exponent);
            }
            if (exponent > Integer.MAX_VALUE || exponent < Integer.MIN_VALUE) {
                throw new ArithmeticException("Exponent " + exponent + " out of range");
            }
        }
        return (significand << INT_MAX_BITS) | (exponent & 0xFFFFFFFFL);
    }

    /*package*/ void setNormalized(long v, long e) {
        long parts = getNormalizedParts(v, e);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
    }

    /*package*/ static int longToSignificand(long v) {
        int shiftLeft = shiftLeftToNormalize(v);
        if (shiftLeft >= 0) {
            return (int)(v << shiftLeft);
        } else {
            return (int) (v >> -shiftLeft);
        }
    }

    /*package*/ static int longToExponent(long v) {
        long bitpattern = v >= 0 ? v : ~v;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        if (v != 0) {
            return INT_MAX_BITS + 1 - zeroes;
        } else /*if (v == 0)*/ {
            return ZERO_EXPONENT;
        }
    }

    /*package*/ static int doubleToSignificand(double val) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            throw new UnsupportedOperationException("Float64Exp doesn't support INF or NAN");
        }
        if (val == 0.0) {
            return 0;
        }
        long bits = Double.doubleToRawLongBits(val);
        long mantissa_bits = bits & 0x000fffffffffffffL;
        int mantissa_value = (int) (mantissa_bits >> 22 | 0x40000000);
        if (val >= Double.MIN_NORMAL) { //regular positive number
            return mantissa_value;
        } else if (val <= -Double.MIN_NORMAL) { //regular negative number
            int mantissa = -mantissa_value;
            int zeroes = Integer.numberOfLeadingZeros(~mantissa);
            return mantissa << (zeroes - 1);
        } else if (val > 0){ //subnormal positive
            int zeroes = Long.numberOfLeadingZeros(mantissa_bits);
            if (zeroes < INT_MAX_BITS) {
                return (int) (mantissa_bits >> (INT_MAX_BITS - zeroes + 1));
            } else {
                return (int) mantissa_bits << (zeroes - 1);
            }
        } else { // subnormal negative
            long mantissa = -mantissa_bits;
            int zeroes = Long.numberOfLeadingZeros(~mantissa);
            if (zeroes < INT_MAX_BITS) {
                return (int) (mantissa >> (INT_MAX_BITS - zeroes + 1));
            } else {
                return (int) mantissa << (zeroes - 1);
            }
        }
    }

    /*package*/ static int doubleToExponent(double val) {
        if (val == 0) {
            return ZERO_EXPONENT;
        }
        long bits = Double.doubleToRawLongBits(val);
        int exponent_bits = (int) ((bits & 0x7ff0000000000000L) >> 52);
        long mantissa_bits = bits & 0x000fffffffffffffL;
        if (val >= Double.MIN_NORMAL) { //regular positive number
            return exponent_bits - 1024 - 29;
        } else if (val <= -Double.MIN_NORMAL) { //regular negative number
            int mantissa = -(int) (mantissa_bits >> 22 | 0x40000000);
            int zeroes = Integer.numberOfLeadingZeros(~mantissa);
            return exponent_bits - 1024 - 28 - zeroes;
        } else if (val > 0){ //subnormal positive
            int zeroes = Long.numberOfLeadingZeros(mantissa_bits);
            return -1041 - zeroes;
        } else {  // subnormal negative
            int zeroes = Long.numberOfLeadingZeros(~-mantissa_bits);
            return -1041 - zeroes;
        }
    }

    //TODO: Remove custom Assert dependency
    private static void assertNormalized(int significand, long exponent) {
        if (significand > 0) {
            Assert.assertEqualsHex("MSB not set", significand | 0x40000000, significand);
        } else if (significand < 0) {
            Assert.assertEqualsHex("MSB not unset", significand & ~0x40000000, significand);
        } else {
            Assert.assertEqualsHex("zero has has wrong exponent", ZERO_EXPONENT, exponent);
        }
    }

    public static void assertApproximately(IFloat64Exp expected, IFloat64Exp actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }


    public static void assertApproximately(String message, IFloat64Exp expected, IFloat64Exp actual, int bitsSimilarCount) {
        if(!actual.approximately(expected, bitsSimilarCount)) {
            Assert.fail(Assert.formatCustomized("expected approximately: ", message, expected, actual));
        }
    }

    public static void assertApproximately(double expected, IFloat64Exp actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }

    public static void assertApproximately(String message, double expected, IFloat64Exp actual, int bitsSimilarCount) {
        if(!actual.approximately(expected, bitsSimilarCount)) {
            Assert.fail(Assert.formatCustomized("expected approximately: ", message, expected, actual));
        }
    }
}
