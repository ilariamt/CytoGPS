package main;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import toolkit.AggregateCsvForBatchFile;
import toolkit.AggregateJsonForBatchFile;
import toolkit.SummaryStatisticsForBatchFile;


/**
 * @author Lin Zhang
 * Programmer / Software Development Engineer
 * Institute for Informatics
 * Washington University School of Medicine in St. Louis
 * 
 * Date: August 30, 2021 
 * 
 * Modified by ilariamt
 * Date: November 21, 2025 
 */
public class Run {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        try {
            System.out.println(LocalDateTime.now());
            System.out.println("Start");
            
            // Check if a folder path argument is provided
            if (args.length != 1) {
            	System.out.println("ERROR: Please provide the folder path as the only argument.");
            	System.out.println("Example:");
            	System.out.println("  java -jar karyotype.jar /path/to/data/");
            	System.exit(1);
            }
            
            // Get the base folder path from the command-line argument
            java.nio.file.Path basePath = Paths.get(args[0]);
            
            // Resolve input and output file paths relative to the base path
            java.nio.file.Path inputFile = basePath.resolve("cytogps_input.txt");
            java.nio.file.Path outputJson = basePath.resolve("cytogps_output.json");
            java.nio.file.Path outputCsv = basePath.resolve("cytogps_output.csv");
            java.nio.file.Path outputStats = basePath.resolve("cytogps_stats_output.csv");
            
            AggregateJsonForBatchFile.generateJson(inputFile, outputJson);
            AggregateCsvForBatchFile.generateAggregateLGF(inputFile, outputCsv);
            SummaryStatisticsForBatchFile.writeSummaryStatistics(inputFile, outputStats);
            
            System.out.println(LocalDateTime.now());
            System.out.println("Complete");
        } catch (Exception e) {
            e.printStackTrace(); 
            System.out.println(LocalDateTime.now());
            System.out.println("Incomplete");
        }
    }
}
