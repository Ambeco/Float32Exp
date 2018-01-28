package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;
import java.math.BigInteger;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

public class MultiplicationPerfTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int first = offset;
        long count = (long) (338099112/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first *= offset;
            first /= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    public static long longTest(int bitOffset) {
        long offset = 1<<bitOffset;
        long first = offset;
        long count = (long) (338099112/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first *= offset;
            first /= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    public static long doubleTest(int bitOffset) {
        double offset = Math.pow(2.0,bitOffset);
        double first = offset;
        long count = (long) (1269489431/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first += offset;
            first /= offset;
        }
        return count*2 + (first!=-1?0:1);
    }

    public static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        Double first = offset;
        long count = (long) (528817563/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.doubleValue() * offset.doubleValue();
            first = first.doubleValue() / offset.doubleValue();
        }
        return count*2 + (first!=-1?0:1);
    }

    public static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        BigInteger first = offset;
        long count = (long) (665388635/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.multiply(offset);
            first = first.divide(offset);
        }
        return count*2 + (!first.equals(BigInteger.ONE.negate())?0:1);
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        BigDecimal first = offset;
        long count = (long) (11240977/bitOffset*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first = first.multiply(offset);
            first = first.divide(offset);
        }
        return count*2 + (!first.equals(BigDecimal.ONE.negate())?0:1);
    }

    public static long float64ExpTest(int bitOffset) {
        Float32Exp offset = new Float32Exp(1);
        offset.shiftLeft(bitOffset);
        Float32Exp first = new Float32Exp(offset);
        long count = (long) (241522919/2*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            first.multiply(offset);
            first.divide(offset);
        }
        return count*2 + (!first.equals(-1)?0:1);
    }
}
