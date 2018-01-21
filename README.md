Lightweight Mutable Floating Point Type with 32bit exponent

It works like double, but with a 32 binary significant digits, and a 32 bit base-2 exponent.
This equates to ~9.6 decimal digits for both parts.
Note that this is _wildly_ different than the IEEE 754 format binary64, which is 53 and 11
bits.

This somewhat mirrors BigDecimal, except since it's mutating, return types are not directly
assignable.
Also, it doesn't accept a MathContext, everything is round-to-zero. Some functionality that's
tied to the BigDecimal internals are elided, like scale() and precision().

This class does no allocations except when going to/from String, BigDecimal, or BigInteger, and it's
internals are merely two 'int' fields. As such, it's very small, very fast, and pretty simple.

Since this class is very small, very fast, it is, as a consequence, very inaccurate. exponents and
getPowerOf10, toString() and the String constructors are inaccurate. 
They don't round-trip, unfortunately. .toString() is simple, but I believe is shortest length.
 Trailing zeroes should be rare, but can occur due to maximum string lengths.

Virtually any menthod may throw an ArithmeticException, if the number is bigger or smaller than
this type can support. This should be rare in practice unless playing with large powers.

The return types are Float32ExpChainedExpression, allowing you to chain operations, but not
directly reassign to a Float32Exp. This helps prevent accidental mutation.

Compiler error:

     Float32Exp thing(Float32Exp left, Float32Exp right) {
         return left.multiply(right).add(3);
         //error: incompatible types: Float32ExpChainedExpression cannot be converted to Float32Exp
     }

Correct:

     Float32Exp thing(Float32Exp left, Float32Exp right) {
         Float32Exp result = new Float32Exp(left);
         result.multiply(right).add(3);
         return result;
     }
     
 Preliminary performance measurements:

    ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)
    ADDITION Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32        INLINE      1153M       437M        40M       142M       176M 
    1<<64        INLINE      1068M       542M        40M        30M       189M 
    1<<128       INLINE      1139M       673M        34M+       26M       162M 
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
    .
    LCG_RNG Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           95M        33M        27M      3483k      1912k        10M 
    1<<64           86M        34M        29M      4170k      2072k        12M 
    1<<128          87M        35M        29M      4162k      2013k        12M 

