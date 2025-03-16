package com.example.Task2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordFrequencyReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Map<String, Integer> wordCount = new HashMap<>();

        // Count word occurrences for each book ID
        for (Text word : values) {
            String lemma = word.toString();
            wordCount.put(lemma, wordCount.getOrDefault(lemma, 0) + 1);
        }

        // Format output
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            result.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
        }

        context.write(key, new Text(result.toString()));
    }
}
