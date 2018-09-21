package cl.cvaldex.rockholiday.main;

import java.io.FileNotFoundException;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.UpdateImageTweetDAO;

public class TestUpdateDAO {
	public static void main(String [] args) throws FileNotFoundException{
		BaseDataSource ds = new PGSimpleDataSource();
		
		ds.setServerName("rockholidays.cvecralyfpim.us-east-1.rds.amazonaws.com");
		ds.setPortNumber(5432);
		ds.setDatabaseName("rockholidays");
		
		ds.setUser("rockholidays");
		ds.setPassword("rockholidays2018");
		
		
		UpdateImageTweetDAO dao = new UpdateImageTweetDAO((DataSource)ds);
		dao.uploadImage("/Users/cvaldesc/Dm4_wYjXoAAGM2H.jpg_large", 1);
	}
}
