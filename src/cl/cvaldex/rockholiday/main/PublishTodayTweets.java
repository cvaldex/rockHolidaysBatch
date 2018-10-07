package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Collection;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.SelectTweetsDAO;
import cl.cvaldex.rockholiday.jdbc.UpdatePublishedTweetDAO;
import cl.cvaldex.rockholiday.twitter.TwitterManager;
import cl.cvaldex.rockholiday.vo.TweetVO;
import twitter4j.TwitterException;

public class PublishTodayTweets {
	static final Logger logger = LogManager.getLogger(PublishTodayTweets.class);
	/*
	 * Arguments: dbServerName dbServerPort dbName dbUserName dbPassword queryDate
	 */
	public static void main(String[] args) throws IOException, TwitterException, ParseException {
		if(args.length < 5){
			logger.error("Invalid args, must be 5: dbServerName dbServerPort dbName dbUserName dbPassword");
			System.exit(1);
		}
		
		String serverName = args[0];
		int serverPort = new Integer(args[1]).intValue();
		String dbName = args[2];
		String dbUserName = args[3];
		String dbPassword = args[4];
		
		BaseDataSource ds = new PGSimpleDataSource();
		
		ds.setServerName(serverName);
		ds.setPortNumber(serverPort);
		ds.setDatabaseName(dbName);
		ds.setUser(dbUserName);
		ds.setPassword(dbPassword);
		
		SelectTweetsDAO td = new SelectTweetsDAO((DataSource) ds);
		
		logger.info("Getting tweet to publish");
		
		Collection<TweetVO> c = td.getTweetToPublish();
	
		int resultsFound = c.size();
		
		if(resultsFound > 0){
			logger.info(resultsFound + " results found!");
			logger.info("Images will be stored in: " + System.getProperty("java.io.tmpdir"));
			
			TwitterManager twitterManager = new TwitterManager();
			twitterManager.loadConfiguration((DataSource) ds, "public.properties", "key", "value");
			
			UpdatePublishedTweetDAO updatePublishedTweetDAO = new UpdatePublishedTweetDAO((DataSource)ds);
			
			for(TweetVO vo : c){
				//publicar el tweet en el timeline
				twitterManager.publishTweet(vo);
				
				//actualizar el status del tweet en Base de Datos para no volver a tomarlo
				updatePublishedTweetDAO.updateTweet(vo.getId());
			}
		}
		else{
			logger.info("No data found!");
		}
	}
	
	public static void writeFile(InputStream input , String fileName) throws IOException{
		//TODO cambiar por algo que controle mejor el error
		if(input == null){
			return;
		}
		
		byte[] buffer = new byte[input.available()];
		input.read(buffer);
		
		File targetFile = new File(System.getProperty("java.io.tmpdir") + fileName);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		
		outStream.close();
	}
}
