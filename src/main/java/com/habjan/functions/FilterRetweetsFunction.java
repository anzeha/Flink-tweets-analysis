package com.habjan.functions;

import com.habjan.model.EsTweet;
import org.apache.flink.api.common.functions.FilterFunction;

public class FilterRetweetsFunction implements FilterFunction<EsTweet> {
    @Override
    public boolean filter(EsTweet esTweet) throws Exception {
        return !esTweet.getText().toLowerCase().startsWith("rt");
    }
}
