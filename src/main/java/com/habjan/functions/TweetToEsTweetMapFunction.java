package com.habjan.functions;

import com.habjan.model.EsTweet;
import com.habjan.model.Tweet;
import com.habjan.model.TweetUtils;
import org.apache.flink.api.common.functions.MapFunction;

public class TweetToEsTweetMapFunction implements MapFunction<Tweet, EsTweet> {
    @Override
    public EsTweet map(Tweet tweet) throws Exception {
        EsTweet esTweet = new EsTweet();
        esTweet.setCreated_at(TweetUtils.TwitterTSToElasticTS(tweet.getCreatedAt().toString()));
        esTweet.setId(tweet.getId());
        esTweet.setUsername(tweet.getUsername().toString());
        esTweet.setUser_id(tweet.getUserId());
        esTweet.setText(tweet.getText().toString());
        return esTweet;
    }
}
