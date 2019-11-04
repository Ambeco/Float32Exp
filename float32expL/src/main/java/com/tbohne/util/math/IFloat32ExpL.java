package com.tbohne.util.math;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface IFloat32ExpL extends Comparable<IFloat32ExpL>, Serializable {
    interface ExponentToStringInterface {
        void addExponent(StringBuilder stringBuilder, int exponent);
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
    StringBuilder toString(StringBuilder sb);
    String toEngineeringString();
    StringBuilder toEngineeringString(StringBuilder sb);
    StringBuilder toString(StringBuilder sb, int minDigits, int maxDigits, int exponentMultiple,
                           ExponentToStringInterface exponentToString);
    StringBuilder toBNotationString(StringBuilder sb);
}
