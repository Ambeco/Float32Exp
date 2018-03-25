package animalia.tbohne.com.utilperf;

import java.text.DecimalFormat;
import java.util.Locale;

import static animalia.tbohne.com.utilperf.Config.CPU_PERF_MULTIPLIER;

/**
 * Each individual test is passed a "bit offset", that means to multiply the values by 2^offset before testing,
 * (or 10^offset for string tests) so we can see how the various value ranges affect performance.
 *
 * Each individual test is hardcoded to take ~3s on my machine, to maximize statistical significance, and keep tests
 * from taking too long.  On your machine, you can tweak Config.CPU_PERF_MULTIPLIER, but the results will normalize
 * themselves for consistency. If a test takes less than one second, the value is followed by a +, and if a test takes
 * more than five seconds, the value is followed by a -.
 * Each category starts by running each test with no offset, to warm the cache and CPU, for more consistent results.
 */
public class Main {
    private static final double MILLIS_PER_SEC = 1000.0;

    private static final int MIN_EXPONENT = 5;
    private static final int MAX_EXPONENT = 8;

    private enum TestFnCategory {
        ADDITION,
        MULTIPLICATION,
        POWER,
        TO_STRING,
        FROM_STRING,
        LCG_RNG,
    };
    private static final boolean[] DO_CATEGORY = new boolean[]{
            true,
            true,
            true,
            true,
            true,
            true,
    };
    private interface TestFn {
        long run(int bitOffset);
    }
    private static class TypeToTest {
        public final String typeName;
        public final TestFn[] Tests;
        public TypeToTest(String typeName, TestFn[] Tests) {
            this.typeName = typeName;
            this.Tests = Tests;
            assert(TestFnCategory.values().length == Tests.length);
        }
    }
    private static final TypeToTest[] TYPES = new TypeToTest[]{
            new TypeToTest("int", new TestFn[] {
                    AdditionPerfTests::intTest,
                    MultiplicationPerfTests::intTest,
                    PowerPerfTests::intTest,
                    ToStringTests::intTest,
                    FromStringTests::intTest,
                    LCGRNGTests::intTest,
            }),
            new TypeToTest("long", new TestFn[] {
                    AdditionPerfTests::longTest,
                    MultiplicationPerfTests::longTest,
                    PowerPerfTests::longTest,
                    ToStringTests::longTest,
                    FromStringTests::longTest,
                    LCGRNGTests::longTest,
            }),
            new TypeToTest("double", new TestFn[] {
                    AdditionPerfTests::doubleTest,
                    MultiplicationPerfTests::doubleTest,
                    PowerPerfTests::doubleTest,
                    ToStringTests::doubleTest,
                    FromStringTests::doubleTest,
                    LCGRNGTests::doubleTest,
            }),
            new TypeToTest("Double", new TestFn[] {
                    AdditionPerfTests::doubleClassTest,
                    MultiplicationPerfTests::doubleClassTest,
                    PowerPerfTests::doubleClassTest,
                    ToStringTests::doubleClassTest,
                    FromStringTests::doubleClassTest,
                    LCGRNGTests::doubleClassTest,
            }),
            new TypeToTest("BigInteger", new TestFn[] {
                    AdditionPerfTests::bigIntegerTest,
                    MultiplicationPerfTests::bigIntegerTest,
                    PowerPerfTests::bigIntegerTest,
                    ToStringTests::bigIntegerTest,
                    FromStringTests::bigIntegerTest,
                    LCGRNGTests::bigIntegerTest,
            }),
            new TypeToTest("BigDecimal", new TestFn[] {
                    AdditionPerfTests::bigDecimalTest,
                    MultiplicationPerfTests::bigDecimalTest,
                    PowerPerfTests::bigDecimalTest,
                    ToStringTests::bigDecimalTest,
                    FromStringTests::bigDecimalTest,
                    LCGRNGTests::bigDecimalTest,
            }),
            new TypeToTest("Float32Exp", new TestFn[] {
                    AdditionPerfTests::float64ExpTest,
                    MultiplicationPerfTests::float64ExpTest,
                    PowerPerfTests::float64ExpTest,
                    ToStringTests::float64ExpTest,
                    FromStringTests::float64ExpTest,
                    LCGRNGTests::float64ExpTest,
            }),
            new TypeToTest("Float32ExpL", new TestFn[] {
                    AdditionPerfTests::float64ExpLTest,
                    MultiplicationPerfTests::float64ExpLTest,
                    PowerPerfTests::float64ExpLTest,
                    ToStringTests::float64ExpLTest,
                    FromStringTests::float64ExpLTest,
                    LCGRNGTests::float64ExpLTest,
            }),
    };

    public static void main(String[] args) {
        System.out.println("ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)");
        DecimalFormat smallFormat = new DecimalFormat(".###");
        DecimalFormat largeFormat = new DecimalFormat("#,###");
        for(int catIdx = 0; catIdx < TestFnCategory.values().length; catIdx++) {
            if (!DO_CATEGORY[catIdx]) continue;;
            TestFnCategory category = TestFnCategory.values()[catIdx];
            System.out.println(category.toString() + " Test:");
            //header
            System.out.print("EXPONENT ");
            for(TypeToTest type : TYPES) {
                //priming
                TestFn TestFn = type.Tests[category.ordinal()];
                TestFn.run(MIN_EXPONENT);
                System.out.print(String.format(Locale.US,"%15s ",type.typeName));
            }
            System.out.println();
            for(int i = MIN_EXPONENT; i < MAX_EXPONENT; ++i) {
                //row
                int bitOffset = 1 << i;
                System.out.print(String.format(Locale.US,"1<<%-6d",bitOffset));
                for(TypeToTest type : TYPES) {
                    //cell
                    TestFn TestFn = type.Tests[category.ordinal()];
                    long start = System.currentTimeMillis();
                    long count = TestFn.run(bitOffset);
                    long end = System.currentTimeMillis();
                    double durSeconds = (end - start) / MILLIS_PER_SEC;
                    if (count <= 0) {
                        System.out.print("            N/A ");
                    } else if (durSeconds <= 0.002) {
                            System.out.print("         INLINE ");
                    } else {
                        double perSecond = count / durSeconds / CPU_PERF_MULTIPLIER;
                        if (perSecond < 100) {
                            System.out.print(String.format(Locale.US," %14s", smallFormat.format(perSecond)));
                        } else {
                            System.out.print(String.format(Locale.US," %14s", largeFormat.format(perSecond)));
                        }
                        if (durSeconds < 1.0) {
                            System.out.print('+');
                        } else if (durSeconds > 5.0) {
                            System.out.print('-');
                        } else {
                            System.out.print(' ');
                        }
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
