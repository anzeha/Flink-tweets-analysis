/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.habjan;

import akka.stream.javadsl.Sink;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habjan.model.EsTweet;
import com.habjan.model.Tweet;
import com.habjan.model.TweetUtils;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.streaming.runtime.operators.util.AssignerWithPeriodicWatermarksAdapter;
import org.apache.flink.util.Collector;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="https://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

    public static String TOPIC_NAME = "tweets_uclfinal";
    public static Boolean WINDOWED = false;
    public static final int WINDOW_DURATION = 120;
    public static final int WINDOW_SLIDE = 10;
    public static String ES_INDEX_NAME = "tweets_lan_test";


    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        ParseArgs(args);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "com.habjan");

        InputStream inputStream = StreamingJob.class.getResourceAsStream("/langdetect-183.bin");

        File file = File.createTempFile("langdetect-183", ".bin");
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        LanguageDetectorModel trainedModel = new LanguageDetectorModel(file);

        // load the model
        LanguageDetector languageDetector = new LanguageDetectorME(trainedModel);


        FlinkKafkaConsumerBase<Tweet> kafkaData = new FlinkKafkaConsumer<Tweet>(
                TOPIC_NAME,
                ConfluentRegistryAvroDeserializationSchema.forSpecific(Tweet.class, "http://localhost:8081"),
                properties).setStartFromEarliest();

        //If windowed stream assign timestamp and watermark
        if (WINDOWED) {
            AssignTimestampAndWatermark(kafkaData);
        }



        DataStream<Tweet> stream = env.addSource(kafkaData);

        DataStream<EsTweet> esStream = MapToEsTweet(stream);



        //LEICESTER NEWCASTLE: 1620412200000L
        //ARSENAL CHELSEA: 1619990100000L
        //CHELSEA LEICESTER 1621364760000L
        //CHELSEA CITY UCL GOAL START 1622317380000
        //                            1622317410000
        //CHELSEA CITY UCL INJURY 1622317140000
        //ENGLAND CROATIA EURO GOAL 1623593690000
        //                          1623593770000
        //ENGLAND CROATIA SUBSTITUTE 1623595070000
        //                          1623595150000
        //ENGLAND CROATIA START     1623589190000
        //                          1623589270000
        //ENGLAND CROATIA END       1623595850000
        //                          1623595930000



        /*--------------CALL RIGHT STREAMING JOB (COMMENT OTHERS)---------------------*/
        //CsvStreamingJob(stream, 1623595930000L);
        //WindowedStreamingJob(stream);
        //ELASTIC SINK
        AddLanguageRecognition(esStream, languageDetector);
        esStream.addSink(ESsinkBuilder().build());
        //ELASTIC SINK
        /*----------------------------------------------------------------------------*/

        env.execute("Flink Streaming Java API Skeleton");
    }

    public static void CsvStreamingJob(DataStream<Tweet> stream, long endTimeStamp) {
        stream.map(new MapFunction<Tweet, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(Tweet tweet) throws Exception {
                Tuple2<String, String> t = new Tuple2<String, String>();
                t.f0 = tweet.getCreatedAt().toString();
                t.f1 = PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(tweet.getText().toString()));
                return t;
            }
        }).filter(new FilterFunction<Tuple2<String, String>>() {
            @Override
            public boolean filter(Tuple2<String, String> stringStringTuple2) throws Exception {
                if (stringStringTuple2.f1.startsWith("rt")) return false;
                else return true;
            }
        }).filter(new FilterFunction<Tuple2<String, String>>() {
            @Override
            public boolean filter(Tuple2<String, String> stringStringTuple2) throws Exception {
                if(TweetUtils.ConvertToEpoch(stringStringTuple2.f0) > endTimeStamp) return false;
                return true;
            }
        }).writeAsCsv("file:///home/anze/csv/testlanguageprocess.csv");
    }

    public static void WindowedStreamingJob(DataStream<Tweet> stream) {
        stream.filter(new FilterFunction<Tweet>() {
            @Override
            public boolean filter(Tweet tweet) throws Exception {
                if(TweetUtils.ConvertToEpoch(tweet.getCreatedAt().toString()) > 1622317140000L) return false;
                return true;
            }
        }).map(new MapFunction<Tweet, Tuple2<String, String>>() {
                   @Override
                   public Tuple2<String, String> map(Tweet tweet) throws Exception {
                       Tuple2<String, String> t = new Tuple2<String, String>();
                       t.f0 = tweet.getCreatedAt().toString();
                       t.f1 = PreprocessUtils.CleanGoalTweet(PreprocessUtils.CleanTweet(tweet.getText().toString()));
                       return t;
                   }
        }).windowAll(SlidingEventTimeWindows.of(Time.seconds(WINDOW_DURATION), Time.seconds(WINDOW_SLIDE))).process(new ProcessAllWindowFunction());
    }

    public static void SinkToElasticSearch(DataStream<EsTweet> stream, ElasticsearchSink.Builder<EsTweet> esSinkBuilder) {
        stream.addSink(esSinkBuilder.build());
    }

    public static void AssignTimestampAndWatermark(FlinkKafkaConsumerBase<Tweet> kafkaData) {
        WatermarkStrategy<Tweet> wmStrategy =
                WatermarkStrategy
                        .<Tweet>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> TweetUtils.ConvertToEpoch(event.getCreatedAt().toString()));

        kafkaData.assignTimestampsAndWatermarks(wmStrategy);
    }

    public static ElasticsearchSink.Builder<EsTweet> ESsinkBuilder(){
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        // use a ElasticsearchSink.Builder to create an ElasticsearchSink
        ElasticsearchSink.Builder<EsTweet> esSinkBuilder = new ElasticsearchSink.Builder<EsTweet>(
                httpHosts,
                new ElasticSearchSinkFunction(ES_INDEX_NAME)
        );

        esSinkBuilder.setBulkFlushMaxActions(1);

        return esSinkBuilder;
    }

    public static DataStream<EsTweet> MapToEsTweet(DataStream<Tweet> stream){
        DataStream<EsTweet> esStream = stream.map(new MapFunction<Tweet, EsTweet>() {
            @Override
            public EsTweet map(Tweet tweet) throws Exception {
                EsTweet esTweet = new EsTweet();
                //esTweet.setDetected_language(languages[0].getLang());
                //esTweet.setLanguage_confidence(languages[0].getConfidence());
                esTweet.setCreated_at(TweetUtils.TwitterTSToElasticTS(tweet.getCreatedAt().toString()));
                esTweet.setId(tweet.getId());
                esTweet.setUsername(tweet.getUsername().toString());
                esTweet.setUser_id(tweet.getUserId());
                esTweet.setText(tweet.getText().toString());
                return esTweet;
            }
        });
        return esStream;
    }

    public static void AddLanguageRecognition(DataStream<EsTweet> stream, LanguageDetector languageDetector){
        stream.map(new MapFunction<EsTweet, EsTweet>() {
            @Override
            public EsTweet map(EsTweet esTweet) throws Exception {

                Language[] languages = languageDetector.predictLanguages(PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(esTweet.getText().toString())));
                esTweet.setDetected_language(languages[0].getLang());
                esTweet.setLanguage_confidence(languages[0].getConfidence());
                /*esTweet.setCreated_at(TweetUtils.TwitterTSToElasticTS(tweet.getCreatedAt().toString()));
                esTweet.setId(tweet.getId());
                esTweet.setUsername(tweet.getUsername().toString());
                esTweet.setUser_id(tweet.getUserId());
                esTweet.setText(tweet.getText().toString());*/
                return esTweet;
            }
        });
    }

    public static void ParseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (i == 0 && args[i].equals("W")) {
                WINDOWED = true;
            }
            if (i == 1) {
                TOPIC_NAME = args[i];
            }
        }
    }

}
