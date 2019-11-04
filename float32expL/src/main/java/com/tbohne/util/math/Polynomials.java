package com.tbohne.util.math;

import java.util.ArrayList;
import java.util.List;

//TODO UNIT TEST
public class Polynomials {
    public static Float32ExpL at(List<? extends IFloat32ExpL> polynomial, IFloat32ExpL x) {
        Float32ExpL out =  new Float32ExpL();
        at(polynomial, x, out);
        return out;
    }

    public static void at(List<? extends IFloat32ExpL> polynomial, IFloat32ExpL x, Float32ExpL out) {
        if (polynomial.size() == 0) {
            out.set(0);
            return;
        }
        out.set(polynomial.get(0));
        if (polynomial.size() == 1) {
            return;
        }
        Float32ExpL xpow = new Float32ExpL(1);
        Float32ExpL t = new Float32ExpL();
        for (int i = 1; i < polynomial.size(); i++) {
            xpow.multiply(x);
            IFloat32ExpL pow = polynomial.get(i);
            if (!pow.equals(ImmutableFloat32ExpL.ZERO)) {
                out.add(t.set(xpow).multiply(pow));
            }
        }
    }

    public static List<ImmutableFloat32ExpL> toImmutable(List<? extends IFloat32ExpL> polynomial) {
        List<ImmutableFloat32ExpL> result = new ArrayList<>(polynomial.size());
        for(int i=0; i<polynomial.size(); i++) {
            result.add(polynomial.get(i).toImmutable());
        }
        return result;
    }

    private static void prepareOut(List<Float32ExpL> out, int length) {
        while(out.size() < length) {
            out.add(new Float32ExpL());
        }
        while(out.size() > length) {
            out.remove(out.size() - 1);
        }
        for (int i = 0; i < length; i++) {
            if (out.get(i) == null) {
                out.set(i, new Float32ExpL());
            }
        }
    }

    public static List<Float32ExpL> derive(List<? extends IFloat32ExpL> polynomial) {
        List<Float32ExpL> out = new ArrayList<>(polynomial.size() - 1);
        derive(polynomial, out);
        return out;
    }

    public static void derive(List<? extends IFloat32ExpL> polynomial, List<Float32ExpL> out) {
        prepareOut(out, polynomial.size() - 1);
        for (int i = 0; i < polynomial.size() - 1; i++) {
            IFloat32ExpL val = polynomial.get(i + 1);
            out.get(i).set(val).multiply(i + 1);
        }
    }

    public static List<Float32ExpL> integrate(List<? extends IFloat32ExpL> polynomial) {
        List<Float32ExpL> out = new ArrayList<>(polynomial.size() + 1);
        integrate(polynomial, out);
        return out;
    }

    public static void integrate(List<? extends IFloat32ExpL> polynomial, List<Float32ExpL> out) {
        prepareOut(out, polynomial.size() + 1);
        out.set(0, new Float32ExpL(0));
        for (int i = 0; i < polynomial.size(); i++) {
            IFloat32ExpL val = polynomial.get(i);
            out.get(i + 1).set(val).divide(i + 1);
        }
    }

    public static void sum(List<Float32ExpL> dest, List<? extends IFloat32ExpL> values) {
        for (int i = 0; i < dest.size() && i < values.size(); i++) {
            dest.get(i).add(values.get(i));
        }
        for (int i = dest.size(); i < values.size(); i++) {
            IFloat32ExpL val = values.get(i);
            dest.add(i, new Float32ExpL(val));
        }
    }

    private Polynomials() {}
}
