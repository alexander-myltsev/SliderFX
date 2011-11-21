package gui;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterMain {
    static Twitter g() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("9p0kAfDL1I4cagWlNp0yg")
                .setOAuthConsumerSecret("hUvGmG4usPTu325N5G5NLsVYL6X386LTFKMfRDZbBmI")
                .setOAuthAccessToken("409588609-0ATCb2Anz80iTOq0bemQULMjjV6ETyik789hDqt2")
                .setOAuthAccessTokenSecret("p8KXOaJT6lZFT2KdCI9iMu5nbPIuZgFr7GmH3gw");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }

    public static void main(String[] args) throws TwitterException {
        Twitter twitter = g();
        Query query = new Query("from:nvidia");
        QueryResult result = twitter.search(query);
        for (Tweet tweet : result.getTweets()) {
            System.out.println(tweet.getFromUser() + ":" + tweet.getText());
        }
    }
}
