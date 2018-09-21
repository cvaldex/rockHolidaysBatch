package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.SelectTweetsDAO;
import cl.cvaldex.rockholiday.vo.TweetVO;

public class TestSelectTodayTweets {
	static final Logger logger = LogManager.getLogger(TestSelectTodayTweets.class);
	/*
	 * Arguments: dbServerName dbServerPort dbName dbUserName dbPassword queryDate
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 6){
			logger.error("Invalid args, must be 6: dbServerName dbServerPort dbName dbUserName dbPassword queryDate");
			System.exit(1);
		}
		
		String serverName = args[0];
		int serverPort = new Integer(args[1]).intValue();
		String dbName = args[2];
		String dbUserName = args[3];
		String dbPassword = args[4];
		String queryDate = args[5];
		
		BaseDataSource ds = new PGSimpleDataSource();
		
		ds.setServerName(serverName);
		ds.setPortNumber(serverPort);
		ds.setDatabaseName(dbName);
		ds.setUser(dbUserName);
		ds.setPassword(dbPassword);
		
		logger.info("Query Date: " + queryDate);
		
		SelectTweetsDAO td = new SelectTweetsDAO((DataSource) ds);
		
		Collection<TweetVO> c = td.getTweetsByDate(queryDate);
		
		int resultsFound = c.size();
		
		if(resultsFound > 0){
			logger.info(resultsFound + " results found!");
			logger.info("Images will be stored in: " + System.getProperty("java.io.tmpdir"));
			
			Iterator<TweetVO> i = c.iterator();
			
			TweetVO tmp = null;
			
			while (i.hasNext()){
				tmp = i.next();
				logger.info(tmp.toString());
				
				writeFile(tmp.getImage1() , tmp.getId()+"_image_1.jpg");
				writeFile(tmp.getImage2() , tmp.getId()+"_image_2.jpg");
				writeFile(tmp.getImage3() , tmp.getId()+"_image_3.jpg");
				writeFile(tmp.getImage4() , tmp.getId()+"_image_4.jpg");
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