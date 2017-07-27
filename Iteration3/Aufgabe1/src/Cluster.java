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

public class Cluster {
	
	public List<Point1> points;
	public Point1 centroid;
	public int id;
	
	//Creates a new Cluster
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList();
		this.centroid = null;
	}

	public List getPoints() {
		return points;
	}
	
	public void addPoint(Point1 point) {
		points.add(point);
	}

	public void setPoints(List points) {
		this.points = points;
	}

	public Point1 getCentroid() {
		return centroid;
	}

	public void setCentroid(Point1 centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}
	
	public void clear() {
		points.clear();
	}
	
	public void plotCluster() {
/*		System.out.println("[Cluster: " + id+"]");
		System.out.println("[Centroid: " + centroid + "]");
		System.out.println("[Points: \n");
		for(Point p : points) {
			System.out.println(p);
		}
		System.out.println("]");
	*/
	}


}