package animalia.tbohne.com.utilperf;

import java.text.DecimalFormat;
import java.util.Locale;

public class Main {
    private static final int MIN_EXPONENT = 5;
    private static final int MAX_EXPONENT = 8;
    private enum TestFnCategory {
        ADDITION,
        MULTIPLICATION,
        POWER,
        TO_STRING,
        FROM_STRING
    };
    private static final boolean[] DO_CATEGORY = new boolean[]{
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
            }),
            new TypeToTest("double", new TestFn[] {
                    AdditionPerfTests::doubleTest,
                    MultiplicationPerfTests::doubleTest,
                    PowerPerfTests::doubleTest,
                    ToStringTests::doubleTest,
                    FromStringTests::doubleTest,
            }),
            new TypeToTest("Double", new TestFn[] {
                    AdditionPerfTests::doubleClassTest,
                    MultiplicationPerfTests::doubleClassTest,
                    PowerPerfTests::doubleClassTest,
                    ToStringTests::doubleClassTest,
                    FromStringTests::doubleClassTest,
            }),
            new TypeToTest("BigInteger", new TestFn[] {
                    AdditionPerfTests::bigIntegerTest,
                    MultiplicationPerfTests::bigIntegerTest,
                    PowerPerfTests::bigIntegerTest,
                    ToStringTests::bigIntegerTest,
                    FromStringTests::bigIntegerTest,
            }),
            new TypeToTest("BigDecimal", new TestFn[] {
                    AdditionPerfTests::bigDecimalTest,
                    MultiplicationPerfTests::bigDecimalTest,
                    PowerPerfTests::bigDecimalTest,
                    ToStringTests::bigDecimalTest,
                    FromStringTests::bigDecimalTest,
            }),
            new TypeToTest("Float64Exp", new TestFn[] {
                    AdditionPerfTests::float64ExpTest,
                    MultiplicationPerfTests::float64ExpTest,
                    PowerPerfTests::float64ExpTest,
                    ToStringTests::float64ExpTest,
                    FromStringTests::float64ExpTest,
            }),
    };

    public static void main(String[] args) {
        System.out.println("ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)");
        DecimalFormat format = new DecimalFormat(".###");
        for(int catIdx = 0; catIdx < TestFnCategory.values().length; catIdx++) {
            if (!DO_CATEGORY[catIdx]) continue;;
            TestFnCategory category = TestFnCategory.values()[catIdx];
            System.out.println(category.toString() + " Test:");
            //priming
            for(TypeToTest type : TYPES) {
                TestFn TestFn = type.Tests[category.ordinal()];
                TestFn.run(1);
            }
            //header
            System.out.print(String.format(Locale.US,"%-11s","EXPONENT"));
            for(TypeToTest type : TYPES) {
                System.out.print(String.format(Locale.US,"%11s",type.typeName));
            }
            System.out.println();
            for(int i = MIN_EXPONENT; i < MAX_EXPONENT; ++i) {
                //row
                int bitOffset = 1 << i;
                System.out.print(String.format(Locale.US,"1<<%-8d",bitOffset));
                for(TypeToTest type : TYPES) {
                    //cell
                    TestFn TestFn = type.Tests[category.ordinal()];
                    long start = System.currentTimeMillis();
                    long count = TestFn.run(bitOffset);
                    long end = System.currentTimeMillis();
                    long dur = end - start;
                    if (count == 0) {
                        System.out.print("        N/A");
                    } else if (dur == 0) {
                            System.out.print("     INLINE");
                    } else {
                        double perSecond = count / (double) dur;
                        if (perSecond >= 10) {
                            System.out.print(String.format(Locale.US,"%10d",(int) perSecond));
                        } else {
                            System.out.print(String.format(Locale.US,"%10s", format.format(perSecond)));
                        }
                        if (dur < 1000) {
                            System.out.print('+');
                        } else if (dur > 5000) {
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
