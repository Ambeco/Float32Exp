package com.tbohne.util.math;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PolynomialsTest {
    private List<Float32ExpL> createList(double... vs) {
        List<Float32ExpL> r = new ArrayList<>(vs.length);
        for (int i = 0; i < vs.length; i++) {
            r.add(i, new Float32ExpL(vs[i]));
        }
        return r;
    }

    private void assertAt(List<Float32ExpL> polynomial, double expected, double x) {
        Float32ExpL y = new Float32ExpL();
        Polynomials.at(polynomial,
                new ImmutableFloat32ExpL(x),
                y,
                new Float32ExpL(),
                new Float32ExpL());
        assertEquals(new ImmutableFloat32ExpL(expected), y);
    }

    @Test
    public void whenInputAllSetThenAtIsCorrect() {
        List<Float32ExpL> in = createList(7, 5, 3);

        assertAt(in, 7, 0);
        assertAt(in, 15, 1);
        assertAt(in, 29, 2);
        assertAt(in, 49, 3);
    }

    @Test
    public void whenInputAllSetThenDeriveCorrect() {
        List<Float32ExpL> in = createList(7, 5, 3);

        List<Float32ExpL> result = new ArrayList<>();
        Polynomials.derive(in, result);

        List<Float32ExpL> expected = createList(5, 6);
        assertEquals(expected, result);
    }

    @Test
    public void whenInputAllSetThenIntegrateCorrect() {
        List<Float32ExpL> in = createList(5, 6);

        List<Float32ExpL> result = new ArrayList<>();
        Polynomials.integrate(in, result);

        List<Float32ExpL> expected = createList(0, 5, 3);
        assertEquals(expected, result);
    }

    @Test
    public void whenDestinationShorterThenValuesThenSumIsCorrect() {
        List<Float32ExpL> dest = createList(5, 6);
        List<Float32ExpL> values = createList(7, 8, 9);

        Polynomials.add(dest, values);

        List<Float32ExpL> expected = createList(12, 14, 9);
        assertEquals(expected, dest);
    }

    @Test
    public void whenDestinationEqualLengthAsValuesThenSumIsCorrect() {
        List<Float32ExpL> dest = createList(5, 6, 10);
        List<Float32ExpL> values = createList(7, 8, 9);

        Polynomials.add(dest, values);

        List<Float32ExpL> expected = createList(12, 14, 19);
        assertEquals(expected, dest);
    }

    @Test
    public void whenDestinationLongerThanValuesThenSumIsCorrect() {
        List<Float32ExpL> dest = createList(5, 6, 10);
        List<Float32ExpL> values = createList(7, 8);

        Polynomials.add(dest, values);

        List<Float32ExpL> expected = createList(12, 14, 10);
        assertEquals(expected, dest);
    }

    @Test
    public void whenDestinationLongerThanValuesThenMultiplyIsCorrect() {
        List<Float32ExpL> first = createList(2, 3, 4);
        List<Float32ExpL> second = createList(5, 6);
        List<Float32ExpL> out = createList();

        Polynomials.multiply(first, second, out, new Float32ExpL());

        List<Float32ExpL> expected = createList(10, 27, 38, 24);
        assertEquals(expected, out);
    }
}