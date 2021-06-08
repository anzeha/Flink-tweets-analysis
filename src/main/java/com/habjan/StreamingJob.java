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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habjan.model.Tweet;
import com.habjan.model.TweetUtils;
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
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
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

import java.time.Duration;
import java.util.*;

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

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "com.habjan");


        FlinkKafkaConsumerBase<Tweet> kafkaData = new FlinkKafkaConsumer<Tweet>(
                "tweets_uclfinal",
                ConfluentRegistryAvroDeserializationSchema.forSpecific(Tweet.class, "http://localhost:8081"),
                properties).setStartFromTimestamp(1622317410000L);

        WatermarkStrategy<Tweet> wmStrategy =
                WatermarkStrategy
                        .<Tweet>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> TweetUtils.ConvertToEpoch(event.getCreatedAt().toString()));

        kafkaData.assignTimestampsAndWatermarks(wmStrategy);

        DataStream<Tweet> stream = env.addSource(kafkaData);

        /*stream.keyBy(value -> value.getId()).window(SlidingEventTimeWindows.of(Time.seconds(500), Time.seconds(100))).process(new ProcessWindowFunction<Tweet, Tweet, Long, TimeWindow>() {
            @Override
            public void process(Long aLong, Context context, Iterable<Tweet> iterable, Collector<Tweet> collector) throws Exception {
                System.out.println(aLong);
                System.out.println("---------Window start------------");
                System.out.println(context.window().toString());
                iterable.forEach((tweet -> {

                    System.out.println(tweet.getId());
                    System.out.println(tweet.getCreatedAt());
                }));
                System.out.println("---------Window stop------------");
            }
        });*/

        stream.windowAll(SlidingEventTimeWindows.of(Time.seconds(500), Time.seconds(100))).process(new ProcessAllWindowFunction<Tweet, Object, TimeWindow>() {
            @Override
            public void process(Context context, Iterable<Tweet> iterable, Collector<Object> collector) throws Exception {

                System.out.println("---------Window start------------");
                System.out.println(context.window().toString());
                iterable.forEach((tweet -> {

                    System.out.println(tweet.getId());
                    System.out.println(tweet.getCreatedAt());
                }));
                System.out.println("---------Window stop------------");
            }
        });

        /*DataStream<Tweet> stream = env.addSource(
                kafkaData.assignTimestampsAndWatermarks(wmStrategy)).keyBy(value -> value.getId()).window(SlidingEventTimeWindows.of(Time.seconds(60), Time.seconds(10))).apply(new WindowFunction<Tweet, Tweet, Long, TimeWindow>() {
            @Override
            public void apply(Long aLong, TimeWindow timeWindow, Iterable<Tweet> iterable, Collector<Tweet> collector) throws Exception {

            }
        });*/
        /*DataStreamSource<Tweet> stream = env.addSource(
                new FlinkKafkaConsumer<>(
                        "tweets_uclfinal",
                        ConfluentRegistryAvroDeserializationSchema.forSpecific(Tweet.class, "http://localhost:8081"),
                        properties).setStartFromTimestamp(1622317410000L));*/

        //LEICESTER NEWCASTLE: 1620412200000L
        //ARSENAL CHELSEA: 1619990100000L
        //CHELSEA LEICESTER 1621364760000L
        //CHELSEA CITY UCL GOAL START 1622317380000
        //                            1622317410000
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        // use a ElasticsearchSink.Builder to create an ElasticsearchSink
        /*ElasticsearchSink.Builder<Tweet> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticSearchSinkFunction("tweets_chelei")
        );

        esSinkBuilder.setBulkFlushMaxActions(1);*/


        // finally, build and add the sink to the job's pipeline
        //stream.addSink(esSinkBuilder.build());

        // execute program

        //CsvStreamingJob(stream);
        env.execute("Flink Streaming Java API Skeleton");
    }

    public static void CsvStreamingJob(DataStreamSource<Tweet> stream) {
        stream.map(new MapFunction<Tweet, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(Tweet tweet) throws Exception {
                Tuple2<String, String> t = new Tuple2<String, String>();
                //String text = tweet.getText().toString();
                //text = text.replaceAll("\\W", " ");
                //text = text.replaceAll("\\s+[a-zA-Z]\\s+", " ");
                //text = text.replaceAll("\\s+", " " );
                //text = text.replaceAll("\\b(g+o+a*l+)+", "goal");
                //text = text.toLowerCase();
                t.f0 = tweet.getCreatedAt().toString();
                t.f1 = PreprocessUtils.CleanGoalTweet(PreprocessUtils.CleanTweet(tweet.getText().toString()));
                return t;
            }
        }).filter(new FilterFunction<Tuple2<String, String>>() {
            @Override
            public boolean filter(Tuple2<String, String> stringStringTuple2) throws Exception {
                if (stringStringTuple2.f1.startsWith("rt")) return false;
                else return true;
            }
        }).writeAsCsv("file:///home/anze/csv/tweets.csv");
    }

}
