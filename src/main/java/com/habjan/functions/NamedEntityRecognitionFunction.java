package com.habjan.functions;

import com.habjan.PreprocessUtils;
import com.habjan.model.EsTweet;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

public class NamedEntityRecognitionFunction extends RichMapFunction<EsTweet, EsTweet> {

    private transient TokenNameFinder nameFinder;
    private final TokenNameFinderModel tokenNameFinderModel;
    private transient Tokenizer tokenizer;
    private final TokenizerModel tokenizerModel;
    private ArrayList<String> PLAYERS;

    public NamedEntityRecognitionFunction(TokenNameFinderModel tokenNameFinderModel, TokenizerModel tokenizerModel, ArrayList<String> PLAYERS) {
        this.tokenNameFinderModel = tokenNameFinderModel;
        this.tokenizerModel = tokenizerModel;
        this.PLAYERS = PLAYERS;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        nameFinder = new NameFinderME(tokenNameFinderModel);
        tokenizer = new TokenizerME(tokenizerModel);
    }

    @Override
    public EsTweet map(EsTweet esTweet) throws Exception {
        /*String tokens1[] = tokenizer.tokenize("Harry Kane scores for england Lovely pass by Kyle Walker");
        String tokens2[] = tokenizer.tokenize("Mike is senior programming manager and Rama is a clerk both are working at Tutorialspoint");
        System.out.println(Arrays.toString(tokens1));
        System.out.println(Arrays.toString(tokens2));

        Span nameSpans1[] = nameFinder.find(tokens1);
        Span nameSpans2[] = nameFinder.find(tokens2);
        //Printing the names and their spans in a sentence
        for(Span s: nameSpans1){
            System.out.println(s.toString()+"  "+tokens1[s.getStart()]);
        }
        for(Span s: nameSpans2){
            System.out.println(s.toString()+"  "+tokens2[s.getStart()]);
        }*/
        //System.out.println(StringUtils.join(PLAYERS, ", "));
        String cleanTweetText = PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(esTweet.getText()));
        ArrayList<String> playersMentioned = new ArrayList<String>();
        for (String player: PLAYERS) {
            if(cleanTweetText.indexOf(player.toLowerCase()) > -1){
                playersMentioned.add(player);
            } else {
                String[] playerNames = player.split(" ");
                if(playerNames.length > 1){
                    if(cleanTweetText.indexOf(playerNames[1].toLowerCase()) > -1)
                        playersMentioned.add(player);
                }
            }
            /*for (String name:playerNames) {
                int index = esTweet.getText().indexOf(name);
                if(index > 0){
                    playersMentioned.add(player);
                    break;
                }
            }*/
        }
        System.out.println("---------------------");
        System.out.println(StringUtils.join(playersMentioned, ", "));
        System.out.println("---------------------");
        esTweet.setPlayersMentioned(playersMentioned);
        return esTweet;
    }

}
