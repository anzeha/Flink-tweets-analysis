package com.habjan.model;


import org.apache.kafka.common.header.Headers;

public class Tweet {
    public long id;
    public String created_at;
    public long user_id;
    public String username;
    public String text;
    public int quote_count;
    public int reply_count;
    public int retweet_count;
    public int favorite_count;

    public Tweet() {}

    public Tweet(long id, String created_at, long user_id, String username, String text, int quote_count, int reply_count, int retweet_count, int favorite_count) {
        this.id = id;
        this.created_at = created_at;
        this.user_id = user_id;
        this.username = username;
        this.text = text;
        this.quote_count = quote_count;
        this.reply_count = reply_count;
        this.retweet_count = retweet_count;
        this.favorite_count = favorite_count;
    }


    public String toString() {
        return "Tweet{id=" + this.id + " text=" + this.text + "}";
    }
    /*String topic;
    int partition;
    long timestamp;
    byte[] key;
    byte[] value;
    Headers headers;

    public Tweet() {
    }

    public Tweet(String topic, int partition, long timestamp, byte[] key, byte[] value, Headers headers) {
        this.topic = topic;
        this.partition = partition;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
        this.headers = headers;
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s, %s, [%s-byte key], [%s-byte value], %s)", Tweet.class.getSimpleName(),
                topic, partition, timestamp,
                key == null ? "null" : String.format("%s-byte", key.length),
                value == null ? "null" : String.format("%s-byte", value.length),
                headers);
    }*/
}
