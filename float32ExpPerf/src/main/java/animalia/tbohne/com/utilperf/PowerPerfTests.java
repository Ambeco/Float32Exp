package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float64Exp;

import java.math.BigDecimal;

public class PowerPerfTests {
    public static final double BASE = 1.001;

    public static long intTest(int bitOffset) {
        return 0;
    }

    public static long doubleTest(int bitOffset) {
        double first = 0;
        int count = 1000000000;
        for(int i = 0; i < count; ++i) {
            first += Math.pow(BASE, bitOffset);
        }
        return first!=-1?count:0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double first = 0d;
        int count = 1000000000;
        for(int i = 0; i < count; ++i) {
            first += Math.pow(BASE, bitOffset);
        }
        return first!=-1?count:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        return 0;
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal first = BigDecimal.ZERO;
        int count = 500000000/bitOffset;
        BigDecimal base = BigDecimal.valueOf(BASE);
        for(int i = 0; i < count; ++i) {
            first = first.add(base.pow(bitOffset));
        }
        return (!first.equals(-1))?count:0;
    }

    public static long float64ExpTest(int bitOffset) {
        Float64Exp first = new Float64Exp(0);
        int count = 10000000;
        Float64Exp base = new Float64Exp();
        for(int i = 0; i < count; ++i) {
            first.add(base.set(BASE).pow(bitOffset));
        }
        return (!first.equals(-1))?count:0;
    }
}
