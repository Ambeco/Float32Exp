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
    EXPONENT         int      double      Double  BigInteger  BigDecimal  Float32Exp
    1<<32         INLINE  1134434044   492704281    41095831   173457560   175700683 
    1<<64         INLINE  1068399823   603965030    37389367    28007490   184854159 
    1<<128        INLINE  1111533978   639363704    33176735    23684728   188183753 
    .
    MULTIPLICATION Test:
    EXPONENT         int      double      Double  BigInteger  BigDecimal  Float32Exp
    1<<32      262450659   507122707   301725113    13380192      373630    80773839 
    1<<64      252492946   512579425   381199252    11167816      136530    79678691 
    1<<128     259689895   485770172   363510786     7897309       44818    78689863 
    .
    POWER Test:
    EXPONENT         int      double      Double  BigInteger  BigDecimal  Float32Exp
    1<<32            N/A  1097334515   276110072         N/A     2540937     4611079 
    1<<64            N/A  1074278962   282397805         N/A     1721354     4755699 
    1<<128           N/A  1086783222   304313954         N/A      961540     4612556 
    .
    TO_STRING Test:
    EXPONENT         int      double      Double  BigInteger  BigDecimal  Float32Exp
    1<<32       30634682         N/A    18610604     7025154    20925659     2956760 
    1<<64       31820116         N/A     3386132     2437775     2455601     2797460 
    1<<128      31128617         N/A     1155048     1391300     1354676     2857539 
    .
    FROM_STRING Test:
    EXPONENT         int      double      Double  BigInteger  BigDecimal  Float32Exp
    1<<32            N/A         N/A    47356281    47155250    27254054    26797716 
    1<<64            N/A         N/A    11951026    11792738    27502331    27742472 
    1<<128           N/A         N/A    10889070    10777112    25803469    25245178 

