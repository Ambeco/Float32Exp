package com.tbohne.util.math.combinitorics;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;

public class Float64ExpUnaryCombinitoricsBase {

    @Parameters(name = "{0}")
    public static ArrayList<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>(11);
        for(int i=0; i<13; ++i) {
            list.add(new Object[]{6-i});
        }
        return list;
    }

    @Parameter(0)  public int left;
    @Rule public final ExpectedException expectedException = ExpectedException.none();
}