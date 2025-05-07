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
---

## **Task 3: Sentiment Scoring**
This step assigns sentiment scores to texts by mapping words to sentiment values using a sentiment lexicon, ensuring that scores are traceable to individual books using a MapReduce job.

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
docker cp shared-folder/input/data/AFINN-111.txt resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/
```

### **4️⃣ Run Hadoop Job**
```sh
docker exec -it resourcemanager /bin/bash
cd /opt/hadoop-2.7.4/share/hadoop/mapreduce/
hadoop fs -put ./AFINN-111.txt /input/dataset
hadoop jar code/SentimentScoringJob.jar /output/part-r-00000 /output/task_3
hadoop fs -cat /output/task_3/*
hdfs dfs -get /output/task_3 /opt/hadoop-2.7.4/share/hadoop/mapreduce/
exit
docker cp resourcemanager:/opt/hadoop-2.7.4/share/hadoop/mapreduce/task_3/ shared-folder/output/Task3
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


# Task 4: Trend Analysis & Aggregation

## 📌 Objective
The goal of Task 4 is to analyze sentiment scores over time by aggregating them into broader time intervals (decades) to observe long-term trends and correlations with historical events.

## 📖 Implementation Notes
- **Scope:** Implemented using Hadoop MapReduce in Java.
- **Input:** The sentiment score output from Task 3.
- **Output:** Aggregated sentiment scores by (BookID | Decade).
- **Time Binning:** Maps each year to its decade (e.g., 2004 → 2000s).

## ⚙ Workflow Overview

### 1️⃣ Mapper
- **Input:** Sentiment score lines in the format: `|`
- **Steps:**
  1. Parse `bookID`, `year`, and `sentimentScore`.
  2. Calculate the decade as `(year / 10) * 10`.
  3. Emit key-value pair: **Key = `|`**, **Value = `sentimentScore`**.

### 2️⃣ Reducer
- **Input:** Aggregated scores for `(bookID | decade)`.
- **Steps:**
  1. Sum all sentiment scores for each `(bookID | decade)`.
  2. Output the aggregated result.


## 🛠 Setup & Execution Commands

### 1️⃣ Start Hadoop Cluster
```bash
docker compose up -d
```
✅ Starts all Hadoop services.

### 2️⃣ Build & Package the Code

### 3️⃣ Copy JAR to Hadoop Container

✅ Moves the JAR into Hadoop.

### 4️⃣ Upload Task 3 Output to HDFS

### 5️⃣ Run MapReduce Job
✅ Executes the job and performs trend analysis.

### 6️⃣ Retrieve Output from HDFS

---

# Task 5 - Hive UDF Bigram Extraction - Complete Commands

## Objective
Extract and analyze bigrams (pairs of consecutive words) using a custom Hive UDF implemented in Java. The UDF processes lemmatized text data output from Task 2.

## Implementation Notes
- **Scope:** UDF written in Java.
- **Input:** Lemmatized text from Task 2.

## Instructions
### Hive UDF Development
- **Functionality:** Java UDF that accepts a block of text, tokenizes it, generates bigrams, and emits them.
- **Return:** List of bigrams.

### Hive Table and Query Process
- **1. Table Creation:** Create a table to store the dataset.
- **2. Data Loading:** Load the lemmatized data from Task 2.
- **3. Querying:** Use the UDF to extract bigrams and calculate their frequencies.
- **4. Output:** Analyze patterns and link to sentiment trends.

---

## 1. Build and Package UDF

## 2. Copy Files into Docker Containers
```bash
docker cp target/BigramUDFJob.jar hive-server:/opt/
docker cp shared-folder/input/data/task5_input namenode:/opt/
```

## 3. Load Data into HDFS
```bash
docker exec -it namenode /bin/bash
hdfs dfs -mkdir -p /data/task5_input
hdfs dfs -put -f /opt/task5_input.txt /data/task5_input/
hdfs dfs -ls /data/task5_input/
exit
```

## 4. Start Hive Session
```bash
docker exec -it hive-server /bin/bash
hive
```

## 5. Hive SQL Commands
```sql
DROP TABLE IF EXISTS task5_input;


CREATE TABLE task5_input (
  book_word_year STRING,
  frequency INT
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n';

LOAD DATA INPATH '/data/task5_input/task5_input' INTO TABLE task5_input;
book_word_year 
SELECT * FROM task5_input LIMIT 5;

DROP TABLE IF EXISTS task5_input_clean;

CREATE TABLE task5_input_clean AS
SELECT 
  split(book_word_year, '\\,')[0] AS book_id,
  split(book_word_year, '\\,')[1] AS word,
  split(book_word_year, '\\,')[2] AS year,
  frequency
FROM task5_input;

SELECT * FROM task5_input_clean LIMIT 5;

DROP TABLE IF EXISTS book_text;

CREATE TABLE book_text AS
SELECT
  concat(book_id, ',', year) AS book_year,
  concat_ws(' ', collect_list(word)) AS all_words
FROM task5_input_clean
GROUP BY book_id, year;

SELECT * FROM book_text LIMIT 5;
ADD JAR /opt/BigramUDFJob.jar;
CREATE TEMPORARY FUNCTION bigram_extract AS 'com.example.task5.BigramUDF';

DROP TABLE IF EXISTS book_bigrams;

CREATE TABLE book_bigrams AS
SELECT book_year, bigram
FROM book_text
LATERAL VIEW explode(bigram_extract(all_words)) bigram_table AS bigram;

DROP TABLE IF EXISTS bigram_frequency;

CREATE TABLE bigram_frequency AS
SELECT book_year, bigram, COUNT(*) AS frequency
FROM book_bigrams
GROUP BY book_year, bigram
ORDER BY frequency DESC;

SELECT * FROM bigram_frequency LIMIT 20;

INSERT OVERWRITE LOCAL DIRECTORY '/opt/task5_bigram_output'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
SELECT * FROM bigram_frequency;
```

## 6. Export and Copy Output
```bash
docker exec -it hive-server /bin/bash
ls /opt/task5_bigram_output

exit

docker cp hive-server:/opt/task5_bigram_output /workspaces/word-based-sentiment-scoring-and-trend-analysis-on-texts-team-cloud/shared-folder/output/Task5
```


