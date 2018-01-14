package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float64Exp;
import com.tbohne.util.math.ImmutableFloat64Exp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ToStringTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int first = 0;
        int count = 100000000;
        for(int i = 0; i < count; ++i) {
            offset += 1;
            first += Integer.toString(offset).length();
        }
        return first!=-1?count:0;
    }

    public static long doubleTest(int bitOffset) {
        return 0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        int first = 0;
        int count = 5000000;
        for(int i = 0; i < count; ++i) {
            offset += 1.0;
            first += Double.toString(offset).length();
        }
        return first!=-1?count:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        int first = 0;
        int count = (int) (50000000000L/bitOffset/bitOffset);
        for(int i = 0; i < count; ++i) {
            offset = offset.add(BigInteger.ONE);
            first += offset.toString().length();
        }
        return first!=-1?count:0;
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        int first = 0;
        int count = (int) (50000000000L/bitOffset/bitOffset);
        for(int i = 0; i < count; ++i) {
            offset = offset.add(BigDecimal.ONE);
            first += offset.toString().length();
        }
        return first!=-1?count:0;
    }

    public static long float64ExpTest(int bitOffset) {
        Float64Exp offset = new Float64Exp(1);
        offset.shiftLeft(bitOffset);
        int first = 0;
        int count = 10000000;
        for(int i = 0; i < count; ++i) {
            offset.add(ImmutableFloat64Exp.ONE);
            first += offset.toString().length();
        }
        return first!=-1?count:0;
    }
}
