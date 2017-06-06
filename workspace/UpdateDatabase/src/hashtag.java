//
// Klasse enthält Daten für jeden Hashtag
//

import java.util.ArrayList;
import java.util.List;

public class hashtag {
	public int haeufigkeit;		// Wie oft kam der Hashtag im Datensatz vor
	public List<Integer> tweet = new ArrayList<Integer>();	// TweetID's, wo der Hashtag vor kam
	public hashtag(int haeufigkeit,int first_tweetid)
	{
		this.haeufigkeit = haeufigkeit;
		tweet.clear();
		tweet.add(first_tweetid);
	}
}
