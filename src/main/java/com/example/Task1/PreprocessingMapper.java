package com.example.Task1;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class PreprocessingMapper extends Mapper<Object, Text, Text, Text> {

    private StringBuilder contentBuilder = new StringBuilder();
    private String fileName = "";
    private final static Text outKey = new Text();
    private final static Text outValue = new Text();
    private Set<String> stopWords = new HashSet<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Initialize stop words.
         String[] stopArray = {
    "the", "and", "a", "an", "in", "of", "to", "is", "are", "was", "were",
    "it", "this", "that", "on", "for", "with", "as", "by", "at", "from",
    "or", "but", "not", "be", "have", "has", "had", "i", "you", "he", "she",
    "they", "we", "me", "him", "her", "them", "my", "your", "their", "our",
    "so", "do", "does", "did", "will", "would", "can", "could", "just", "about",
    "into", "than", "then", "out", "up", "down", "over", "under", "chapter", "ii",
    "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii",
    "xiii", "xiv", "xv", "xvi", "xvii", "xviii", "xix", "xx", "xxi", "xxii", "xxiii", "xxiv",
    "xxv", "xxvi", "xxvii", "xxviii", "xxix", "xxx", "xxxi", "xxxii", "xxxiii", "xxxiv", "xxxv",
    "xxxvi", "xxxvii", "xxxviii", "xxxix", "xl", "xli", "xlii", "xliii", "xliv", "xlv", "xlvi", "xlvii", "xlviii", "xlix", "l"
};

        for (String sw : stopArray) {
            stopWords.add(sw);
        }
        // Retrieve the file name from the input split.
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        fileName = fileSplit.getPath().getName();
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Accumulate each line from the file.
        contentBuilder.append(value.toString()).append("\n");
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Process the complete file content.
        String content = contentBuilder.toString();
        if (content.isEmpty()) {
            return;
        }
        // Extract metadata: Title and Release Year.
        String title = extractTitle(content);
        String year = extractYear(content);
        if (title.isEmpty()) {
            title = fileName;
        }
        if (year.isEmpty()) {
            year = "unknown";
        }
        String bookID = extractBookID(fileName);

        // Clean the text.
        String cleanedText = cleanText(content);

        // Set composite key as "bookID_year" and value as "title[TAB]cleanedText".
        outKey.set(bookID + "_" + year + "_" + title);
        outValue.set(cleanedText);
        context.write(outKey, outValue);
    }

    // Look for a line starting with "Title:" to extract the title.
    private String extractTitle(String content) {
        Pattern pattern = Pattern.compile("Title:\\s*(.*)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim().toLowerCase();
        }
        return "";
    }

    // Look for a line starting with "Release date:" and extract the first 4-digit number.
    private String extractYear(String content) {
        Pattern pattern = Pattern.compile("Release date:\\s*(.*)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String releaseLine = matcher.group(1);
            Pattern yearPattern = Pattern.compile("\\b(\\d{4})\\b");
            Matcher ym = yearPattern.matcher(releaseLine);
            if (ym.find()) {
                return ym.group(1);
            }
        }
        return "";
    }

    // Extract book ID from the file name (e.g., "pg245.txt" -> "245").
    private String extractBookID(String fileName) {
        Pattern pattern = Pattern.compile("pg(\\d+)\\.txt");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    // Clean the text: convert to lowercase, remove punctuation, and filter out stop words.
    private String cleanText(String text) {
        // Lowercase and remove non-alphanumeric characters.
        String lower = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        StringBuilder sb = new StringBuilder();
        for (String token : lower.split("\\s+")) {
            if (!stopWords.contains(token) && !token.isEmpty()) {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
