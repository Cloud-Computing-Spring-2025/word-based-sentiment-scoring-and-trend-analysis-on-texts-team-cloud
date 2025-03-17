# Multi-Stage Sentiment Analysis with MapReduce and Hive

This project performs **Multi-Stage Sentiment Analysis on Historical Literature** using **Hadoop MapReduce** and **Hive**.

## **Prerequisites**
- Docker installed
- Maven installed
- Hadoop 2.7.4 running in a Docker container
- Dataset (historical texts) downloaded

---

## **Task 1: Preprocessing**
This step processes the raw text data by cleaning, tokenizing, and preparing it for sentiment analysis.

### **1️⃣ Start Docker Containers**
```sh
docker compose up -d
```

### **2️⃣ Build the Maven Project**
```sh
mvn install
```

### **3️⃣ Move JAR Files to the Shared Folder**
```sh
mv target/*.jar shared-folder/input/code/
```

### **4️⃣ Copy JAR and Data Files into Hadoop Docker Container**
```sh
docker cp shared-folder/input/code/PreprocessingHadoop-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg245.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg105.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg161.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg3913.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg1929.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
docker cp shared-folder/input/data/pg421.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
```

### **5️⃣ Run Hadoop Job for Preprocessing**
```sh
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
```

---

## **Task 2: Word Frequency Analysis**
This step computes word frequencies and applies lemmatization using a MapReduce job.

### **1️⃣ Build the Maven Project**
```sh
mvn install
```

### **2️⃣ Move JAR Files to the Shared Folder**
```sh
mv target/*.jar shared-folder/input/code/
```

### **3️⃣ Copy JAR to Hadoop Docker Container**
```sh
docker cp shared-folder/input/code/ resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
```

### **4️⃣ Run Hadoop Job for Word Frequency Analysis**
```sh
docker exec -it resourcemanager /bin/bash
cd /opt/hadoop-2.7.4/share/hadoop/mapreduce/
hadoop jar code/WordFrequencyJob.jar /output/part-r-00000 /output/task_2
hadoop fs -cat /output/task_2/*
hdfs dfs -get /output/task_2 /opt/hadoop-2.7.4/share/hadoop/mapreduce/
exit
docker cp resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/task_2/ shared-folder/output/Task2
```

---

## **Output**
After running both tasks, the final processed data will be stored in:
- **`shared-folder/output/`** for **Task 1 (Preprocessing)**
- **`shared-folder/output/Task2/`** for **Task 2 (Word Frequency Analysis)**

---

## **Notes**
- Ensure that all files are correctly copied into the container before running Hadoop jobs.
- If any step fails, verify the Hadoop logs using:
  ```sh
  hadoop fs -ls /output/
  ```
- This pipeline can be further extended with Hive for additional analysis and visualization.

---

This setup ensures a **smooth multi-stage sentiment analysis workflow** using **MapReduce and Hive** in **Hadoop 2.7.4 Docker environment**.
