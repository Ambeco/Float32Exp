Lightweight Mutable Floating Point Type with 32bit exponent

This class works like `double`, except has less preicsion, and _far_ more range.  This is useful when 
you need to do rough calculations with mind-bogglingly huge numbers. So, for incremental games, and
that's probably about it. Why not `BigDecimal`? Because `BigDecimal` gets _really_ slow with even in
the range of a couple hundred bits. In the range of 1E40, this class is ~6x faster for addition, 
~1720x faster for multiplication, ~96x faster for arbitrary powers, and ~2x faster for `.toString`. 
In fact, even for values close to 30, this class is on par or faster in all operations, except
`BigDecimal.toString` is crazy fast for those values. No operation comes close to the speed of `double`
itself, of course, but if you could use that, you wouldn't be here.

It works like `double`, but with a 32 binary significant digits, and a 32 bit base-2 exponent.
This equates to ~9.6 decimal digits for both parts.
Note that this is _wildly_ different than the IEEE 754 format `binary64`, which is 53 and 11
bits.

This somewhat mirrors `BigDecimal`, except since it's mutating, return types are not directly
assignable.
Also, it doesn't accept a `MathContext`, everything is round-to-zero. Some functionality that's
tied to the `BigDecimal` internals are elided, like `scale()` and `precision()`.

This class does no allocations except when going to/from `String`, `BigDecimal`, or `BigInteger`, and it's
internals are merely two 'int' fields. As such, it's very small, very fast, and pretty simple.

Since this class is very small, very fast, it is, as a consequence, very inaccurate. exponents and
`getPowerOf10`, `toString()` and the `String` constructors are inaccurate. 
They don't round-trip, unfortunately. `.toString()` is simple, but I believe is shortest length.
 Trailing zeroes should be rare, but can occur due to maximum string lengths.

Virtually any menthod may throw an `ArithmeticException`, if the number is bigger or smaller than
this type can support. This should be rare in practice unless playing with large powers.

The return types are `Float32ExpChainedExpression`, allowing you to chain operations, but not
directly reassign to a `Float32Exp`. This helps prevent accidental mutation.

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
     
 Performance measurements:

    ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)
    ADDITION Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32            INLINE  764,229,056  342,339,135   28,845,697  125,330,972  160,459,299 
    1<<64            INLINE  869,426,577  542,690,769   28,706,900   22,962,037  141,610,164 
    1<<128           INLINE  708,506,647  446,995,297   23,609,597   18,566,051  148,584,381 
    .
    MULTIPLICATION Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32       190,611,736  321,450,005  184,201,922    8,434,937      208,332   62,943,668 
    1<<64       185,483,791  290,754,346  241,097,475    6,477,412      109,364   63,996,909 
    1<<128      197,498,354  410,662,215  240,240,240    7,521,555       35,911   60,953,292 
    .
    POWER Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32               N/A  680,876,236  271,352,159          N/A    1,714,875    5,456,444 
    1<<64               N/A  858,993,459  240,500,240          N/A    1,071,958  130,954,664 
    1<<128              N/A  904,203,641  256,016,385          N/A      511,888  130,954,664 
    .
    TO_STRING Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32        20,211,640          N/A   17,555,890+   4,215,093-  12,912,440    2,026,045 
    1<<64        25,038,045          N/A    2,593,322    1,693,178-   1,620,338    1,878,965 
    1<<128       19,268,810          N/A      707,513-     870,113      832,448    1,826,178 
    .
    FROM_STRING Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32               N/A          N/A   32,986,025   33,205,432   18,145,791   18,215,441 
    1<<64               N/A          N/A    8,402,892    8,123,315   19,104,885   20,336,607 
    1<<128              N/A          N/A    7,236,789    6,764,774   14,915,367   13,653,215 
    
    LCG_RNG Test:
    EXPONENT            int       double       Double   BigInteger   BigDecimal   Float32Exp 
    1<<32        57,740,004-  23,684,114   23,931,097    3,320,159    1,417,173-  10,567,839 
    1<<64        59,840,913   27,659,742   19,886,078    3,412,664    1,521,878   12,351,468 
    1<<128       63,800,215   30,010,104   23,257,594    4,105,088    1,364,065   11,895,505 

