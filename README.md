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
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32        INLINE      1076M       485M        41M       166M       192M 
    1<<64        INLINE      1083M       572M        39M        28M       194M 
    1<<128       INLINE      1085M       612M        32M        25M       186M 
    .
    MULTIPLICATION Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32          256M       505M       293M        13M       369k        84M 
    1<<64          256M       501M       371M        11M       139k        83M 
    1<<128         253M       491M       358M      7653k        41k        75M 
    .
    POWER Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           N/A      1084M       308M        N/A      2569k      7707k 
    1<<64           N/A      1119M       301M        N/A      1558k       115M 
    1<<128          N/A      1080M       330M        N/A       733k       112M 
    .
    TO_STRING Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           28M        N/A        15M      5314k        17M      2707k 
    1<<64           29M        N/A      3037k      1887k      1896k      2339k 
    1<<128          29M        N/A      1070k      1322k      1311k      2627k 
    .
    FROM_STRING Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           N/A        N/A        46M        41M        25M        26M 
    1<<64           N/A        N/A        12M-       12M        26M        26M 
    1<<128          N/A        N/A        10M-       10M        23M        23M 
    .
    LCG_RNG Test:
    EXPONENT        int     double     Double BigInteger BigDecimal Float32Exp
    1<<32           92M        35M        31M      4234k      1856k        13M 
    1<<64           95M        35M        32M      4226k      1835k        15M 
    1<<128          92M        39M        32M      4394k      1914k        16M 

