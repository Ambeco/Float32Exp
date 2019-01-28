package com.tbohne.utilperf;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float32ExpL;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.tbohne.utilperf.Config.CPU_PERF_MULTIPLIER;

class AdditionPerfTests {
    static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        long count = (long) (2955665024L*CPU_PERF_MULTIPLIER);
        int first = offset;
        for(long i = 0; i < count; ++i) {
            first += offset;
            first -= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    static long longTest(int bitOffset) {
        long offset = 1<<bitOffset;
        long count = (long) (2955665024L*CPU_PERF_MULTIPLIER);
        long first = offset;
        for(long i = 0; i < count; ++i) {
            first += offset;
            first -= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    static long doubleTest(int bitOffset) {
        double offset = Math.pow(2.0,bitOffset);
        double first = offset;
        long count = (long) (3285288598L/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += offset;
            first -= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        Double first = offset;
        long count = (long) (1419825808/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.doubleValue() + offset.doubleValue();
            first = first.doubleValue() - offset.doubleValue();
        }
        return count*2 + (first!=-1?0:1);
    }

    static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        BigInteger first = offset;
        long count = (long) (52588469*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.add(offset);
            first = first.subtract(offset);
        }
        return count*2 + (!first.equals(BigInteger.ONE.negate())?0:1);
    }

    static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        BigDecimal first = offset;
        long count = (long) (269746677088L/bitOffset/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.add(offset);
            first = first.subtract(offset);
        }
        return count*2 + (!first.equals(BigDecimal.ONE.negate())?0:1);
    }

    static long float64ExpTest(int bitOffset) {
        Float32Exp offset = new Float32Exp(1);
        offset.shiftLeft(bitOffset);
        Float32Exp first = new Float32Exp(offset);
        long count = (long) (515766522/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first.add(offset);
            first.subtract(offset);
        }
        return count*2 + (!first.equals(-1)?0:1);
    }

    static long float64ExpLTest(int bitOffset) {
        Float32ExpL offset = new Float32ExpL(1);
        offset.shiftLeft(bitOffset);
        Float32ExpL first = new Float32ExpL(offset);
        long count = (long) (515766522/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first.add(offset);
            first.subtract(offset);
        }
        return count*2 + (!first.equals(-1)?0:1);
    }
}
