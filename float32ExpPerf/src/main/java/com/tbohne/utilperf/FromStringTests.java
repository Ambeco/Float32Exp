package com.tbohne.utilperf;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.Float32ExpL;

import java.math.BigDecimal;

import static com.tbohne.utilperf.Config.CPU_PERF_MULTIPLIER;

public class FromStringTests {
    public static long intTest(int bitOffset) {
        return 0;
    }

    public static long longTest(int bitOffset) {
        return 0;
    }

    public static long doubleTest(int bitOffset) {
        return 0;
    }

    public static long doubleClassTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (1212389168/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    public static long bigIntegerTest(int bitOffset) {
        return 0;
    }

    public static long bigDecimalTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (37017270*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    public static long float64ExpTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (51249375*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    public static long float64ExpLTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (51249375*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }
}
