package com.flinters.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class CsvResultWriter {

    public static void writeTopCtr(Path outputPath, List<CampaignStats> list) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("campaign_id,total_impressions,total_clicks,ctr");
            writer.newLine();
            for (CampaignStats stats : list) {
                writer.write(String.format(Locale.US, "%s,%d,%d,%.6f",
                        stats.getCampaignId(),
                        stats.getTotalImpressions(),
                        stats.getTotalClicks(),
                        stats.getCtr()
                ));
                writer.newLine();
            }
        }
    }

    public static void writeTopCpa(Path outputPath, List<CampaignStats> list) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("campaign_id,total_spend,total_conversions,cpa");
            writer.newLine();
            for (CampaignStats stats : list) {
                Double cpa = stats.getCpa();
                writer.write(String.format(Locale.US, "%s,%.2f,%d,%.2f",
                        stats.getCampaignId(),
                        stats.getTotalSpend(),
                        stats.getTotalConversions(),
                        cpa != null ? cpa : 0.0
                ));
                writer.newLine();
            }
        }
    }
}