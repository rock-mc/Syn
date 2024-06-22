package com.rock_mc.syn.utlis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void parseTime() {
        long[] result = Utils.parseTime(new String[]{"log", "t:2h"});
        assertEquals(7200, result[0]);
        assertEquals(0, result[1]);
    }
}