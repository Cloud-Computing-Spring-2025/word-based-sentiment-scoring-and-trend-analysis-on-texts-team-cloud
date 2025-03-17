package com.example.Task3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class SentimentScoringReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
         throws IOException, InterruptedException {
        int cumulativeScore = 0;
        for (IntWritable val : values) {
            cumulativeScore += val.get();
        }
        context.write(key, new IntWritable(cumulativeScore));
    }
}
