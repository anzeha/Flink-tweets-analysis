package com.habjan.functions;

import com.habjan.PreprocessUtils;
import com.habjan.model.EsTweet;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.Arrays;

public class SentimentCategorizerFunction extends RichMapFunction<EsTweet, EsTweet> {
    private transient DocumentCategorizerME documentCategorizerME;
    private final DoccatModel doccatModel;
    private transient Tokenizer tokenizer;
    private final TokenizerModel tokenizerModel;

    public SentimentCategorizerFunction(DoccatModel doccatModel, TokenizerModel tokenizerModel) {
        this.doccatModel = doccatModel;
        this.tokenizerModel = tokenizerModel;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        documentCategorizerME = new DocumentCategorizerME(doccatModel);
        tokenizer = new TokenizerME(tokenizerModel);
    }

    @Override
    public EsTweet map(EsTweet esTweet) throws Exception {
        if(esTweet.getDetectedLanguage().equals("eng")){
            String cleanText = PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(esTweet.getText()));
            String[] tweetWords = tokenizer.tokenize(cleanText);
            double[] outcomes = documentCategorizerME.categorize(tweetWords);
            String category = documentCategorizerME.getBestCategory(outcomes);
            esTweet.setSentiment(category);
            esTweet.setSentimentProbability(Arrays.stream(outcomes).max().getAsDouble());
        }

        return esTweet;
    }
}
