package com.tbohne.util.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Float64ExpLBinaryOpsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Float32ExpL mDecimal = new Float32ExpL();

    @Test
    public void when2097mod1039ThenResultIs19() {
        //TODO: Why only 6 bits?
        Float64ExpLTestUtils.assertApproximately( 19.0, mDecimal.set(2097).remainder(1039), 6);
    }

    //TODO:Delete. Just measuring how doubles work.
    @Test
    public void fmodWorksLikeIThink() {
        Float64ExpLTestUtils.assertApproximately( -2.0, (int)((-7.0) / 3.0), 30);
        Float64ExpLTestUtils.assertApproximately( -2.0, (int)(7.0 / -3.0), 30);
        Float64ExpLTestUtils.assertApproximately( -1.0, (int)((-7.0) % 3.0), 30);
        Float64ExpLTestUtils.assertApproximately( 1.0, (int)(7.0 % -3.0), 30);
    }
}