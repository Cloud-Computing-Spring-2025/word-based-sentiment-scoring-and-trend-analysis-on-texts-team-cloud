package com.example.task5;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class BigramUDF extends UDF {
    public List<String> evaluate(Text input) {
        if (input == null) {
            return null;
        }

        String text = input.toString().toLowerCase().replaceAll("[^a-z\\s]", "");
        StringTokenizer tokenizer = new StringTokenizer(text);
        List<String> words = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            words.add(tokenizer.nextToken());
        }

        List<String> bigrams = new ArrayList<>();
        for (int i = 0; i < words.size() - 1; i++) {
            bigrams.add(words.get(i) + " " + words.get(i + 1));
        }

        return bigrams;
    }
}