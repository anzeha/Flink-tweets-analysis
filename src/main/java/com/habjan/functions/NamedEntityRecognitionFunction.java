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
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.Arrays;

public class NamedEntityRecognitionFunction extends RichMapFunction<EsTweet, EsTweet> {

    private transient TokenNameFinder nameFinder;
    private final TokenNameFinderModel tokenNameFinderModel;
    private transient Tokenizer tokenizer;
    private final TokenizerModel tokenizerModel;

    public NamedEntityRecognitionFunction(TokenNameFinderModel tokenNameFinderModel, TokenizerModel tokenizerModel) {
        this.tokenNameFinderModel = tokenNameFinderModel;
        this.tokenizerModel = tokenizerModel;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        nameFinder = new NameFinderME(tokenNameFinderModel);
        tokenizer = new TokenizerME(tokenizerModel);
    }

    @Override
    public EsTweet map(EsTweet esTweet) throws Exception {
        String tokens[] = tokenizer.tokenize(PreprocessUtils.CleanForLanguageAnalysis(esTweet.getText()));
        Span nameSpans[] = nameFinder.find(tokens);
        String persons = "";
        String test = "";
        //Printing the names and their spans in a sentence
        for(Span s: nameSpans){
            System.out.println(s.toString()+"  "+tokens[s.getStart()]);
            test = s.toString()+"  "+tokens[s.getStart()] + " ";
        }
        if(nameSpans.length > 0){
            System.out.println(esTweet.getText());
        }
        /*for(Span s: nameSpans)
            persons += s.toString() + " ";*/
        esTweet.setNER(test);
        return esTweet;
    }
}
