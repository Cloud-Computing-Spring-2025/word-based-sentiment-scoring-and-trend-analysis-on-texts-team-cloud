# multi-stage-sentiment-analysis-Mapreduce_hive
This is Multi-Stage Sentiment Analysis on Historical Literature application is developed with map reduce and hive

For Task1:

docker compose up -d

mvn install

mv target/*.jar shared-folder/input/code/


docker cp shared-folder/input/code/PreprocessingHadoop-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg245.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg105.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg161.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg3913.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg1929.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg421.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/


docker exec -it resourcemanager /bin/bash
cd /opt/hadoop-2.7.4/share/hadoop/mapreduce/
hadoop fs -mkdir -p /input/dataset
hadoop fs -put ./pg245.txt /input/dataset
hadoop fs -put ./pg105.txt /input/dataset
hadoop fs -put ./pg161.txt /input/dataset
hadoop fs -put ./pg3913.txt /input/dataset
hadoop fs -put ./pg1929.txt /input/dataset
hadoop fs -put ./pg421.txt /input/dataset
hadoop jar PreprocessingHadoop-0.0.1-SNAPSHOT.jar com.example.Task1.PreprocessingDriver /input/dataset /output
hadoop fs -cat /output/*
hdfs dfs -get /output /opt/hadoop-2.7.4/share/hadoop/mapreduce/
exit
docker cp resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/output/ shared-folder/output/

For Task 2:

mvn install

mv target/*.jar shared-folder/input/code/
docker cp shared-folder/input/code/ resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker exec -it resourcemanager /bin/bash                                                                   
cd /opt/hadoop-2.7.4/share/hadoop/mapreduce/
hadoop jar code/WordFrequencyJob.jar /output/part-r-00000 /output/task_2
hadoop fs -cat /output/task_2/*
hdfs dfs -get /output/task_2 /opt/hadoop-2.7.4/share/hadoop/mapreduce/
exit
docker cp resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/task_2/ shared-folder/output/Task2
