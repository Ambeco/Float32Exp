package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;
import java.math.BigInteger;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

public class AdditionPerfTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int count = 100000000;
        int first = offset;
        for(int i = 0; i < count; ++i) {
            first += offset;
            first -= offset;
        }
        return first!=-1?count*2:0;
    }

    public static long doubleTest(int bitOffset) {
        double offset = Math.pow(2.0,bitOffset);
        double first = offset;
        int count = (int) (2147483647/2*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += offset;
            first -= offset;
        }
        return first!=-1?count*2:0;
    }

    public static long doubleClassTest(int bitOffset) {
        Double offset = Math.pow(2.0,bitOffset);
        Double first = offset;
        int count = (int) (1941747572/2*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first = first.doubleValue() + offset.doubleValue();
            first = first.doubleValue() - offset.doubleValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        BigInteger offset = BigInteger.ONE.shiftLeft(bitOffset);
        BigInteger first = offset;
        int count = (int) (2195503680L/bitOffset*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first = first.add(offset);
            first = first.subtract(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }

    public static long bigDecimalTest(int bitOffset) {
        BigDecimal offset = BigDecimal.valueOf(2).pow(bitOffset);
        BigDecimal first = offset;
        int count = (int) (5514562752L/bitOffset*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first = first.add(offset);
            first = first.subtract(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }

    public static long float64ExpTest(int bitOffset) {
        Float32Exp offset = new Float32Exp(1);
        offset.shiftLeft(bitOffset);
        Float32Exp first = new Float32Exp(offset);
        int count = (int) (564174894/2*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first.add(offset);
            first.subtract(offset);
        }
        return (!first.equals(-1))?count*2:0;
    }
}
