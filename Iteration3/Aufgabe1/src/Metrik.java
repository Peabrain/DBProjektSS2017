public class Metrik {

  //Macht aus ein Hashztag ein Punkt
  public static Point1 applyMetrik(String hashtag){
    double x; //Ein wert des sich aus Anfangs und Endbustaben zusammen setzt
    double y; //Länge des Wortes
    
    x = 0;
    y = 0;
    double m1 = 0;
    for(int i = 0;i < hashtag.length();i++)
    {
    	double m = (Character.getNumericValue(hashtag.charAt(i))) * (i + 1);
		x += m * m;
    	m = (Character.getNumericValue(hashtag.charAt(i))) - m1;
		y += m * m;
		m1 = m;
    }
    x = Math.sqrt((double)x);
    y = Math.sqrt((double)y);    

    System.out.println("Created point out of: "+hashtag);
    System.out.println(x +"und y: "+y);
    return new Point1(x * 0.5,y * 2.0,hashtag);
  }
}