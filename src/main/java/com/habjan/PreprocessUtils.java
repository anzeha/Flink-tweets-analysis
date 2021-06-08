package com.habjan;

public class PreprocessUtils {
    public static String CleanTweet(String tweet){
        tweet = tweet.replaceAll("\\W", " ");
        tweet = tweet.replaceAll("\\s+[a-zA-Z]\\s+", " ");
        tweet = tweet.replaceAll("\\s+", " " );
        tweet = tweet.replaceAll("\\b(g+o+a*l+)+", "goal");
        tweet = tweet.toLowerCase();

        return tweet;
    }

    public static String CleanGoalTweet(String tweet){
        //ENG
        tweet = tweet.replaceAll("\\b((g+o+a*l+)+[A-Za-z0-9]*)+", "goal");
        //FRENCH
        tweet = tweet.replaceAll("\\b((b+u+t+)+[A-Za-z0-9]*)+", "goal");
        //GERMAN
        tweet = tweet.replaceAll("\\b((t+o+r+)+[A-Za-z0-9]*)+", "goal");
        //SPANISH
        tweet = tweet.replaceAll("\\b((g+o+l+a+[sz]+o+)+[A-Za-z0-9]*)+", "goal");
        return tweet;
    }
}
