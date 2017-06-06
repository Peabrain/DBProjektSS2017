//
// Klasse enthält Daten zum Tweet
//

public class tweet {
	public String handle = null;
	public String text = null;
	public int anz_retweet = 0;
	public int anz_likes = 0;
	public String org_autor = null;
	public String timestamp = null;
	
	public tweet(String handle,String text,int anz_retweet,int anz_likes,String org_autor,String timestamp)
	{
		this.handle = handle;
		this.text = text;
		this.anz_retweet = anz_retweet;
		this.anz_likes = anz_likes;
		this.org_autor = org_autor;
		this.timestamp = timestamp;
	}
}
