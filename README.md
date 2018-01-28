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
         
Performance measurements: ALL MEASUREMENTS OPS/SEC. BIGGER IS BETTER

    ADDITION Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32      4,607,427,941   2,349,495,250     644,806,398-    395,384,519      33,421,334     148,953,627     154,190,291 
    1<<64      5,113,607,308   4,980,059,013     766,516,238     393,849,045      32,663,645      23,655,216     129,850,585 
    1<<128     5,026,641,197   6,308,783,402     922,315,721     362,848,405      24,805,882      17,285,071     126,942,289 
    .
    MULTIPLICATION Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32        190,800,853     112,139,009     372,940,491     185,876,120      10,821,438         340,388      58,779,002 
    1<<64        218,269,278     128,067,845     436,400,629     179,931,120       7,580,530         109,059      68,986,837 
    1<<128       240,127,210     106,588,623     383,531,550     215,053,909       7,229,969          33,660      73,657,493 
    .
    POWER Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32                N/A             N/A     943,765,450     229,475,643             N/A       1,662,065       5,477,082 
    1<<64                N/A             N/A     924,572,823     252,332,340             N/A       1,404,284      93,608,784 
    1<<128               N/A             N/A     936,250,569     249,971,959             N/A         571,979      91,340,681 
    .
    TO_STRING Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32         19,762,960      20,420,571             N/A      12,233,836       4,562,360      16,593,139       1,995,006 
    1<<64         20,793,841      18,852,298             N/A       2,377,186       1,588,049       1,814,792       1,987,534 
    1<<128        22,515,368      21,152,193             N/A         844,104       1,035,589       1,057,059       2,219,720 
    .
    FROM_STRING Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32                N/A             N/A             N/A      20,380,399             N/A      11,013,767      11,904,617 
    1<<64                N/A             N/A             N/A       4,752,529             N/A      10,245,577       8,470,971 
    1<<128               N/A             N/A             N/A       3,734,933             N/A       7,637,151       9,340,145 
    .
    LCG_RNG Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp 
    1<<32         63,253,630      45,014,063      25,876,948      25,028,898       3,332,912       1,902,624      10,666,560 
    1<<64         67,552,853      48,364,158      33,899,798      22,396,981       3,338,078       1,464,358      10,919,055 
    1<<128        73,593,321      34,665,066      26,542,921      23,239,850       3,692,970       1,581,176      13,288,253 
