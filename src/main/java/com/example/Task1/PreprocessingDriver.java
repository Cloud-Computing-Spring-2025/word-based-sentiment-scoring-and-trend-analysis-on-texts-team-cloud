package com.example.Task1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import com.example.Task1.PreprocessingMapper;
import com.example.Task1.PreprocessingReducer;

public class PreprocessingDriver {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: PreprocessingDriver <input path> <output path>");
            System.exit(-1);
        }
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Preprocessing Job");
        job.setJarByClass(PreprocessingDriver.class);
        job.setMapperClass(PreprocessingMapper.class);
        job.setReducerClass(PreprocessingReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        // Optionally set number of reducers (here, 1 to combine all records).
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, new Path(args[0]));  // Folder with your text files.
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output folder.
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
