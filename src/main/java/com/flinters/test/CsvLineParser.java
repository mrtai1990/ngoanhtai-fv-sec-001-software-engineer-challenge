package com.flinters.test;

public class CsvLineParser {

    public static class ParsedRecord {
        private final String campaignId;
        private final long impressions;
        private final long clicks;
        private final double spend;
        private final long conversions;

        public ParsedRecord(String campaignId, long impressions, long clicks, double spend, long conversions) {
            this.campaignId = campaignId;
            this.impressions = impressions;
            this.clicks = clicks;
            this.spend = spend;
            this.conversions = conversions;
        }

        public String getCampaignId() { return campaignId; }
        public long getImpressions() { return impressions; }
        public long getClicks() { return clicks; }
        public double getSpend() { return spend; }
        public long getConversions() { return conversions; }
    }

    public static ParsedRecord parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Line is empty");
        }

        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Column count mismatch (expected 6, actual:" + parts.length + ")");
        }

        String campaignId = parts[0].trim();
        if (campaignId.isEmpty()) {
            throw new IllegalArgumentException("campaign_id empty");
        }

        try {
            long impressions = Long.parseLong(parts[2].trim());
            long clicks = Long.parseLong(parts[3].trim());
            double spend = Double.parseDouble(parts[4].trim());
            long conversions = Long.parseLong(parts[5].trim());

            if (impressions < 0 || clicks < 0 || spend < 0.0 || conversions < 0) {
                throw new IllegalArgumentException("Negative values are not allowed");
            }
            if (clicks > impressions) {
                throw new IllegalArgumentException("Clicks cannot exceed impressions");
            }

            return new ParsedRecord(campaignId, impressions, clicks, spend, conversions);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
        }
    }
}