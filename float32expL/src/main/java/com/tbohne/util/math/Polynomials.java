package com.tbohne.util.math;

import java.util.List;

//TODO UNIT TEST
public class Polynomials {
    private Polynomials() {
    }

    public static void at(List<? extends IFloat32ExpL> polynomial,
            IFloat32ExpL x,
            Float32ExpL out,
            Float32ExpL tempXPow,
            Float32ExpL tempT) {
        if (polynomial.size() == 0) {
            out.set(0);
            return;
        }
        out.set(polynomial.get(0));
        if (polynomial.size() == 1) {
            return;
        }
        tempXPow.set(1);
        for (int i = 1; i < polynomial.size(); i++) {
            tempXPow.multiply(x);
            IFloat32ExpL coefficient = polynomial.get(i);
            if (!coefficient.equals(ImmutableFloat32ExpL.ZERO)) {
                out.add(tempT.set(tempXPow).multiply(coefficient));
            }
        }
    }

    public static boolean findNextZeroAfter(List<? extends IFloat32ExpL> polynomial,
            IFloat32ExpL greaterThan,
            Float32ExpL out,
            List<Float32ExpL> tempDerivative,
            Float32ExpL temp1,
            Float32ExpL temp2,
            Float32ExpL tempFx,
            Float32ExpL tempFPx) {
        int biggestIdx = findBiggestExponent(polynomial);
        if (biggestIdx == 0) {
            return false;
        } else if (biggestIdx == 1) {
            out.set(polynomial.get(0)).negate().divide(polynomial.get(1));
            return out.greaterThan(greaterThan);
        } else if (biggestIdx == 2) {
            //TRY: x = (-b - sqrt(b^2 - 4ac))/(2a)
            IFloat32ExpL a = polynomial.get(2);
            IFloat32ExpL b = polynomial.get(1);
            IFloat32ExpL c = polynomial.get(0);
            //  step: 4ac
            out.set(4).multiply(a).multiply(c);
            //  step: sqrt(b^2 - 4ac)
            temp1.set(b).pow(ImmutableFloat32ExpL.TWO).subtract(out);
            if (temp1.lessThan(ImmutableFloat32ExpL.ZERO)) { // polynomial never touches zero
                return false;
            }
            //  step: (-b - sqrt(b^2 - 4ac))/(2a)
            out.set(b).negate().subtract(temp1).divide(2).divide(a);
            if (out.greaterThan(greaterThan)) { // this is the result
                return true;
            }
            //TRY: x = (-b + sqrt(b^2 - 4ac))/(2a)
            out.set(b).negate().add(temp1).divide(2).divide(a);
            return out.greaterThan(greaterThan);
        } else {
            //line must be strictly up and to the right
            if (greaterThan.lessThan(ImmutableFloat32ExpL.ZERO)) {
                throw new IllegalArgumentException(
                        "Cannot solve polynomials with N>2 and negative x values. x=" +
                                greaterThan);
            }
            for (int i = 1; i < polynomial.size(); i++) {
                if (polynomial.get(i).lessThan(ImmutableFloat32ExpL.ZERO)) {
                    throw new IllegalArgumentException(
                            "Cannot solve polynomials with N>2 and negative coefficients. Coefficient " +
                                    i + " is " + polynomial.get(i));
                }
            }
            // if f(greaterThan) is already >0, then we're too far. fail fast.
            at(polynomial, greaterThan, out, temp1, temp2);
            if (out.greaterThan(ImmutableFloat32ExpL.ZERO)) {
                return false;
            } else if (out.equals(ImmutableFloat32ExpL.ZERO)) {
                return true;
            }
            //Use Newton's method
            out.set(greaterThan);
            derive(polynomial, tempDerivative);
            // TODO: with a range of (2^64)^(2^64), then 33 iterations isn't always enough
            for (int i = 0; i < 33; i++) {
                at(polynomial, out, tempFx, temp1, temp2);
                at(tempDerivative, out, tempFPx, temp1, temp2);
                //Due to the "strictly up and right" above, this will never Div0.
                //TODO ArithmeticException from overflow?
                out.subtract(tempFx.divide(tempFPx));
                //TODO generic exit condition?
                if (tempFx.lessOrEquals(ImmutableFloat32ExpL.ONE)) {
                    return true;
                }
            }
            return true;
        }
    }

    public static int findBiggestExponent(List<? extends IFloat32ExpL> polynomial) {
        for (int i = polynomial.size() - 1; i >= 0; i--) {
            if (polynomial.get(i).equals(ImmutableFloat32ExpL.ZERO)) {
                return i;
            }
        }
        return 0;
    }

    private static void prepareOut(List<Float32ExpL> out, int length) {
        while (out.size() < length) {
            out.add(new Float32ExpL());
        }
        while (out.size() > length) {
            out.remove(out.size() - 1);
        }
        for (int i = 0; i < length; i++) {
            if (out.get(i) == null) {
                out.set(i, new Float32ExpL());
            }
        }
    }

    public static void assign(List<Float32ExpL> to, List<? extends IFloat32ExpL> from) {
        prepareOut(to, from.size());
        for (int i = 0; i < from.size(); i++) {
            to.get(i).set(from.get(i));
        }
    }

    public static void derive(List<? extends IFloat32ExpL> polynomial, List<Float32ExpL> out) {
        prepareOut(out, polynomial.size() - 1);
        for (int i = 0; i < polynomial.size() - 1; i++) {
            IFloat32ExpL coefficient = polynomial.get(i + 1);
            out.get(i).set(coefficient).multiply(i + 1);
        }
    }

    public static void integrate(List<? extends IFloat32ExpL> polynomial, List<Float32ExpL> out) {
        prepareOut(out, polynomial.size() + 1);
        out.get(0).set(ImmutableFloat32ExpL.ZERO);
        for (int i = 0; i < polynomial.size(); i++) {
            IFloat32ExpL coefficient = polynomial.get(i);
            out.get(i + 1).set(coefficient).divide(i + 1);
        }
    }

    public static void clear(List<Float32ExpL> dest) {
        for (Float32ExpL digit : dest) { digit.set(ImmutableFloat32ExpL.ZERO); }
    }

    public static void add(List<Float32ExpL> dest, List<? extends IFloat32ExpL> values) {
        for (int i = 0; i < dest.size() && i < values.size(); i++) {
            dest.get(i).add(values.get(i));
        }
        for (int i = dest.size(); i < values.size(); i++) {
            Float32ExpL coefficient = new Float32ExpL(values.get(i));
            dest.add(i, coefficient);
        }
    }

    public static void subtract(List<Float32ExpL> dest, List<? extends IFloat32ExpL> values) {
        for (int i = 0; i < dest.size() && i < values.size(); i++) {
            dest.get(i).subtract(values.get(i));
        }
        for (int i = dest.size(); i < values.size(); i++) {
            Float32ExpL coefficient = new Float32ExpL(values.get(i));
            coefficient.negate();
            dest.add(i, coefficient);
        }
    }

    public static void multiply(List<? extends IFloat32ExpL> left,
            List<? extends IFloat32ExpL> right,
            List<Float32ExpL> out,
            Float32ExpL temp) {
        if (left == out || right == out) throw new IllegalArgumentException("out must be unique");
        prepareOut(out, left.size() + right.size() - 1);
        clear(out);
        for (int i = 0; i < left.size(); i++) {
            for (int j = 0; j < right.size(); j++) {
                temp.set(left.get(i)).multiply(right.get(i));
                out.get(i + j).add(temp);
            }
        }
    }

    public static void multiply(List<Float32ExpL> left, IFloat32ExpL right) {
        for (int i = 0; i < left.size(); i++) {
            left.get(i).multiply(right);
        }
    }
}
