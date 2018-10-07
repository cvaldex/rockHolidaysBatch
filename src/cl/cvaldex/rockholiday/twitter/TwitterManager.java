package cl.cvaldex.rockholiday.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.cvaldex.rockholiday.twitter.exception.TweetTooLongException;
import cl.cvaldex.rockholiday.vo.TweetVO;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {
	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;
	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;
	private String tweetPattern;
	private int tweetMaxLength;
	private String storedDateFormat;
	private String publishDateFormat;
	
	private ConfigurationBuilder configurationBuilder;
	private Configuration twitterConfiguration;

	protected static String OAUTH_CONSUMER_KEY = "twitter.api.oAuthConsumerKey";
	protected static String OAUTH_CONSUMER_SECRET = "twitter.api.oAuthConsumerSecret";
	protected static String OAUTH_ACCESS_TOKEN = "twitter.api.oAuthAccessToken";
	protected static String OAUTH_ACCESS_TOKEN_SECRET = "twitter.api.oAuthAccessTokenSecret";
	
	protected static String TWEET_PATTERN_KEY = "twitter.publishPattern";
	protected static String TWEET_MAX_LENGTH_KEY = "twitter.maxLength";
	protected static String TWEET_STORED_DATE_FORMAT_KEY = "twitter.storedDateFormat";
	protected static String TWEET_PUBLISH_DATE_FORMAT_KEY = "twitter.publishDateFormat";

	static final Logger logger = LogManager.getLogger(TwitterManager.class);

	public void publishTweet(TweetVO tweet) throws TwitterException, IOException, ParseException {
		String text = assembleTweet(tweet);
		
		if(text.length() > tweetMaxLength){
			throw new TweetTooLongException("Tweet too long: " + text.length() + " characters");
		}
		
		if(configurationBuilder == null){
			configurationBuilder = createConfigurationBuilder();
			twitterConfiguration = configurationBuilder.build();
		}
		
		logger.debug(text);

		TwitterFactory tf = new TwitterFactory(twitterConfiguration);
		Twitter twitter = tf.getInstance();

		StatusUpdate status = new StatusUpdate(text);
		processImages(twitter , status , tweet);

		logger.info("Publishing Tweet");
		twitter.updateStatus(status);
		logger.info("Publishing Tweet Finished");
	}
	
	public String assembleTweet(TweetVO tweet) throws ParseException{
		String tempTweet = tweetPattern.replaceAll("\\$TEXT", tweet.getText());
		tempTweet = tempTweet.replaceAll("\\$DATE", formatTweetDate(tweet.getDate()));
		tempTweet = tempTweet.replaceAll("\\$AUTHOR", tweet.getAuthor());
		
		return tempTweet;
	}
	
	private String formatTweetDate(String date) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(storedDateFormat);
		Date tmpDate = formatter.parse(date);
		
		formatter = new SimpleDateFormat(publishDateFormat);
		
		return formatter.format(tmpDate);
	}

	private ConfigurationBuilder createConfigurationBuilder() throws TwitterException{
		logger.info("Starting twitter configuration builder");

		if(oAuthConsumerKey == null || oAuthConsumerKey.trim().length() == 0) throw new TwitterException("Missing parameter OAuthConsumerKey");
		if(oAuthConsumerSecret == null || oAuthConsumerSecret.trim().length() == 0) throw new TwitterException("Missing parameter OAuthConsumerSecret");
		if(oAuthAccessToken == null || oAuthAccessToken.trim().length() == 0) throw new TwitterException("Missing parameter OAuthAccessToken");
		if(oAuthAccessTokenSecret == null || oAuthAccessTokenSecret.trim().length() == 0) throw new TwitterException("Missing parameter OAuthAccessTokenSecret");

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().setOAuthConsumerKey(oAuthConsumerKey)
				.setOAuthConsumerSecret(oAuthConsumerSecret)
				.setOAuthAccessToken(oAuthAccessToken)
				.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);

		return configurationBuilder;
	}

	private static void processImages(Twitter twitter , StatusUpdate status , TweetVO tweet) throws IOException, TwitterException{
		File tmpImage = null;
		UploadedMedia media = null;

		long[] mediaIds = new long[4];
		int index = 0;
		
		String tempDir = getSystemTempDir();

		if(tweet.getImage1() != null){
			tmpImage = new File(tempDir + tweet.getId()+"_01.jpg");
			writeFile(tweet.getImage1() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}

		if(tweet.getImage2() != null){
			tmpImage = new File(tempDir + tweet.getId()+"_02.jpg");
			writeFile(tweet.getImage2() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}

		if(tweet.getImage3() != null){
			tmpImage = new File(tempDir + tweet.getId()+"_03.jpg");
			writeFile(tweet.getImage3() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}

		if(tweet.getImage4() != null){
			tmpImage = new File(tempDir + tweet.getId()+"_04.jpg");
			writeFile(tweet.getImage4() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}

		long[] mediaIdsFinal = new long[index];
		System.arraycopy( mediaIds, 0, mediaIdsFinal, 0, index );

		status.setMediaIds(mediaIdsFinal);
	}
	
	private static String getSystemTempDir(){
		String tmpDir = System.getProperty("java.io.tmpdir");
		String systemFileSeparator = System.getProperty("file.separator");
		
		if(tmpDir.endsWith(systemFileSeparator)){
			tmpDir = tmpDir + systemFileSeparator;
		}
		
		return tmpDir;
	}

	private static void writeFile(InputStream input , File file) throws IOException{
		byte[] buffer = new byte[input.available()];
		input.read(buffer);

		OutputStream outStream = new FileOutputStream(file);
		outStream.write(buffer);

		outStream.close();
	}

	public String getoAuthConsumerKey() {
		return oAuthConsumerKey;
	}

	public void setoAuthConsumerKey(String oAuthConsumerKey) {
		if(oAuthConsumerKey != null){
			this.oAuthConsumerKey = oAuthConsumerKey.trim();
		}
	}

	public String getoAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public void setoAuthConsumerSecret(String oAuthConsumerSecret) {
		if(oAuthConsumerSecret != null){
			this.oAuthConsumerSecret = oAuthConsumerSecret.trim();
		}
	}

	public String getoAuthAccessToken() {
		return oAuthAccessToken;
	}

	public void setoAuthAccessToken(String oAuthAccessToken) {
		if(oAuthAccessToken != null){
			this.oAuthAccessToken = oAuthAccessToken.trim();
		}
	}

	public String getoAuthAccessTokenSecret() {
		return oAuthAccessTokenSecret;
	}

	public void setoAuthAccessTokenSecret(String oAuthAccessTokenSecret) {
		if(oAuthAccessTokenSecret != null){
			this.oAuthAccessTokenSecret = oAuthAccessTokenSecret.trim();
		}
	}

	public void loadConfiguration(DataSource dataSource, String tableName, String keyColumn, String valueColumn){
		DatabaseConfiguration config = new DatabaseConfiguration();
		config.setDataSource(dataSource);
		config.setTable(tableName);
		config.setKeyColumn(keyColumn);
		config.setValueColumn(valueColumn);

		this.setoAuthConsumerKey(config.getString(OAUTH_CONSUMER_KEY));
		this.setoAuthConsumerSecret(config.getString(OAUTH_CONSUMER_SECRET));
		this.setoAuthAccessToken(config.getString(OAUTH_ACCESS_TOKEN));
		this.setoAuthAccessTokenSecret(config.getString(OAUTH_ACCESS_TOKEN_SECRET));
		
		this.setTweetPattern(config.getString(TWEET_PATTERN_KEY).trim());
		this.setTweetMaxLength(Integer.parseInt(config.getString(TWEET_MAX_LENGTH_KEY).trim()));
		this.setStoredDateFormat(config.getString(TWEET_STORED_DATE_FORMAT_KEY).trim());
		this.setPublishDateFormat(config.getString(TWEET_PUBLISH_DATE_FORMAT_KEY).trim());
		
		logger.debug("TWEET_STORED_DATE_FORMAT_KEY: " + this.getStoredDateFormat());
		logger.debug("TWEET_PUBLISH_DATE_FORMAT_KEY: " + this.getPublishDateFormat());
		logger.debug("TWEET_PATTERN_KEY: " + this.getTweetPattern());
	}

	public String getTweetPattern() {
		return tweetPattern;
	}

	public void setTweetPattern(String tweetPattern) {
		this.tweetPattern = tweetPattern;
	}

	public int getTweetMaxLength() {
		return tweetMaxLength;
	}

	public void setTweetMaxLength(int tweetMaxLength) {
		this.tweetMaxLength = tweetMaxLength;
	}

	public String getStoredDateFormat() {
		return storedDateFormat;
	}

	public void setStoredDateFormat(String storedDateFormat) {
		this.storedDateFormat = storedDateFormat;
	}

	public String getPublishDateFormat() {
		return publishDateFormat;
	}

	public void setPublishDateFormat(String publishDateFormat) {
		this.publishDateFormat = publishDateFormat;
	}
}
