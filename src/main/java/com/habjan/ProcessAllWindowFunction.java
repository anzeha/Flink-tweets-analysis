package com.habjan;

import com.github.wpm.tfidf.TfIdf;
import com.habjan.model.Tweet;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.github.wpm.tfidf.ngram.NgramTfIdf.ngramDocumentTerms;
import static com.github.wpm.tfidf.ngram.NgramTfIdf.termStatistics;

public class ProcessAllWindowFunction extends org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, String>, Object, TimeWindow> {


    @Override
    public void process(Context context, Iterable<Tuple2<String, String>> iterable, Collector<Object> collector) throws Exception {
        System.out.println("---------Window start------------");
        List<String> text = new ArrayList<String>();
        iterable.forEach((tweet -> {
            text.add(tweet.f1);
        }));
        //System.out.println(Arrays.toString(text.toArray()));
        List<Integer> ns = new ArrayList<Integer>();
        ns.add(1);

        Iterable<Collection<String>> documents = ngramDocumentTerms(ns, text);
        Iterable<Map<String, Double>> tfs = TfIdf.tfs(documents);
        Map<String, Double> idf = TfIdf.idfFromTfs(tfs, false, false);
        System.out.println("IDF\n" + termStatistics(idf));

        System.out.println("---------Window stop------------");
    }



}
