package com.tbohne.util.math;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface IFloat32ExpL extends Comparable<IFloat32ExpL>, Serializable {
    interface ExponentToStringInterface {
        /*
         * Writes the exponent to the destination buffer.
         * Examples of strings written for exponent 3 might be "e3", "G" or "km".
         * @param exponent the exponent to write to the array
         * @param destination where to write the exponent
         * @return index in array last written to.
         * @throws IndexOutOfBoundsException if this writes more than 18 chars.
         */
        int writeExponent(int exponent, char[] destination, int index);
    }
    class StringFormatParams {
        public int minDigits;
        public int maxDigits;
        public int exponentMultiple;
        public ExponentToStringInterface exponentToString;
        public StringFormatParams(int minDigits, int maxDigits, int exponentMultiple, ExponentToStringInterface exponentToString) {
            this.minDigits = minDigits;
            this.maxDigits = maxDigits;
            this.exponentMultiple = exponentMultiple;
            this.exponentToString = exponentToString;
        }
    }
    long getParts();
    int significand();
    int exponent();

    boolean approximately(IFloat32ExpL val, int bitsSimilarCount);
    boolean approximately(long val, int bitsSimilarCount);
    boolean approximately(double val, int bitsSimilarCount);

    int compareTo(IFloat32ExpL val);
    int compareTo(long val);
    int compareTo(double val);

    boolean lessThan(IFloat32ExpL val);
    boolean lessThan(long val);
    boolean lessThan(double val);

    boolean lessOrEquals(IFloat32ExpL val);
    boolean lessOrEquals(long val);
    boolean lessOrEquals(double val);

    boolean greaterOrEquals(IFloat32ExpL val);
    boolean greaterOrEquals(long val);
    boolean greaterOrEquals(double val);

    boolean greaterThan(IFloat32ExpL val);
    boolean greaterThan(long val);
    boolean greaterThan(double val);

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableFloat32ExpL toImmutable();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();

    String toString();
    String toEngineeringString();
    String toString(StringFormatParams params);
    void appendString(Appendable appendable);
    void appendEngineeringString(Appendable appendable);
    void appendString(Appendable appendable, StringFormatParams params);
    int appendString(char[] destination, int index, StringFormatParams params);
}
