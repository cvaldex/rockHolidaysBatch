package cl.cvaldex.rockholiday.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import cl.cvaldex.rockholiday.vo.TweetVO;

public class SelectTweetsDAO {
	private DataSource ds;

	public SelectTweetsDAO(DataSource ds){
		this.ds = ds;
	}

	public Collection<TweetVO> getTweetToPublish(){
		Collection<TweetVO> tweets = null;

		PreparedStatement selectTweetsPS = null;
		Connection conn = null;
		ResultSet rs= null;

		try {
			conn = ds.getConnection();

			selectTweetsPS = conn.prepareStatement(assembleQuery());

			selectTweetsPS.execute();
			rs = selectTweetsPS.getResultSet();
			
			//procesar la salida de la Query
			tweets = this.getTweets(rs);
			
			//cerrar elementos de colección a BD
			rs.close();
			conn.close();
			selectTweetsPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tweets;
	}
	
	private String assembleQuery(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT tweet, eventdate, author, image1, image2, image3, image4, id FROM public.today_tweets ");
		builder.append("WHERE row_status = 0"); //el tweet no ha sido publicado aun
		builder.append("ORDER BY priority, eventdate ASC LIMIT 1"); //ordenados del más antiguo al más nuevo, obtenemos solo el primero
		
		return builder.toString();
	}
	
	private Collection<TweetVO> getTweets(ResultSet rs) throws SQLException{
		Collection<TweetVO> tweets = new ArrayList<TweetVO>();
		TweetVO tweet = null;
		
		while(rs.next()){
			tweet = new TweetVO();

			tweet.setText(rs.getString(1).trim());
			tweet.setDate(rs.getString(2).trim());
			tweet.setAuthor(rs.getString(3).trim());
			
			tweet.setImage1(rs.getBinaryStream(4));
			tweet.setImage2(rs.getBinaryStream(5));
			tweet.setImage3(rs.getBinaryStream(6));
			tweet.setImage4(rs.getBinaryStream(7));
			tweet.setId(rs.getInt(8));
		
			tweets.add(tweet);
		}
		
		return tweets;
	}
}
