/* 

 * KMeans.java ; Cluster.java ; Point.java
 * http://www.dataonfocus.com/k-means-clustering-java-code/
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 *
*/

import java.util.ArrayList;
import java.util.List;
 
public class KMeans {
 
	//Number of Clusters. This metric should be related to the number of points
    public int NUM_CLUSTERS = 7;    
    //Number of Points
    private int NUM_POINTS = 15;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 25;
    
    private List<Point1> points;
    private List<Cluster> clusters;
    
    public KMeans() {
    	this.points = new ArrayList<Point1>();
    	this.clusters = new ArrayList<Cluster>();    	
    }
    /*
    public static void main(String[] args) {
    	
    	KMeans kmeans = new KMeans();
    	kmeans.init();
    	kmeans.calculate();
    }*/
    
    //Initializes the process
    public void init() {
    	//Create Points
    	points = Point1.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
    	
    	//Create Clusters
    	//Set Random Centroids
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster cluster = new Cluster(i);
    		Point1 centroid = Point1.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	//Print Initial state
    	plotClusters();
    }
    //Unser Zusazt///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void init(List<Point1> startingPoints){
    	points=startingPoints;
    	NUM_POINTS=points.size();
    	//Create Clusters
    	//Set Random Centroids
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster cluster = new Cluster(i);
    		Point1 centroid = Point1.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	//Print Initial state
    	plotClusters();
    }
    public void printCentroids(){
/*    	List<Point> printList=getCentroids();
    	System.out.println("Centroids:");
    	for(Point p:printList){
    		System.out.println("("+p.getX()+", "+p.getY()+")");
    	}
*/    }
    public List<Cluster> getClusters(){
    	return clusters;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
	private void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	List<Point1> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	List<Point1> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point1.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
/*        	System.out.println("#################");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distance);
*/        	plotClusters();
        	        	
        	if(distance == 0) {
        		finish = true;
        	
        	}
        }
    }
    
    
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    List<Point1> getCentroids() {
    	List<Point1> centroids = new ArrayList<Point1>(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point1 aux = cluster.getCentroid();
    		Point1 point = new Point1(aux.getX(),aux.getY(),"Cluster center");
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Point1 point : points) {
        	min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = Point1.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point1> list = cluster.getPoints();
            int n_points = list.size();
            
            for(Point1 point : list) {
            	sumX += point.getX();
                sumY += point.getY();
            }
            
            Point1 centroid = cluster.getCentroid();
            if(n_points > 0) {
            	double newX = sumX / n_points;
            	double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}