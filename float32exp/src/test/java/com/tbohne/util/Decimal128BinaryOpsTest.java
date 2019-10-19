package com.tbohne.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.tbohne.util.Decimal128TestUtils.FULL_ACCURACY;
import static com.tbohne.util.Decimal128TestUtils.assertApproximately;

@RunWith(BlockJUnit4ClassRunner.class)
public class Decimal128BinaryOpsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    Decimal128 mDecimal = new Decimal128();

    @Test
    public void when2097mod1039ThenResultIs19() {
        assertApproximately( 19.0, mDecimal.set(2097).remainder(1039), 6);
    }
}