package com.tbohne.util.math;

import com.tbohne.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is the shared logic of Float32Exp and ImmutableFloat32Exp.
 */
/*package*/ class Float32ExpSharedBase extends Number implements IFloat32Exp {
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
                stringBuilder.append('E').append(exponent);
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

    /*package*/ Float32ExpSharedBase() {significand = 0; exponent = ZERO_EXPONENT;}
    /*package*/ Float32ExpSharedBase(int significand, int exponent) {
        this.significand = significand;
        this.exponent = exponent;
    }
    /*package*/ Float32ExpSharedBase(long parts) {
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
    }

    /*package*/ IFloat32Exp set(char[] in, int offset, int len) {
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

    /*package*/ IFloat32Exp set(BigDecimal val) {
        set(val.unscaledValue());
        long pow10parts = getPowerOf10Parts(val.scale());
        long scaleSig = pow10parts >> INT_MAX_BITS;
        long scaleExp = (int) pow10parts;
        long newSig = ((long) significand) << (INT_MAX_BITS-2);
        newSig /= scaleSig;
        setNormalized(newSig, ((long) exponent) - scaleExp - EXPONENT_BIAS);
        return this;
    }

    /*package*/ IFloat32Exp set(BigInteger val) {
        int bits = val.bitLength() - INT_MAX_BITS + 1;
        if (bits < 0) {
            setLong(val.intValue());
        } else {
            val = val.shiftRight(bits);
            setNormalized(val.intValue(), bits);
        }
        return this;
    }

    /*package*/ void setImpl(long parts)
    {setImpl((int) (parts >> INT_MAX_BITS), (int) parts);}

    /*package*/ void setImpl(int significand, int exponent) {
        if (INTERNAL_ASSERTS) {
            assertNormalized(significand, exponent);
        }
        this.significand = significand;
        this.exponent = exponent;
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
        return Integer.compare(significand, 0);
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
        if (object instanceof IFloat32Exp) {
            IFloat32Exp other = (IFloat32Exp) object;
            return equalsImpl(other.significand(), other.exponent());
        }
        if (object instanceof Integer) {
            return equalsImpl(getLongParts((Integer)object));
        }
        if (object instanceof Long) {
            return equalsImpl(getLongParts((Long)object));
        }
        if (object instanceof Float) {
            return equalsImpl(getDoubleParts((Float)object));
        }
        if (object instanceof Double) {
            return equalsImpl(getDoubleParts((Double)object));
        }
        return false;
    }
    public boolean equals(IFloat32Exp val) {
        return equalsImpl(val.significand(), val.exponent());
    }
    public boolean equals(long val) {
        return equalsImpl(getLongParts(val));
    }
    public boolean equals(double val) {
        return equalsImpl(getDoubleParts(val));
    }
    private boolean equalsImpl(long parts)
    {return equalsImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private boolean equalsImpl(int otherSignificand, int otherExponent) {
        return significand==otherSignificand && exponent == otherExponent;
    }

    /*
     * Returns true if the values are within ~22 bits of each other.
     */
    public boolean approximately(IFloat32Exp val, int bitsSimilarCount)
    {return approximatelyImpl(val.significand(), val.exponent(), bitsSimilarCount);}
    public boolean approximately(long val, int bitsSimilarCount)
    {return approximatelyImpl(getDoubleParts(val), bitsSimilarCount);}
    public boolean approximately(double val, int bitsSimilarCount)
    {return approximatelyImpl(getDoubleParts(val), bitsSimilarCount);}
    private boolean approximatelyImpl(long parts, int bitsSimilarCount)
    {return approximatelyImpl((int) (parts >> INT_MAX_BITS), (int) parts, bitsSimilarCount);}
    private boolean approximatelyImpl(long otherSignificand, int otherExponent, int bitsSimilarCount) {
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
    public int compareTo(IFloat32Exp val) {return compareToImpl(val.significand(), val.exponent());}
    public int compareTo(long val) {return compareToImpl(getDoubleParts(val));}
    public int compareTo(double val) {return compareToImpl(getDoubleParts(val));}
    private int compareToImpl(long parts) {return compareToImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private int compareToImpl(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent > otherExponent ? 1 : -1;
        } else if (significand != otherSignificand) {
            return significand > otherSignificand ? 1 : -1;
        } else {
            return 0;
        }
    }

    public boolean lessThan(IFloat32Exp val)
    {return lessThanImpl(val.significand(), val.exponent());}
    public boolean lessThan(long val)
    {return lessThanImpl(getLongParts(val));}
    public boolean lessThan(double val)
    {return lessThanImpl(getDoubleParts(val));}
    private boolean lessThanImpl(long parts)
    {return lessThanImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private boolean lessThanImpl(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand < otherSignificand;
    }

    public boolean lessOrEquals(IFloat32Exp val)
    {return lessOrEqualsImpl(val.significand(), val.exponent());}
    public boolean lessOrEquals(long val)
    {return lessOrEqualsImpl(getLongParts(val));}
    public boolean lessOrEquals(double val)
    {return lessOrEqualsImpl(getDoubleParts(val));}
    private boolean lessOrEqualsImpl(long parts)
    {return lessOrEqualsImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private boolean lessOrEqualsImpl(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent < otherExponent;
        }
        return significand <= otherSignificand;
    }

    public boolean greaterOrEquals(IFloat32Exp val)
    {return greaterOrEqualsImpl(val.significand(), val.exponent());}
    public boolean greaterOrEquals(long val)
    {return greaterOrEqualsImpl(getLongParts(val));}
    public boolean greaterOrEquals(double val)
    {return greaterOrEqualsImpl(getDoubleParts(val));}
    private boolean greaterOrEqualsImpl(long parts)
    {return greaterOrEqualsImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private boolean greaterOrEqualsImpl(int otherSignificand, int otherExponent) {
        if (exponent != otherExponent) {
            return exponent > otherExponent;
        }
        return significand >= otherSignificand;
    }

    public boolean greaterThan(IFloat32Exp val)
    {return greaterThanImpl(val.significand(), val.exponent());}
    public boolean greaterThan(long val)
    {return greaterThanImpl(getLongParts(val));}
    public boolean greaterThan(double val)
    {return greaterThanImpl(getDoubleParts(val));}
    private boolean greaterThanImpl(long parts)
    {return greaterThanImpl((int) (parts >> INT_MAX_BITS), (int) parts);}
    private boolean greaterThanImpl(int otherSignificand, int otherExponent) {
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
            //TODO: for Float32Exp(512), this ends up one bit too small, even with rounding. Why?
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
        //calculate display exponent and digit counts
        int digitsBeforeDecimal = (int) base10Exp % exponentMultiple + 1;
        int displayExponent = (int) base10Exp - digitsBeforeDecimal + 1;
        int minDigitsAfterDecimal = min_digits - digitsBeforeDecimal;
        int maxDigitsAfterDecimal = max_digits - digitsBeforeDecimal;
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
     * Returns this {@code Float32Exp} as a big integer instance. A fractional
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
     * Returns this {@code Float32Exp} as a big decimal instance.
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(significand).multiply(BigDecimal.TEN.pow(exponent));
    }

    @Override
    public ImmutableFloat32Exp toImmutable() {
        return new ImmutableFloat32Exp(significand, exponent);
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

    /* package */ static long getNormalizedParts(long significand, long exponent) {
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

    /*package*/ void setLong(long v) {
        long parts = getLongParts(v);
        this.significand = (int) (parts >> INT_MAX_BITS);
        this.exponent = (int) parts;
    }

    /*package*/ static int longToSignificand(long v) {
        long bitpattern = v >= 0 ? v : ~v;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        int shiftLeft = zeroes - INT_MAX_BITS - 1;
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

    /*package*/ static long getLongParts(long v) {
        if (v == 0) {
            return ZERO_EXPONENT & 0xFFFFFFFFL;
        }
        long bitpattern = v >= 0 ? v : ~v;
        int zeroes = Long.numberOfLeadingZeros(bitpattern);
        int exp = INT_MAX_BITS + 1 - zeroes;
        long sig = v << (-exp + INT_MAX_BITS);
        return sig | (exp & 0xFFFFFFFFL);
    }

    /*package*/ static long getDoubleParts(double val) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            throw new UnsupportedOperationException("Float32Exp doesn't support INF or NAN");
        }
        if (val == 0.0) {
            return ZERO_EXPONENT & 0xFFFFFFFFL;
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
            sig = mantissa_value << (zeroes - 1);
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
        return (sig << INT_MAX_BITS) | (exp & 0xFFFFFFFFL);
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

    public static void assertApproximately(IFloat32Exp expected, IFloat32Exp actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }


    public static void assertApproximately(String message, IFloat32Exp expected, IFloat32Exp actual, int bitsSimilarCount) {
        if(!actual.approximately(expected, bitsSimilarCount)) {
            Assert.fail(Assert.formatCustomized("expected approximately: ", message, expected, actual));
        }
    }

    public static void assertApproximately(double expected, IFloat32Exp actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }

    public static void assertApproximately(String message, double expected, IFloat32Exp actual, int bitsSimilarCount) {
        if(!actual.approximately(expected, bitsSimilarCount)) {
            Assert.fail(Assert.formatCustomized("expected approximately: ", message, expected, actual));
        }
    }
}
