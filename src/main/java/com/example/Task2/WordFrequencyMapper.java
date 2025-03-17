package com.example.Task2;

import edu.stanford.nlp.simple.Sentence;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordFrequencyMapper extends Mapper<Object, Text, Text, IntWritable> {
    private Text compositeKey = new Text();
    private final static IntWritable one = new IntWritable(1);

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Each input line is expected to be in the format:
        // "105_1994_persuasion<TAB>project gutenberg ebook persuasion ebook use anyone anywhere united states most other parts world no cos"
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        
        String[] parts = line.split("\t", 2);
        if (parts.length < 2) return;  // Skip malformed lines
        
        // Parse the header part "105_1994_persuasion"
        String header = parts[0].trim();
        String[] headerParts = header.split("_", 3);
        if (headerParts.length < 3) return;
        String bookID = headerParts[0].trim();
        String year = headerParts[1].trim();
        // The title (headerParts[2]) is available if needed, but for key we need only bookID and year.
        
        // Get the cleaned text from Task 1 output
        String text = parts[1].trim();
        
        // Use Stanford CoreNLP Simple API to perform lemmatization.
        // The Sentence class automatically tokenizes and lemmatizes the input text.
        Sentence sentence = new Sentence(text);
        List<String> lemmas = sentence.lemmas();
        
        // Emit key-value pairs: key "bookID, lemma, year" and value 1.
        for (String lemma : lemmas) {
            if (lemma != null && !lemma.isEmpty()) {
                compositeKey.set(bookID + ", " + lemma + ", " + year);
                context.write(compositeKey, one);
            }
        }
    }
}
