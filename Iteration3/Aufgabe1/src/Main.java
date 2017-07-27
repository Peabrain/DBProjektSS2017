import java.awt.Color;
import java.awt.Point;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Color [] ColorTable = {Color.red,Color.yellow,Color.green,Color.blue,Color.cyan,Color.orange,Color.gray,Color.magenta};
    	KMeans kmeans = new KMeans();
    	PointsMaker pmk=new PointsMaker();
    	JFrame f = new JFrame();
    	PointPanel panel=new PointPanel();
        
    	
    	//CSV datein wird geöfnet und die Hashtags extraiert
    	pmk.init();
    	pmk.calculate();
    	
    	//Kmeans wird aufgeführt
    	kmeans.init(pmk.points);
    	kmeans.calculate();
    	
    	
    	
    	//Erzeugung der Jsondatei fürs Netztwerk
    	HashtagNetzwerk netz=new HashtagNetzwerk();
        for(int i = 0;i < kmeans.NUM_CLUSTERS;i++)
        {
	    	String hex = String.format("#%01x%01x%01x", ColorTable[i % 8].getRed() >> 4, ColorTable[i % 8].getGreen() >> 4, ColorTable[i % 8].getBlue() >> 4);
	        netz.addCluster(kmeans.getClusters().get(i).points, hex);
	        Point center=kmeans.getCentroids().get(1);
	        netz.addNode("center" + i,(int)center.getX(),(int)center.getY(),"fff");
        }
        netz.makeLines();
        netz.createFile();
        
        

    	//Jframe Java darstellung
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(panel);

        for(int i = 0;i < kmeans.NUM_CLUSTERS;i++)
        {
        	System.out.println("");
        	System.out.println("-----------");
        	System.out.println("Cluster: " + i);
        	System.out.println("-----------");
	        for(Point1 p:kmeans.getClusters().get(i).points){
	        	System.out.println(p.getName());
	        	panel.addPoint(p,ColorTable[i % 8]);
	        }
        }        
        for(Point p:kmeans.getCentroids()){
        	panel.addPoint(p,Color.black);
        }        
        f.setSize(800,600);
        f.setLocation(200,200);
        f.setVisible(true);

    	
	}

}
