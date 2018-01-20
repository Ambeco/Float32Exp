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
    EXPONENT           int     double     Double BigInteger BigDecimal Float32Exp
    1<<32       200000000    1150747     527009      43369     187181     187705 
    1<<64           INLINE   1150747     665335      39976      29852     192585 
    1<<128          INLINE   1153402     673174      34416      28674     193330 
    .
    MULTIPLICATION Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float32Exp
    1<<32          267308     525210     313381      13757        398      86767 
    1<<64          266028     523423     397772      11634        145      82135 
    1<<128         264830     517464     394011       8324         47      81699 
    .
    TO_STRING Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           32331         N/A     19157+      7482-     22014       3146 
    1<<64           33898         N/A      3405       2769       2724       3070 
    1<<128          33489         N/A      1235       1529       1514       3050 
    .
    FROM_STRING Test:
    EXPONENT           int     double     Double BigInteger BigDecimal Float32Exp
    1<<32              N/A        N/A     49079      48095      25085      24937
    1<<64              N/A        N/A     11880      11745      24910      25031
    1<<128             N/A        N/A     10992      11061      20889      20790

