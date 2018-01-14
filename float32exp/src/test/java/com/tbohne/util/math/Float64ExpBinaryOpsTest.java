package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpBinaryOpsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float64Exp mDecimal = new Float64Exp();

    @Test
    public void when2097mod1039ThenResultIs19() {
        Float64ExpTestUtils.assertApproximately( 19.0, mDecimal.set(2097).remainder(1039), 6);
    }
}