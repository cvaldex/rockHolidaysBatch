package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.InsertTweetsDAO;
import cl.cvaldex.rockholiday.parser.TweetsParser;
import cl.cvaldex.rockholiday.vo.TweetVO;

public class TestGoogleSheetLoader {

	public static void main(String[] args) throws Exception {
		TweetsParser gsParser = new TweetsParser();
		
		gsParser.setSecretsFilePath("/Users/cvaldesc/client_secret.json");
		gsParser.setSheetID("1OjTsrhmYLJIb-scLJuMCGIZwVrsTAzRwbBLFDIjEoaE");
		gsParser.setRange("Tweets!A1:I");
		
		Collection<TweetVO> tweets = gsParser.parse();
		
		BaseDataSource ds = new PGSimpleDataSource();
		ds.setServerName("rockholidays.cvecralyfpim.us-east-1.rds.amazonaws.com");
		ds.setPortNumber(5432);
		ds.setDatabaseName("rockholidays");
		
		ds.setUser("rockholidays");
		ds.setPassword("rockholidays2018");
		
		InsertTweetsDAO dao = new InsertTweetsDAO((DataSource)ds);
		
		dao.insertTweets(tweets);
	}
	
	public static void printTweets(Collection<TweetVO> tweets) throws IOException{
		Iterator<TweetVO> i = tweets.iterator();
		TweetVO tmpTweet = null;
		
		int index = 1;

		while(i.hasNext()){
			tmpTweet = i.next();
			
			System.out.println(tmpTweet.toString());
			
			writeFile(tmpTweet.getImage1() , (index + "_1.jpg"));
			writeFile(tmpTweet.getImage2() , (index + "_2.jpg"));
			writeFile(tmpTweet.getImage3() , (index + "_3.jpg"));
			writeFile(tmpTweet.getImage4() , (index + "_4.jpg"));
			
			index++;
		}
	}
	
	public static void writeFile(InputStream input , String fileName) throws IOException{
		byte[] buffer = new byte[input.available()];
		input.read(buffer);
		
		File targetFile = new File("/Users/cvaldesc/tmp/"+fileName);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		
		outStream.close();
	}
}
