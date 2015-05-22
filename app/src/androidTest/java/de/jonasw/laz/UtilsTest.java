package de.jonasw.laz;

import junit.framework.TestCase;

/**
 * Created by thriqon on 4/30/15.
 */
public class UtilsTest extends TestCase {

    public void testMinEasy() {
        assertEquals(Integer.valueOf(2), Utils.min(2, 3));
        assertEquals(Integer.valueOf(2), Utils.min(3, 2));
    }
}
