package com.habjan.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TweetUtils {
    public static Map<String, Object> TweetToJson(Tweet tweet) {
        Map<String, Object> json = new HashMap<>();

        json.put("id", tweet.getId());
        json.put("created_at", TwitterTSToElasticTS(tweet.getCreatedAt().toString()));
        json.put("user_id", tweet.getUserId());
        json.put("username", tweet.getUsername().toString());
        json.put("text", tweet.getText().toString());
        json.put("quote_count", tweet.getQuoteCount());
        json.put("reply_count", tweet.getReplyCount());
        json.put("retweet_count", tweet.getRetweetCount());
        return json;
    }

    public static String TwitterTSToElasticTS(String twitterTS){
        DateTimeFormatter dtf
                = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss X uuuu", Locale.ROOT);
        return OffsetDateTime.parse(twitterTS, dtf).toInstant().toString();
    }

    public static long ConvertToEpoch(String twitterTS){
        DateTimeFormatter dtf
                = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss X uuuu", Locale.ROOT);
        return OffsetDateTime.parse(twitterTS, dtf).toInstant().toEpochMilli();
    }
}
