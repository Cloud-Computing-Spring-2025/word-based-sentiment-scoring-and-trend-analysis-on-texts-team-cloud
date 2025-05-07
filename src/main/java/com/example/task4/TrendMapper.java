package com.example.task4;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TrendMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final Text outputKey = new Text();
    private final IntWritable outputValue = new IntWritable();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;

        String[] parts = line.split("\t");
        if (parts.length != 2) return;

        String[] metadata = parts[0].split(",");
        if (metadata.length < 2) return;

        String bookID = metadata[0].trim();
        String yearStr = metadata[metadata.length - 1].trim();

        try {
            int year = Integer.parseInt(yearStr);
            String decade = (year / 10 * 10) + "s";

            // Output format: "bookID,decade"
            outputKey.set(bookID + "," + decade);
            outputValue.set(Integer.parseInt(parts[1].trim()));

            context.write(outputKey, outputValue);
        } catch (NumberFormatException e) {
            // Skip malformed lines
        }
    }
}