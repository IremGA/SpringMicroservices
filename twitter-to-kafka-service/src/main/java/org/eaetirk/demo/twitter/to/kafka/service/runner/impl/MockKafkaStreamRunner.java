package org.eaetirk.demo.twitter.to.kafka.service.runner.impl;

import org.eaetirk.demo.config.TwitterToKafkaServiceConfigData;
import org.eaetirk.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import org.eaetirk.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


@Component
@ConditionalOnProperty(name="twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = {"apple", "banana", "orange", "grape", "peach", "strawberry", "blueberry", "kiwi",
            "pineapple", "watermelon", "melon", "lemon", "lime", "pear", "cherry", "apricot",
            "plum", "fig", "avocado", "coconut", "pomegranate", "mango", "papaya", "cranberry",
            "raspberry", "blackberry", "guava", "lychee", "dragonfruit", "passionfruit", "date",
            "kiwifruit", "nectarine", "tangerine", "persimmon", "boysenberry", "cantaloupe",
            "honeydew", "mulberry", "quince", "starfruit", "kumquat", "durian", "feijoa",
            "gooseberry", "huckleberry", "jackfruit", "rhubarb", "soursop"};
    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";


    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {

        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Starting Mock filtering twitter streams for keywords {} ", Arrays.toString(keywords));
        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs)  {
       //instead of blocking main thread we are opening a new thread
        Executors.newSingleThreadExecutor().submit(() ->{
            try {
                while(true){
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleepForTweet(sleepTimeMs);
                }
            }catch (TwitterException exception){
                LOG.error("Error while Streaming Mock Twitter : ", exception);
            }

        });


    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
    String[] params = new String[]{
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
            getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
    };

    String tweet = tweetAsRawJson;
    for(int i=0; i< params.length; i++){
        tweet = tweet.replace("{"+i+"}", params[i]);

    }
    return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet =  new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength-minTweetLength+1) + minTweetLength;
        for(int i=0; i<tweetLength; i++){
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if(i == tweetLength/2){
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        LOG.info("Random Twitter content : {} ", tweet );
        return tweet.toString().trim();
    }

    private void sleepForTweet(long sleepTimeMs){
        try{
            Thread.sleep(sleepTimeMs);
        }catch (InterruptedException e){
            throw new RuntimeException("Error while sleeping for waiting new status to create!");
        }
    }
}
