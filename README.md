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
         
ALL MEASUREMENTS OPS/SEC (BIGGER IS BETTER)

    ADDITION Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32      6,342,628,807   3,258,726,598     828,154,423     505,996,368      43,988,682     187,424,041     191,308,057     192,593,922 
    1<<64      6,439,357,351   6,196,362,734   1,132,467,631     511,280,449      38,667,992      29,341,110     183,155,725     196,258,190 
    1<<128     6,488,836,496   6,189,874,396   1,123,175,589     512,572,494      34,757,746      25,446,723     181,799,973     194,262,344 
    .
    MULTIPLICATION Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32        254,018,867     156,021,741     444,032,679     310,886,280      13,784,152         419,439      84,804,395      87,003,933 
    1<<64        256,329,880     152,709,626     504,967,951     312,539,930      12,026,254         148,533      88,243,668      92,289,996 
    1<<128       252,689,919     154,171,962     512,717,863     303,918,139       8,893,666          48,134      87,762,688      92,857,716 
    .
    POWER Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32                N/A             N/A   1,141,937,231     321,586,596             N/A       2,665,072       8,124,188       7,912,321 
    1<<64                N/A             N/A   1,125,726,171     313,315,837             N/A       1,710,042     129,085,518     211,141,511 
    1<<128               N/A             N/A   1,118,708,849     318,740,697             N/A         797,471     124,014,448     206,207,774 
    .
    TO_STRING Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32         29,802,291      29,631,884             N/A      17,376,723       5,779,660      19,736,649       2,916,923       3,107,236 
    1<<64         28,996,323      28,173,111             N/A       3,289,084       2,290,149       2,284,298       2,927,266       3,018,855 
    1<<128        29,745,271      29,032,421             N/A       1,132,609       1,330,495       1,333,931       2,890,818       3,195,442 
    .
    FROM_STRING Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32                N/A             N/A             N/A      24,411,831             N/A      12,902,499      13,033,920      13,150,982 
    1<<64                N/A             N/A             N/A       6,114,777             N/A      13,387,801      12,844,455      13,077,156 
    1<<128               N/A             N/A             N/A       5,614,576             N/A      12,006,899      11,263,599      11,553,060 
    .
    LCG_RNG Test:
    EXPONENT             int            long          double          Double      BigInteger      BigDecimal      Float32Exp     Float32ExpL 
    1<<32         91,041,120      53,073,676      36,881,320      31,490,817       4,368,391       2,054,701      13,995,987      15,772,852 
    1<<64         93,288,319      53,849,781      38,925,406      33,318,075       4,423,725       2,023,623      15,964,495      15,675,911 
    1<<128        93,500,890      54,472,735      38,623,353      32,893,925       4,475,823       2,038,338      16,449,002      16,053,154 