package cl.cvaldex.rockholiday.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import cl.cvaldex.rockholiday.vo.TweetVO;

public class UpdateImageTweetDAO {
	private DataSource ds;

	public UpdateImageTweetDAO(DataSource ds){
		this.ds = ds;
	}

	public void uploadImage(String fileName , int id) throws FileNotFoundException{
		Collection<TweetVO> tweets = null;

		PreparedStatement selectTweetsPS = null;
		Connection conn = null;

		try {
			conn = ds.getConnection();

			selectTweetsPS = conn.prepareStatement(assembleQuery());

			//setear parámetros para la Query
			
			selectTweetsPS.setBinaryStream(1, new FileInputStream(new File(fileName)));
			selectTweetsPS.setInt(2, id);

			selectTweetsPS.execute();
			//rs = selectTweetsPS.getResultSet();
			
			//procesar la salida de la Query
			
			//cerrar elementos de colección a BD
			conn.close();
			selectTweetsPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String assembleQuery(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("UPDATE public.tweets ");
		builder.append("set image1 = ? ");
		builder.append("WHERE id = ?");
		
		return builder.toString();
	}
}
