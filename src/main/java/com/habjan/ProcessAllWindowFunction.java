package com.habjan;

import com.habjan.model.Tweet;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class ProcessAllWindowFunction extends org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tweet, Object, TimeWindow> {
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
}
