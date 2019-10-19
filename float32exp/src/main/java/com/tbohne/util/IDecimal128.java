package com.tbohne.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface IDecimal128 extends Comparable<IDecimal128>, Serializable {
    interface ExponentToStringInterface {
        void addExponent(StringBuilder stringBuilder, int exponent);
    }
    int significand();
    int exponent();

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

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableDecimal128 toImmutable();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();

    String toString();
    StringBuilder toString(StringBuilder sb);
    String toEngineeringString();
    StringBuilder toEngineeringString(StringBuilder sb);
    StringBuilder toString(StringBuilder sb, int min_digits, int max_digits, int exponentMultiple,
                           ExponentToStringInterface exponentToString);
    StringBuilder toBNotationString(StringBuilder sb);
}
