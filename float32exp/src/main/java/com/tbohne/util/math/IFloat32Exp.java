package com.tbohne.util.math;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface IFloat32Exp extends Comparable<IFloat32Exp>, Serializable {
    interface ExponentToStringInterface {
        void addExponent(StringBuilder stringBuilder, int exponent);
    }
    int significand();
    int exponent();

    boolean approximately(IFloat32Exp val, int bitsSimilarCount);
    boolean approximately(long val, int bitsSimilarCount);
    boolean approximately(double val, int bitsSimilarCount);

    int compareTo(IFloat32Exp val);
    int compareTo(long val);
    int compareTo(double val);

    boolean lessThan(IFloat32Exp val);
    boolean lessThan(long val);
    boolean lessThan(double val);

    boolean lessOrEquals(IFloat32Exp val);
    boolean lessOrEquals(long val);
    boolean lessOrEquals(double val);

    boolean greaterOrEquals(IFloat32Exp val);
    boolean greaterOrEquals(long val);
    boolean greaterOrEquals(double val);

    boolean greaterThan(IFloat32Exp val);
    boolean greaterThan(long val);
    boolean greaterThan(double val);

    BigInteger toBigInteger();
    BigDecimal toBigDecimal();
    ImmutableFloat32Exp toImmutable();
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
