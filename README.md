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

The return types are Float32ExpChainedExpression, allowing you to chain operations, but not
directly reassign to a Float32Exp. This helps prevent accidental mutation.

Compiler error:

     Float32Exp thing(Float32Exp left, Float32Exp right) {
         return left.multiply(right).add(3);
         //error: incompatible types: Float32ExpChainedExpression cannot be converted to Float32Exp
     }

Correct:

     void thing(Float32Exp left, Float32Exp right) {
         Float32Exp result = new Float32Exp(left);
         result.multiply(right).add(3);
         return result;
     }
     
 Preliminary performance measurements:

    ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)
    ADDITION Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32        INLINE      1080M       523M        44M       169M       186M 
    1<<64        INLINE      1053M       610M        39M        29M       197M 
    1<<128       INLINE      1088M       660M        35M        25M       162M 
    .
    MULTIPLICATION Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32          261M       526M       316M        14M       397k        89M 
    1<<64          271M       526M       404M        12M       155k        85M 
    1<<128         268M       519M       405M      8927k        50k        86M 
    .
    POWER Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           N/A      1146M       333M        N/A      2768k      4974k 
    1<<64           N/A      1150M       345M        N/A      1668k        72M 
    1<<128          N/A      1051M       300M        N/A       778k        75M 
    .
    TO_STRING Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           28M        N/A        16M      5518k        18M      2926k 
    1<<64           28M        N/A      3150k      2242k      2203k      2871k 
    1<<128          29M        N/A      1080k      1311k      1285k      2878k 
    .
    FROM_STRING Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           N/A        N/A        45M        44M        23M        23M 
    1<<64           N/A        N/A        11M        11M        23M        23M 
    1<<128          N/A        N/A        10M        10M        21M        21M 

