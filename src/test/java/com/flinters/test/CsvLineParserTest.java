package com.flinters.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CsvLineParserTest {

    @Test
    void testValidLine() {
        String line = "CAMP_001,2026-11-01,1000,50,25.50,5";
        CsvLineParser.ParsedRecord record = CsvLineParser.parse(line);
        assertNotNull(record);
        assertEquals("CAMP_001", record.getCampaignId());
        assertEquals(1000, record.getImpressions());
        assertEquals(50, record.getClicks());
        assertEquals(25.50, record.getSpend());
        assertEquals(5, record.getConversions());
    }

    @Test
    void testInvalidColumnCount() {
        String line = "CAMP_001,2026-11-01,1000,50,25.50";
        assertThrows(IllegalArgumentException.class, () -> CsvLineParser.parse(line));
    }

    @Test
    void testNegativeValues() {
        String line = "CAMP_001,2026-11-01,-1000,50,25.50,5";
        assertThrows(IllegalArgumentException.class, () -> CsvLineParser.parse(line));
    }

    @Test
    void testInvalidNumberFormat() {
        String line = "CAMP_001,2026-11-01,abc,50,25.50,5";
        assertThrows(IllegalArgumentException.class, () -> CsvLineParser.parse(line));
    }
}