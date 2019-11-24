package com.tbohne.util;

import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Assert {
    @FormatMethod
    static public void fail(@FormatString String format, @Nullable Object ...values) {
        throw new AssertionError(String.format(format, values));
    }

    static public void failComparison(@CompileTimeConstant String expectation, String message, @Nullable Object expected,
            @Nullable Object actual) {
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            expectedString = formatClassAndValue(expected, expectedString);
            actualString = formatClassAndValue(expected, actualString);
        }
        fail("%s %s <%s> but was <%s>",
                message,
                expectation,
                expectedString,
                actualString);
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    static public void assertGreater(long expected, long actual) {
        assertGreater(null, expected, actual);
    }

    static public void assertGreater(String message, long expected, long actual) {
        if (actual <= expected) {
            failNotGreater(message, expected, actual);
        }
    }

    static private void failNotGreater(String message, Object expected, Object actual) {
        failComparison("Expected greater: ", message, expected, actual);
    }

    static public void assertAtLeast(long expected, long actual) {
        assertAtLeast(null, expected, actual);
    }

    static public void assertAtLeast(String message, long expected, long actual) {
        if (actual < expected) {
            failNotAtLeast(message, expected, actual);
        }
    }

    static private void failNotAtLeast(String message, Object expected, Object actual) {
        failComparison("Expected at least: ", message, expected, actual);
    }

    static public void assertAtMost(long expected, long actual) {
        assertAtMost(null, expected, actual);
    }

    static public void assertAtMost(String message, long expected, long actual) {
        if (actual > expected) {
            failNotAtMost(message, expected, actual);
        }
    }

    static private void failNotAtMost(String message, Object expected, Object actual) {
        failComparison("Expected at most: ", message, expected, actual);
    }

    static public void assertLess(long expected, long actual) {
        assertLess(null, expected, actual);
    }

    static public void assertLess(String message, long expected, long actual) {
        if (actual >= expected) {
            failNotLess(message, expected, actual);
        }
    }

    static private void failNotLess(String message, Object expected, Object actual) {
        failComparison("Expected less: ", message, expected, actual);
    }

    static public void assertMultiple(long expected, long actual) {
        assertMultiple(null, expected, actual);
    }

    static public void assertMultiple(String message, long multipleOf, long actual) {
        if (actual % multipleOf != 0) {
            failComparison("Expected multiple: ", message, multipleOf, actual);
        }
    }

    static public void assertEquals(String expected, String actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(String message, String expected, String actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        failComparison("Expected: ", message, expected, actual);
    }

    static public void assertEqualsHex(String message, long expected, long actual) {
        if (expected != actual) {
            failComparison("Expected: ", message, "0x"+Long.toHexString(expected), "0x"+Long.toHexString(actual));
        }
    }

    static public void assertApproximately(double expected, double actual, int bitsSimilarCount) {
        assertApproximately(null, expected, actual, bitsSimilarCount);
    }

    // These only match to a tenth of a percent because logarithms are hard :(
    static public void assertApproximately(String message, double expected, double actual, int bitsSimilarCount) {
        Assert.assertAtMost(51, bitsSimilarCount);
        Assert.assertAtLeast(1, bitsSimilarCount);
        double ratio = Math.pow(2, -bitsSimilarCount);
        double offset = Math.abs(expected != 0 ? expected * ratio : actual * ratio);
        double min = expected - offset;
        double max = expected + offset;
        if (max == Double.POSITIVE_INFINITY && expected > 0) {
            max = Double.MAX_VALUE;
        } else if (min == Double.NEGATIVE_INFINITY && expected < 0) {
            min = -Double.MAX_VALUE;
        }

        if (actual < min || actual > max) {
            String range = min + " to " + max;
            failComparison("Expected: ", message, range, actual);
        }
    }
}
