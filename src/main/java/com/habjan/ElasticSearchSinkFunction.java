package com.habjan;

import com.habjan.model.Tweet;
import com.habjan.model.TweetUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

public class ElasticSearchSinkFunction implements ElasticsearchSinkFunction<Tweet> {

    public String index;

    public ElasticSearchSinkFunction(String index) {
        this.index = index;
    }

    public IndexRequest createIndexRequest(Tweet element) {

        return Requests.indexRequest()
                .index(this.index)
                .source(TweetUtils.TweetToJson(element));
    }

    @Override
    public void process(Tweet tweet, RuntimeContext ctx, RequestIndexer indexer) {
        indexer.add(createIndexRequest(tweet));
    }
}
