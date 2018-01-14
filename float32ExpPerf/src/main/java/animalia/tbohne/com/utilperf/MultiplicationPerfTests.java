package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float64Exp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MultiplicationPerfTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int first = offset;
        int count = 500000000;
        for(int i = 0; i < count; ++i) {
            first *= offset;
            first /= offset;
        }
        return first!=-1?count*2:0;
    }

    public static long doubleTest(int bitOffset) {
        double offset = Math.pow(2.0,bitOffset);
        double first = offset;
        int count = 1000000000;
        for(int i = 0; i < count; ++i) {
            first += offset;
            first /= offset;
        }
        return first!=-1?count*2:0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        Double first = offset;
        int count = 500000000;
        for(int i = 0; i < count; ++i) {
            first = first.doubleValue() * offset.doubleValue();
            first = first.doubleValue() / offset.doubleValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        BigInteger first = offset;
        int count = 1000000000/bitOffset;
        for(int i = 0; i < count; ++i) {
            first = first.multiply(offset);
            first = first.divide(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        BigDecimal first = offset;
        int count = 10000000/bitOffset;
        for(int i = 0; i < count; ++i) {
            first = first.multiply(offset);
            first = first.divide(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }

    public static long float64ExpTest(int bitOffset) {
        Float64Exp offset = new Float64Exp(1);
        offset.shiftLeft(bitOffset);
        Float64Exp first = new Float64Exp(offset);
        int count = 100000000;
        for(int i = 0; i < count; ++i) {
            first.multiply(offset);
            first.divide(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }
}
