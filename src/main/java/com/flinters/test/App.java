package com.flinters.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        String inputFilePath = null;
        String outputDirStr = "./output";

        for (int i = 0; i < args.length; i++) {
            if ("--input".equals(args[i]) && i + 1 < args.length) {
                inputFilePath = args[i + 1];
            } else if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputDirStr = args[i + 1];
            }
        }

        if (inputFilePath == null) {
            System.err.println("Missing required parameter: --input <csv_file_path>");
            System.out.println("Example: java -jar target/ad-performance-aggregator-1.0.0.jar --input data.csv --output ./output");
            System.exit(1);
        }

        Path inputPath = Paths.get(inputFilePath);
        if (!Files.exists(inputPath) || !Files.isReadable(inputPath)) {
            System.err.println("Error: Input file does not exist or cannot be read: " + inputFilePath);
            System.exit(1);
        }

        Path outputDir = Paths.get(outputDirStr);
        try {
            Files.createDirectories(outputDir);
        } catch (java.io.IOException | SecurityException e) {
            System.err.println("Error: Failed to create output directory: " + outputDirStr);
            System.exit(1);
        }

        System.out.println("==================================================");
        System.out.println("STARTING AD DATA PROCESSING");
        System.out.println("Input file: " + inputPath.toAbsolutePath());
        System.out.println("Output directory: " + outputDir.toAbsolutePath());
        System.out.println("==================================================");

        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        try {
            AdAggregator aggregator = new AdAggregator(inputPath);
            Map<String, CampaignStats> statsMap = aggregator.aggregate();

            List<CampaignStats> topCtr = aggregator.getTop10Ctr(statsMap.values());
            List<CampaignStats> topCpa = aggregator.getTop10Cpa(statsMap.values());

            Path topCtrFile = outputDir.resolve("top10_ctr.csv");
            Path topCpaFile = outputDir.resolve("top10_cpa.csv");

            CsvResultWriter.writeTopCtr(topCtrFile, topCtr);
            CsvResultWriter.writeTopCpa(topCpaFile, topCpa);

            long endTime = System.currentTimeMillis();
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsedMB = (memoryAfter - memoryBefore) / (1024 * 1024);

            System.out.println("--------------------------------------------------");
            System.out.println("PROCESSING COMPLETED SUCCESSFULLY!");
            System.out.println("- Total lines processed: " + aggregator.getTotalLinesProcessed());
            System.out.println("- Valid lines processed: " + aggregator.getValidLinesProcessed());
            System.out.println("- Error/skipped lines: " + aggregator.getErrorLinesCount());
            System.out.println("- Unique campaigns: " + statsMap.size());
            System.out.println("- Execution time: " + (endTime - startTime) + " ms");
            System.out.println("- Estimated Heap memory consumed: ~" + Math.max(0, memoryUsedMB) + " MB");
            System.out.println("- Results saved at: " + outputDir.toAbsolutePath());
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("Unknown error occurred during processing: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}