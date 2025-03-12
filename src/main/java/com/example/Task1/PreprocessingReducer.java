package com.example.Task1;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PreprocessingReducer extends Reducer<Text, Text, Text, Text> {
    
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        // For our job, there should be one record per file.
        // If multiple records exist, concatenate them.
        StringBuilder aggregated = new StringBuilder();
        for (Text val : values) {
            aggregated.append(val.toString()).append(" ");
        }
        context.write(key, new Text(aggregated.toString().trim()));
    }
}
