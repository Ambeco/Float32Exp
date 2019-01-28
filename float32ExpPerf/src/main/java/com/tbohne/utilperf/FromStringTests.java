package com.tbohne.utilperf;

import java.math.BigDecimal;

import static com.tbohne.utilperf.Config.CPU_PERF_MULTIPLIER;

class FromStringTests {
    static long intTest(int bitOffset) {
        return 0;
    }

    static long longTest(int bitOffset) {
        return 0;
    }

    static long doubleTest(int bitOffset) {
        return 0;
    }

    static long doubleClassTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (1212389168/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    static long bigIntegerTest(int bitOffset) {
        return 0;
    }

    static long bigDecimalTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (37017270*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    static long float64ExpTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (51249375*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }

    static long float64ExpLTest(int bitOffset) {
        String string = "1E" + bitOffset;
        int first = 0;
        long count = (long) (51249375*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return count + (first!=-1?0:1);
    }
}
