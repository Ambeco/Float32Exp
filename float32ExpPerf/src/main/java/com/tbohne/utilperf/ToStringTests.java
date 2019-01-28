package com.tbohne.utilperf;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.ImmutableFloat32Exp;
import com.tbohne.util.math.ImmutableFloat32ExpL;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.tbohne.utilperf.Config.CPU_PERF_MULTIPLIER;

public class ToStringTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int first = 0;
        long count = (long) (93281170*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset += 1;
            first += Integer.toString(offset).length();
        }
        return count + (first!=-1?0:1);
    }

    public static long longTest(int bitOffset) {
        long offset = 1<<bitOffset;
        long first = 0;
        long count = (long) (93281170*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset += 1;
            first += Long.toString(offset).length();
        }
        return count + (first!=-1?0:1);
    }

    public static long doubleTest(int bitOffset) {
        return 0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        int first = 0;
        long count = (long) (30729830400L/bitOffset/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset += 1.0;
            first += Double.toString(offset).length();
        }
        return count + (first!=-1?0:1);
    }

    public static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        int first = 0;
        long count = (long) (335497726/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset = offset.add(BigInteger.ONE);
            first += offset.toString().length();
        }
        return count + (first!=-1?0:1);
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        int first = 0;
        long count = (long) (23363140600L/bitOffset/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset = offset.add(BigDecimal.ONE);
            first += offset.toString().length();
        }
        return count + (first!=-1?0:1);
    }

    public static long float64ExpTest(int bitOffset) {
        Float32Exp offset = new Float32Exp(1);
        offset.shiftLeft(bitOffset);
        int first = 0;
        long count = (long) (7429402*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset.add(ImmutableFloat32Exp.ONE);
            first += offset.toString().length();
        }
        return count + (first!=-1?0:1);
    }

    public static long float64ExpLTest(int bitOffset) {
        Float32ExpL offset = new Float32ExpL(1);
        offset.shiftLeft(bitOffset);
        int first = 0;
        long count = (long) (7429402*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            offset.add(ImmutableFloat32ExpL.ONE);
            first += offset.toString().length();
        }
        return count + (first!=-1?0:1);
    }
}
