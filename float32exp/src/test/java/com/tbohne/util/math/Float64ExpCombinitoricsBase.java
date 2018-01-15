package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

public class Float64ExpCombinitoricsBase {

    @Parameterized.Parameters(name = "{0} and {1}")
    public static ArrayList<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>(13*13);
        for(int i=0; i<15; ++i) {
            for(int j=0; j<15; ++j) {
                list.add(new Object[]{7-i, 7-j});
            }
        }
        return list;
    }

    @Parameterized.Parameter(0)  public int left;
    @Parameterized.Parameter(1)  public int right;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
}