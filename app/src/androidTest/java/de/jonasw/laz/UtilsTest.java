package de.jonasw.laz;

import junit.framework.TestCase;

/**
 * Created by thriqon on 4/30/15.
 */
public class UtilsTest extends TestCase {
    public void testMinNull() {
        try {
            Utils.min();
            fail();
        } catch (Exception e) {
        }
        try {
            Utils.min(null);
            fail();
        } catch (Exception e) {
        }
    }

    public void testMinEasy() {
        assertEquals(0.5f, Utils.min(1f, 0.5f, 2f), 0.1f);
        assertEquals(Integer.valueOf(1), Utils.min(3, 1, 50));
        assertEquals(Integer.valueOf(2), Utils.min(2));
    }

    public void testMaxEasy() {
        assertEquals(2f, Utils.max(1f, 0.5f, 2f), 0.1f);
        assertEquals(Integer.valueOf(50), Utils.max(3, 1, 50));
        assertEquals(Integer.valueOf(2), Utils.max(2));
    }
}
