package com.example.Task3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class SentimentScoringMapper extends Mapper<Object, Text, Text, IntWritable> {

    private HashMap<String, Integer> sentimentLexicon = new HashMap<>();
    private Text compositeKey = new Text();
    private final static IntWritable scoreWritable = new IntWritable();

    @Override
    protected void setup(Context context) throws IOException {
        // Load the sentiment lexicon from a file (e.g., AFINN-111.txt)
        BufferedReader reader = new BufferedReader(new FileReader("/opt/hadoop-2.7.4/share/hadoop/mapreduce/AFINN-111.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                String word = parts[0].toLowerCase().trim();
                try {
                    int score = Integer.parseInt(parts[1].trim());
                    sentimentLexicon.put(word, score);
                } catch (NumberFormatException e) {
                    // skip invalid entries
                }
            }
        }
        reader.close();
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Expected input: "105_1994_persuasion<TAB>project gutenberg ebook persuasion ebook use anyone anywhere united states most other parts world no cost"
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        
        String[] parts = line.split("\t", 2);
        if (parts.length < 2) return;
        
        // Parse header to extract bookID and year (assuming format "bookID_year_title")
        String header = parts[0].trim();
        String[] headerParts = header.split("_", 3);
        if (headerParts.length < 2) return;
        String bookID = headerParts[0].trim();
        String year = headerParts[1].trim();
        
        // Create composite key: "bookID, year"
        compositeKey.set(bookID + ", " + year);
        
        // Process the text: convert to lowercase, remove non-alphanumeric characters, and tokenize.
        String text = parts[1].toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = text.split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (sentimentLexicon.containsKey(token)) {
                int score = sentimentLexicon.get(token);
                scoreWritable.set(score);
                context.write(compositeKey, scoreWritable);
            }
        }
    }
}
