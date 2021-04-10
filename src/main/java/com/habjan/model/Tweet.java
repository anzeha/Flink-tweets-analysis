package com.habjan.model;

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
}
