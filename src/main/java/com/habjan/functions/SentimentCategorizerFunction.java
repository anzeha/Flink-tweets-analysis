package com.habjan.functions;

import com.habjan.PreprocessUtils;
import com.habjan.model.EsTweet;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.Arrays;

public class SentimentCategorizerFunction extends RichMapFunction<EsTweet, EsTweet> {
    private transient DocumentCategorizerME documentCategorizerME;
    private final DoccatModel doccatModel;

    public SentimentCategorizerFunction(DoccatModel doccatModel) {
        this.doccatModel = doccatModel;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        documentCategorizerME = new DocumentCategorizerME(doccatModel);
    }

    @Override
    public EsTweet map(EsTweet esTweet) throws Exception {
        String cleanText = PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(esTweet.getText()));
        String[] tweetWords = cleanText.split(" ");
        double[] outcomes = documentCategorizerME.categorize(tweetWords);
        String category = documentCategorizerME.getBestCategory(outcomes);
        esTweet.setSentiment(category);
        esTweet.setSentimentProbability(Arrays.stream(outcomes).max().getAsDouble());

        return esTweet;
    }
}
