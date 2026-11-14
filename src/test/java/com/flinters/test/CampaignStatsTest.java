package com.flinters.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class CampaignStatsTest {

    @Test
    void testCalculationMetrics() {
        CampaignStats stats = new CampaignStats("CAMP_001");
        stats.add(1000, 100, 50.0, 5);

        assertEquals(0.10, stats.getCtr(), 0.0001);
        assertEquals(10.0, stats.getCpa(), 0.0001);
    }

    @Test
    void testDivisionByZeroSafety() {
        CampaignStats statsZero = new CampaignStats("CAMP_002");
        statsZero.add(0, 0, 10.0, 0);

        assertEquals(0.0, statsZero.getCtr(), 0.0001);
        assertNull(statsZero.getCpa());
    }
}