package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

public class PowerPerfTests {
    public static final double BASE = 1.001;

    public static long intTest(int bitOffset) {
        return 0;
    }

    public static long longTest(int bitOffset) {
        return 0;
    }

    public static long doubleTest(int bitOffset) {
        double first = 0;
        long count = (long) (3409824572L*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += Math.pow(BASE, bitOffset);
        }
        return count + (first!=-1?0:1);
    }

    public static long doubleClassTest(int bitOffset) {
        Double first = 0d;
        long count = (long) (828407072*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += Math.pow(BASE, bitOffset);
        }
        return count + (first!=-1?0:1);
    }

    public static long bigIntegerTest(int bitOffset) {
        return 0;
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal first = BigDecimal.ZERO;
        long count = (long) (265398510/bitOffset*CPU_PERF_MULTIPLIER);
        BigDecimal base = BigDecimal.valueOf(BASE);
        for(long i = 0; i < count; ++i) {
            first = first.add(base.pow(bitOffset));
        }
        return count + (!first.equals(BigDecimal.ONE.negate())?0:1);
    }

    public static long float64ExpTest(int bitOffset) {
        Float32Exp first = new Float32Exp(0);
        long count = (long) (388289237*CPU_PERF_MULTIPLIER);
        if (bitOffset == 32) { // no idea why this is slow compared to others
            count = (int) (24575669*CPU_PERF_MULTIPLIER);
        }
        Float32Exp base = new Float32Exp();
        for(long i = 0; i < count; ++i) {
            first.add(base.set(BASE).pow(bitOffset));
        }
        return count + (!first.equals(-1)?0:1);
    }
}
