package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

public class PowerPerfTests {
    public static final double BASE = 1.001;

    public static long intTest(int bitOffset) {
        return 0;
    }

    public static long doubleTest(int bitOffset) {
        double first = 0;
        int count = (int) (2147483647*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += Math.pow(BASE, bitOffset);
        }
        return first!=-1?count:0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double first = 0d;
        int count = (int) (992063492*CPU_PERF_MULTIPLIER);
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
        int count = (int) (364431424/bitOffset*CPU_PERF_MULTIPLIER);
        BigDecimal base = BigDecimal.valueOf(BASE);
        for(int i = 0; i < count; ++i) {
            first = first.add(base.pow(bitOffset));
        }
        return (!first.equals(-1))?count:0;
    }

    public static long float64ExpTest(int bitOffset) {
        Float32Exp first = new Float32Exp(0);
        int count = (int) (14405013*CPU_PERF_MULTIPLIER);
        Float32Exp base = new Float32Exp();
        for(int i = 0; i < count; ++i) {
            first.add(base.set(BASE).pow(bitOffset));
        }
        return (!first.equals(-1))?count:0;
    }
}
