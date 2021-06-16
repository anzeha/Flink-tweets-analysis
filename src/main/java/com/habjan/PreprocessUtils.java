package com.habjan;

public class PreprocessUtils {
    public static String CleanTweet(String tweet){
        tweet = tweet.replaceAll("\\W", " ");
        tweet = tweet.replaceAll("\\s+[a-zA-Z]\\s+", " ");
        tweet = tweet.replaceAll("\\s+", " " );
        tweet = tweet.replaceAll("\\b(g+o+a*l+)+", "goal");
        //REMOVE URLS
        tweet = tweet.replaceAll("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", "");
        tweet = tweet.replaceAll("\\b(http[s]?)*", "");
        tweet = tweet.toLowerCase();
        tweet.trim();
        return tweet;
    }

    public static String CleanForLanguageAnalysis(String tweet){
        tweet = tweet.toLowerCase();
        //MENTIONS
        tweet = tweet.replaceAll("(@\\w*)", "");
        //REMOVE HASHTAGS
        tweet = tweet.replaceAll("#\\w+\\s*", "");
        //REMOVE ALL SPECIAL CHARS
        tweet = tweet.replaceAll("\\W", " ");
        //HTML symbols
        tweet = tweet.replaceAll("&[a-z]+;", "");
        //MULTIPLE SPACE WITH ONE SPACE
        tweet = tweet.replaceAll("\\s+", " " );
        tweet = tweet.replaceAll("\\b(g+o+a*l+)+", "goal");
        //REMOVE URLS
        tweet = tweet.replaceAll("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", "");
        tweet = tweet.replaceAll("\\b(http[s]?)*", "");
        //SPECIAL CHARS
        tweet = tweet.replaceAll("[^a-z\\s\\(\\-:\\)\\\\\\/\\];='#]", "");
        tweet = tweet.trim();
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
