package org.eaetirk.demo.twitter.to.kafka.service.runner.impl;

import org.apache.hc.core5.http.ParseException;
import org.eaetirk.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import org.eaetirk.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Use conditionOnProperty if you are using twitter v2 api as below :
 * @ConditionalOnProperty(name = "twitter-to-kafka-service.enable-v2-tweets", havingValue = "true", matchIfMissing = true)
 */
@Component
@ConditionalOnExpression("not ${twitter-to-kafka-service.enable-mock-tweets} && ${twitter-to-kafka-service.enable-v2-tweets}")
public class TwitterV2KafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterV2KafkaStreamRunner.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterV2StreamHelper twitterV2StreamHelper;

    public TwitterV2KafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterV2StreamHelper twitterV2StreamHelper){
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterV2StreamHelper = twitterV2StreamHelper;
    }
    @Override
    public void start() {

        String bearerToken = twitterToKafkaServiceConfigData.getTwitterV2BearerToken();
        if(null != bearerToken){

            List<String> keywords = twitterToKafkaServiceConfigData.getTwitterKeywords();
            Map<String, String> rules = getRules(keywords);
            try{
                twitterV2StreamHelper.setupRules(bearerToken, rules);
                twitterV2StreamHelper.connectStream(bearerToken);
            }catch(IOException | URISyntaxException |ParseException e){
                LOG.error("Error Streaming Tweets!");
              throw new RuntimeException("Error streaming Tweets ! {}", e);
            }

        }else{
            LOG.error("TWITTER_BEARER_TOKEN is empty");
            throw new RuntimeException("TWITTER_BEARER_TOKEN is empty");
        }

    }

    private  Map<String, String> getRules(List<String> keywords) {
        Map<String, String> rules = new HashMap<>();
        for (String keyword : keywords){
            rules.put(keyword, "Keyword: "+keyword);
        }
        LOG.info("Created filter for twitter stream for keywords : {} ", keywords);
        return rules;
    }
}
