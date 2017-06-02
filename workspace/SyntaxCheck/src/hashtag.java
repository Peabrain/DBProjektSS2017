import java.util.ArrayList;
import java.util.List;

public class hashtag {
	public int haeufigkeit;
	public List<Integer> tweet = new ArrayList<Integer>();
	public hashtag(int haeufigkeit,int first_tweetid)
	{
		this.haeufigkeit = haeufigkeit;
		tweet.clear();
		tweet.add(first_tweetid);
	}
}
