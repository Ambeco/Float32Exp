Lightweight Mutable Floating Point Type with 32bit exponent

It works like double, but with a 32 binary significant digits, and a 32 bit base-2 exponent.
This equates to ~9.6 decimal digits for both parts.
Note that this is _wildly_ different than the IEEE 754 format binary128, which is 113 and 15
bits, and also the IEEE 754 format Float128, which is 110 and 17 bits.

This somewhat mirrors BigDecimal, except since it's mutating, return types are not directly
assignable.
Also, it doesn't accept a MathContext, everything is round-to-zero. Some functionality that's
tied to the BigDecimal internals are elided, like scale() and precision().

This class does no allocations except when going to/from String, BigDecimal, or BigInteger, and it's
internals are merely two 'int' fields. As such, it's very small, very fast, and pretty simple.

Since this class is very small, very fast, and so, as a consequence, very inaccurate. exponents and
getPowerOf10 are wildly inaccurate, and as a result, .toString() and the String constructors are
also wildly inaccurate. They don't even come close to round-tripping. .toString() is fast and
simple, and therefore comes with no guarantees about being shortest length, or even not having
trailing zeroes. Trailing zeroes should be rare, but theoretically can occur, due to truncation.

Virtually any menthod may throw a ArithmeticException, if the number is bigger or smaller than
this type can support. This should be rare unless playing with exponents.

The return types are Float64ExpChainedExpression, allowing you to chain operations, but not
directly reassign to a Float64Exp. This helps prevent accidental mutation.

Compiler error:

     Float64Exp thing(Float64Exp left, Float64Exp right) {
         return left.multiply(right).add(3);
         //error: incompatible types: Float64ExpChainedExpression cannot be converted to Float64Exp
     }

Correct:

     void thing(Float64Exp left, Float64Exp right) {
         Float64Exp result = new Float64Exp(left);
         result.multiply(right).add(3);
         return result;
     }

 For reasons I don't understand, the build targets are set up wrong, and Android Studio cannot build
 any part of this. It always believes all files are up-to-date, and trying to run tests says:
     Class not found: "com.tbohne.util.math.NNNTest"Empty test suite.
 As a result, I have to do builds on the command line via something like:
     gradlew clean assemble test
 Once built, Android Studio has no problem testing on these binaries.

 Preliminary performance measurements:

    ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)
    ADDITION Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float64Exp
    1<<32           INLINE   1153402     545553      46513     193199     198412
    1<<64           INLINE   1207729     695652      42732      32464     224466
    1<<128          INLINE   1122964     715051      38026      28903     201511

    MULTIPLICATION Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float64Exp
    1<<32          279485     564015     333333      15038        416      93414
    1<<64          281928     556637     412881      12575        157      90744
    1<<128         286368     558815     421052       9435         51      90130

    POWER Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float64Exp
    1<<32              N/A   1142857     318471         N/A      3520       3355
    1<<64              N/A   1184834     357525         N/A      2660       3298
    1<<128             N/A   1207729     365764         N/A      1824       3145

    TO_STRING Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float64Exp
    1<<32           27586         N/A     12787       5801      19284       3699
    1<<64           27948         N/A      3479       2357       2325       3478
    1<<128          27122         N/A      1172       1385       1365       3556

    FROM_STRING Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float64Exp
    1<<32              N/A        N/A     45766      44786      26025      25094
    1<<64              N/A        N/A     11901      11823      25479      26143
    1<<128             N/A        N/A     11080      11113      23764      23201

