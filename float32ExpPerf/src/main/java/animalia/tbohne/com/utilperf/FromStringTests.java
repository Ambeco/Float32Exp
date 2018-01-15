package animalia.tbohne.com.utilperf;

import com.tbohne.util.math.Float32Exp;

import java.math.BigDecimal;

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
        int count = 20000000;
        for(int i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigIntegerTest(int bitOffset) {
        String string = "1e" + bitOffset;
        int first = 0;
        int count = 2000000000/bitOffset;
        for(int i = 0; i < count; ++i) {
            first += new Double(string).intValue();
        }
        return first!=-1?count*2:0;
    }

    public static long bigDecimalTest(int bitOffset) {
        String string = "1e" + bitOffset;
        int first = 0;
        int count = 2000000000/bitOffset;
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
        int count = 10000000;
        for(int i = 0; i < count; ++i) {
            first += new BigDecimal(string).intValue();
        }
        return first!=-1?count*2:0;
    }
}
