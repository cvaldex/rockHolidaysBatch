package cl.cvaldex.rockholiday.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import cl.cvaldex.rockholiday.vo.TweetVO;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterMgr {
	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;
	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;

	public void publishTweet(TweetVO tweet) throws TwitterException, IOException {
		System.out.println("Starting security configuration");
		
		ConfigurationBuilder cb = createConfigurationBuilder();
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		StatusUpdate status = new StatusUpdate(tweet.getText());
		processImages(twitter , status , tweet);
		
		System.out.println("Publishing Tweet");
		twitter.updateStatus(status);
		System.out.println("Publishing Tweet Finished");
	}
	
	private ConfigurationBuilder createConfigurationBuilder() throws TwitterException{
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
		
		if(tweet.getImage1() != null){
			tmpImage = new File(System.getProperty("java.io.tmpdir") + tweet.getId()+"_01.jpg");
			writeFile(tweet.getImage1() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}
		
		if(tweet.getImage2() != null){
			tmpImage = new File(System.getProperty("java.io.tmpdir") + tweet.getId()+"_02.jpg");
			writeFile(tweet.getImage2() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}
		
		if(tweet.getImage3() != null){
			tmpImage = new File(System.getProperty("java.io.tmpdir") + tweet.getId()+"_03.jpg");
			writeFile(tweet.getImage3() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}
		
		if(tweet.getImage4() != null){
			tmpImage = new File(System.getProperty("java.io.tmpdir") + tweet.getId()+"_04.jpg");
			writeFile(tweet.getImage4() , tmpImage);
			media = twitter.uploadMedia(tmpImage);
			mediaIds[index++] = media.getMediaId();
		}
		
		long[] mediaIdsFinal = new long[index];
		System.arraycopy( mediaIds, 0, mediaIdsFinal, 0, index );
		
		status.setMediaIds(mediaIdsFinal);
		
		
		
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
		this.oAuthConsumerKey = oAuthConsumerKey;
	}

	public String getoAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public void setoAuthConsumerSecret(String oAuthConsumerSecret) {
		this.oAuthConsumerSecret = oAuthConsumerSecret;
	}

	public String getoAuthAccessToken() {
		return oAuthAccessToken;
	}

	public void setoAuthAccessToken(String oAuthAccessToken) {
		this.oAuthAccessToken = oAuthAccessToken;
	}

	public String getoAuthAccessTokenSecret() {
		return oAuthAccessTokenSecret;
	}

	public void setoAuthAccessTokenSecret(String oAuthAccessTokenSecret) {
		this.oAuthAccessTokenSecret = oAuthAccessTokenSecret;
	}

}
