package cl.cvaldex.rockholiday.jdbc;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdatePublishedTweetDAO {
	public static final int PUBLISHED_ROW_STATUS = 1;
	private DataSource ds;
	static final Logger logger = LogManager.getLogger(UpdatePublishedTweetDAO.class);

	public UpdatePublishedTweetDAO(DataSource ds){
		this.ds = ds;
	}

	public void updateTweet(int tweetId) throws FileNotFoundException{
		PreparedStatement selectTweetsPS = null;
		Connection conn = null;

		try {
			conn = ds.getConnection();

			selectTweetsPS = conn.prepareStatement(assembleQuery());

			//setear parámetros para la Query
			logger.info("Updating Tweet ID: " + tweetId);
			
			selectTweetsPS.setInt(1, PUBLISHED_ROW_STATUS);
			selectTweetsPS.setInt(2, tweetId);

			selectTweetsPS.execute();

			//cerrar elementos de colección a BD
			conn.close();
			selectTweetsPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String assembleQuery(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("UPDATE public.today_tweets ");
		builder.append("set row_status = ? ");
		builder.append("WHERE id = ?");
		
		return builder.toString();
	}
}
