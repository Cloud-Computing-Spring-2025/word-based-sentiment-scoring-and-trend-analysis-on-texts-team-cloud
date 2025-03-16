package com.example.Task2;

import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordFrequencyMapper extends Mapper<Object, Text, Text, Text> {
    private Text bookID = new Text();
    private Text word = new Text();
    private LemmatizerME lemmatizer;

    @Override
    protected void setup(Context context) throws IOException {
        // Load the lemmatizer model
        InputStream modelInputStream = new FileInputStream("/workspaces/word-based-sentiment-scoring-and-trend-analysis-on-texts-team-cloud/src/main/java/com/example/models/en-lemmatizer.bin");
        LemmatizerModel model = new LemmatizerModel(modelInputStream);
        lemmatizer = new LemmatizerME(model);
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] parts = value.toString().split("\t", 2);
        if (parts.length < 2) return; // Skip malformed lines

        String bookIDStr = parts[0].trim();
        bookID.set(bookIDStr);

        String text = parts[1].toLowerCase().replaceAll("[^a-zA-Z ]", ""); // Clean text
        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(text);

        String[] posTags = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) posTags[i] = "NN"; // Default POS tag

        String[] lemmas = lemmatizer.lemmatize(tokens, posTags);
        for (String lemma : lemmas) {
            if (!lemma.equals("O")) { // Exclude "O" (no lemma found)
                word.set(lemma);
                context.write(bookID, word);
            }
        }
    }
}
