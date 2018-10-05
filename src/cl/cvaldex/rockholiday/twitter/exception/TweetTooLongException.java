package cl.cvaldex.rockholiday.twitter.exception;

public class TweetTooLongException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TweetTooLongException(String message){
		super(message);
	}
	
	public String toString(){
		return super.getMessage();
	}
}
