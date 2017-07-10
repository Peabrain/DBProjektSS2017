import java.util.ArrayList;
import java.util.List;

//Macht aus einer Liste von Hashtags eine liste von Punkten, mit hilfe der Metrik
public class PointsMaker {
	String[] hashtags;
	List<Point> points=new ArrayList<Point>();
	void init(){
		
		hashtags=CSVReader.getHashtags("Hashtags.csv");
		
		
	}
	
	void calculate(){
		//Usable only 1 time
		System.out.println(hashtags.length);
		for(String s:hashtags){
			points.add(Metrik.applyMetrik(s));
		}
	}
	
	
}
