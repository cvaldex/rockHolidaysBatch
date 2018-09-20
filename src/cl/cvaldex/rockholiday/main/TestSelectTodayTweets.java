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

import cl.cvaldex.rockholiday.jdbc.SelectTweetsDAO;
import cl.cvaldex.rockholiday.vo.TweetVO;

public class TestSelectTodayTweets {

	public static void main(String[] args) throws IOException {
		String date = "2018-09-13";
		
		BaseDataSource ds = new PGSimpleDataSource();
		ds.setUrl("jdbc:postgresql://rockholidays.cvecralyfpim.us-east-1.rds.amazonaws.com:5432/rockholidays");
		ds.setUser("rockholidays");
		ds.setPassword("rockholidays2018");
		
		SelectTweetsDAO td = new SelectTweetsDAO((DataSource) ds);
		
		Collection<TweetVO> c = td.getTweetsByDate(date);
		
		if(c.size() > 0){
			Iterator<TweetVO> i = c.iterator();
			
			TweetVO tmp = null;
			
			while (i.hasNext()){
				tmp = i.next();
				System.out.println(tmp.toString());
				
				writeFile(tmp.getImage1() , tmp.getId()+"_image_1.jpg");
				writeFile(tmp.getImage2() , tmp.getId()+"_image_2.jpg");
				writeFile(tmp.getImage3() , tmp.getId()+"_image_3.jpg");
				writeFile(tmp.getImage4() , tmp.getId()+"_image_4.jpg");
			}
		}
		else{
			System.out.println("No data found!");
		}
	}
	
	public static void writeFile(InputStream input , String fileName) throws IOException{
			//TODO cambiar por algo que controle mejor el error
			if(input == null){
				return;
			}
			
			byte[] buffer = new byte[input.available()];
			input.read(buffer);
			
			File targetFile = new File("/Users/cvaldesc/tmp/"+fileName);
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			
			outStream.close();
	}
}
