package com.flinters.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AdAggregator {

    private final Path inputPath;
    private long totalLinesProcessed = 0;
    private long validLinesProcessed = 0;
    private long errorLinesCount = 0;

    public AdAggregator(Path inputPath) {
        this.inputPath = inputPath;
    }

    public Map<String, CampaignStats> aggregate() throws IOException {
        Map<String, CampaignStats> aggregationMap = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                totalLinesProcessed++;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                try {
                    CsvLineParser.ParsedRecord record = CsvLineParser.parse(line);
                    aggregationMap.computeIfAbsent(record.getCampaignId(), CampaignStats::new)
                            .add(record.getImpressions(), record.getClicks(), record.getSpend(), record.getConversions());
                    validLinesProcessed++;
                } catch (IllegalArgumentException e) {
                    errorLinesCount++;
                    if (errorLinesCount <= 10) {
                        System.err.println("[WARN] Line " + totalLinesProcessed + " error: " + e.getMessage());
                    }
                }
            }
        }
        return aggregationMap;
    }

    public List<CampaignStats> getTop10Ctr(Collection<CampaignStats> statsCollection) {
        PriorityQueue<CampaignStats> minHeap = new PriorityQueue<>(11, Comparator.comparingDouble(CampaignStats::getCtr));

        for (CampaignStats stats : statsCollection) {
            minHeap.offer(stats);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<CampaignStats> result = new ArrayList<>(minHeap);
        result.sort((a, b) -> Double.compare(b.getCtr(), a.getCtr()));
        return result;
    }

    public List<CampaignStats> getTop10Cpa(Collection<CampaignStats> statsCollection) {
        PriorityQueue<CampaignStats> maxHeap = new PriorityQueue<>(11, (a, b) -> Double.compare(b.getCpa(), a.getCpa()));

        for (CampaignStats stats : statsCollection) {
            Double cpa = stats.getCpa();
            if (cpa == null) {
                continue;
            }

            maxHeap.offer(stats);
            if (maxHeap.size() > 10) {
                maxHeap.poll();
            }
        }

        List<CampaignStats> result = new ArrayList<>(maxHeap);
        result.sort(Comparator.comparingDouble(CampaignStats::getCpa));
        return result;
    }

    public long getTotalLinesProcessed() { return totalLinesProcessed; }
    public long getValidLinesProcessed() { return validLinesProcessed; }
    public long getErrorLinesCount() { return errorLinesCount; }
}