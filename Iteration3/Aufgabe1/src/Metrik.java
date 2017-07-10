public class Metrik {

  //Macht aus ein Hashztag ein Punkt
  public static Point applyMetrik(String hashtag){
    int x; //Ein wert des sich aus Anfangs und Endbustaben zusammen setzt
    int y; //Länge des Wortes
    
    x = Character.getNumericValue(hashtag.charAt(0))*27;
    x=x-27+Character.getNumericValue(hashtag.charAt(hashtag.length()-1));
    y=hashtag.length();
    

    System.out.println("Created point out of: "+hashtag);
    System.out.println(x +"und y: "+y);
    return new Point(x*0.4,y*10,hashtag);
  }
}