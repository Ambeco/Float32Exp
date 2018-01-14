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
