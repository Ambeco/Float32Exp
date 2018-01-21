package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

public class FromStringTests {
    public static long intTest(int bitOffset) {
        return 0;
    }

    public static long doubleTest(int bitOffset) {
        return 0;
    }

    public static long doubleClassTest(int bitOffset) {
        String string = "1e" + bitOffset;
        int first = 0;
        int count = (int) (72668214*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        String string = "1e" + bitOffset;
        int first = 0;
        int count = (int) (2266469952L/bitOffset*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigDecimalTest(int bitOffset) {
        String string = "1e" + bitOffset;
        int first = 0;
        int count = (int) (4250760384L/bitOffset*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return first!=-1?count*2:0;
    }

    public static long float64ExpTest(int bitOffset) {
        String string = "1e" + bitOffset;
        Float32Exp offset = new Float32Exp(1);
        offset.shiftLeft(bitOffset);
        int first = 0;
        int count = (int) (66887100*CPU_PERF_MULTIPLIER);
        for(int i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return first!=-1?count*2:0;
    }
}
