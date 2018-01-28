package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;
import com.tbohne.util.math.IFloat32Exp;
import com.tbohne.util.math.ImmutableFloat32Exp;

import java.math.BigDecimal;
import java.math.BigInteger;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

/**
 * naïve linear congruential generator to calculate random numbers
 * from http://benchmarksgame.alioth.debian.org/u64q/fasta-description.html#fasta
 **/
public class LCGRNGTests {
    public static long intTest(int bitOffset) {
        int offset = 1<<bitOffset;
        int IM = 139968 * offset;
        int IA = 3877 * offset;
        int IC = 29573 * offset;
        int Seed = 42 * offset;
        int res = 0;
        long count = (long) (287234733*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            Seed = (Seed * IA + IC) % IM;
            res += Integer.MAX_VALUE * Seed / IM;
        }
        return count + (res!=-1?0:1);
    }

    public static long longTest(int bitOffset) {
        long offset = 1<<bitOffset;
        long IM = 139968 * offset;
        long IA = 3877 * offset;
        long IC = 29573 * offset;
        long Seed = 42 * offset;
        long res = 0;
        long count = (long) (287234733*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            Seed = (Seed * IA + IC) % IM;
            res += Integer.MAX_VALUE * Seed / IM;
        }
        return count + (res!=-1?0:1);
    }

    public static long doubleTest(int bitOffset) {
        int offset = 1<<bitOffset;
        double IM = 139968 * offset;
        double IA = 3877 * offset;
        double IC = 29573 * offset;
        double Seed = 42 * offset;
        double res = 0;
        long count = (long) (114479618*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            Seed = (Seed * IA + IC) % IM;
            res += Integer.MAX_VALUE * Seed / IM;
        }
        return count + (res!=-1?0:1);
    }

    public static long doubleClassTest(int bitOffset) {
        int offset = 1<<bitOffset;
        Double IM = (double) (139968 * offset);
        Double IA = (double) (3877 * offset);
        Double IC = (double) (29573 * offset);
        Double Seed = (double) (42 * offset);
        Double Max = (double) Integer.MAX_VALUE;
        Double res = 0d;
        long count = (long) (98188366*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            Seed = (Seed * IA + IC) % IM;
            res += Max * Seed / IM;
        }
        return count + (res!=-1?0:1);
    }

    public static long bigIntegerTest(int bitOffset) {
        int offset = 1<<bitOffset;
        BigInteger IM = BigInteger.valueOf(139968 * offset);
        BigInteger IA = BigInteger.valueOf(3877 * offset);
        BigInteger IC = BigInteger.valueOf(29573 * offset);
        BigInteger Seed = BigInteger.valueOf(42 * offset);
        BigInteger res = BigInteger.ZERO;
        BigInteger Max = BigInteger.valueOf(Integer.MAX_VALUE);
        long count = (long) (12921700*CPU_PERF_MULTIPLIER)+1;
        for(long i = 0; i < count; ++i) {
            Seed = Seed.multiply(IA).add(IC).mod(IM);
            res = res.add(Max).multiply(Seed).divide(IM);
        }
        return count + (!res.equals(BigInteger.ONE.negate())?0:1);
    }

    public static long bigDecimalTest(int bitOffset) {
        int offset = 1<<bitOffset;
        BigDecimal IM = BigDecimal.valueOf(139968 * offset);
        BigDecimal IA = BigDecimal.valueOf(3877 * offset);
        BigDecimal IC = BigDecimal.valueOf(29573 * offset);
        BigDecimal Seed = BigDecimal.valueOf(42 * offset);
        BigDecimal res = BigDecimal.ZERO;
        BigDecimal Max = BigDecimal.valueOf(Integer.MAX_VALUE);
        long count = (long) (5886718*CPU_PERF_MULTIPLIER)+1;
        for(long i = 0; i < count; ++i) {
            Seed = Seed.multiply(IA).add(IC).remainder(IM);
            res = res.add(Max).multiply(Seed).divide(IM, BigDecimal.ROUND_HALF_EVEN);
        }
        return count + (!res.equals(BigDecimal.ONE.negate())?0:1);
    }

    public static long float64ExpTest(int bitOffset) {
        int offset = 1<<bitOffset;
        ImmutableFloat32Exp IM = new ImmutableFloat32Exp(139968 * offset);
        ImmutableFloat32Exp IA = new ImmutableFloat32Exp(3877 * offset);
        ImmutableFloat32Exp IC = new ImmutableFloat32Exp(29573 * offset);
        Float32Exp Seed = new Float32Exp(42 * offset);
        Float32Exp res = new Float32Exp(ImmutableFloat32Exp.ZERO);
        ImmutableFloat32Exp Max = new ImmutableFloat32Exp(Integer.MAX_VALUE);
        long count = (long) (43359568*CPU_PERF_MULTIPLIER);
        for(long i = 0; i < count; ++i) {
            Seed.multiply(IA).add(IC).remainder(IM);
            res.add(Max).multiply(Seed).divide(IM);
        }
        return count + (!res.equals(-1)?0:1);
    }
}
