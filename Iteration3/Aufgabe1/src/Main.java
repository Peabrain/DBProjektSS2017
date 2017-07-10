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
    	
    	/*
    	
    	//Erzeugung der Jsondatei fürs Netztwerk
    	HashtagNetzwerk netz=new HashtagNetzwerk();
        netz.addCluster(kmeans.getClusters().get(0).points, "#00f");
        netz.addCluster(kmeans.getClusters().get(1).points, "#0f0");
        Point center1=kmeans.getCentroids().get(0);
        Point center2=kmeans.getCentroids().get(1);
        netz.addNode("center1",(int)center1.getX(),(int)center1.getY(),"fff");
        netz.addNode("center2",(int)center2.getX(),(int)center2.getY(),"fff");
        netz.makeLines();
        netz.createFile();
        */
        

    	//Jframe Java darstellung
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(panel);
        for(Point p:kmeans.getClusters().get(0).points){
        	panel.addPoint(p,Color.red);
        }
        for(Point p:kmeans.getClusters().get(1).points){
        	panel.addPoint(p,Color.yellow);
        }
        
        
        for(Point p:kmeans.getCentroids()){
        	panel.addPoint(p,Color.black);
        }        
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);

    	
	}

}
