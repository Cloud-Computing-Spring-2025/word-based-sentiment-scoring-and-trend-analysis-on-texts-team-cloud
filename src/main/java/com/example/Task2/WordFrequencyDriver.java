package com.example.Task2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordFrequencyDriver {
    public static void main(String[] args) throws Exception {
        // Check if the correct number of arguments are provided
        if (args.length != 2) {
            System.err.println("Usage: WordFrequencyDriver <input path> <output path>");
            System.exit(-1);
        }

        // Use arguments for paths
        String inputPath = args[0];
        String outputPath = args[1];

        // Debugging the arguments
        System.out.println("Arguments Length: " + args.length);
        System.out.println("Input Path: " + inputPath);
        System.out.println("Output Path: " + outputPath);

        // Set up configuration and job
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Word Frequency Analysis");

        // Set the Jar class
        job.setJarByClass(WordFrequencyDriver.class);

        // Set the Mapper, Reducer, and Combiner classes
        job.setMapperClass(WordFrequencyMapper.class);
        job.setCombinerClass(WordFrequencyReducer.class);
        job.setReducerClass(WordFrequencyReducer.class);

        // Set output key and value classes
        job.setOutputKeyClass(Text.class); // Key is word
        job.setOutputValueClass(Text.class); // Value is word frequency

        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(inputPath)); // Input file path
        FileOutputFormat.setOutputPath(job, new Path(outputPath)); // Output folder path

        // Wait for job completion and exit with status
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
