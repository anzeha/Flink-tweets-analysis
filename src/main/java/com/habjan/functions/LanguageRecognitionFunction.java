package com.habjan.functions;

import com.habjan.PreprocessUtils;
import com.habjan.model.EsTweet;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

public class LanguageRecognitionFunction extends RichMapFunction<EsTweet, EsTweet> {
    private transient LanguageDetector languageDetector;
    private final LanguageDetectorModel languageDetectorModel;

    public LanguageRecognitionFunction(LanguageDetectorModel languageDetectorModel) {
        this.languageDetectorModel = languageDetectorModel;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.languageDetector = new LanguageDetectorME(languageDetectorModel);
    }

    @Override
    public EsTweet map(EsTweet esTweet) throws Exception {
        Language[] languages = languageDetector.predictLanguages(PreprocessUtils.CleanForLanguageAnalysis(PreprocessUtils.CleanGoalTweet(esTweet.getText().toString())));
        esTweet.setDetectedLanguage(languages[0].getLang());
        esTweet.setLanguageConfidence(languages[0].getConfidence());
        return esTweet;
    }
}
