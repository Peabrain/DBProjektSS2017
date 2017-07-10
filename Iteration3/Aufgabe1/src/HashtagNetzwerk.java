import java.sql.*;
import org.json.*;

import java.sql.Date;
import java.time.Period;
import java.util.*;
import java.awt.Color;
import java.io.*;

//Erzuegt den *.json File zu den Clustern
public class HashtagNetzwerk {
  static String jsonFilePath="netzwerk.json";
  static double scaling=0.4;
  

  JSONArray  allNodes;
  JSONObject finaljO;
  JSONArray allLines;
  
  HashtagNetzwerk(){
  finaljO=new JSONObject();
  allNodes=new JSONArray();
  allLines=new JSONArray();
  }
  
  public JSONObject makeNode(String name,int x, int y,String color){
    JSONObject node=new JSONObject();

    node.put("id", name);
    node.put("label", name);
    node.put("x", x);
    node.put("y", y);
    node.put("size", 2);
    node.put("color", color);  
    return node;
  }
  public void addNode(String name,int x, int y,String color){
    allNodes.put(makeNode(name, x, y, color));
  }
  
  public void addCluster(List<Point> points,String color){
    System.out.println("Adding cluster");
    int newX;
    int newY;
    for(Point p:points){
      newX=Integer.valueOf((int)p.getX());
      newY=Integer.valueOf((int)p.getY());
      allNodes.put(makeNode(p.Myname, newX, newY, color));
    }
  }
  
  public void addAllClusters(List<Cluster> clusters,String color){
    for(Cluster c:clusters){
      addCluster(c.points,color);
    }
  }
  
  public JSONObject makeLineNodes(String nodeId, char character, int topOrdown){
    JSONObject node=new JSONObject();

    node.put("id", nodeId);
    node.put("x",(Character.getNumericValue(character)*27-20)*scaling);
    node.put("y",topOrdown);
    node.put("size",1);
    node.put("label",""+character);
    return node;
  }

  public JSONObject makeLineEdges(String source, String target){
    JSONObject edge=new JSONObject();

    edge.put("id", source+target);
    edge.put("source", source);
    edge.put("target", target);

    return edge;
  }

  public void makeLines(){
	    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	    for(int i=0; i<26; i++){
	      allNodes.put(makeLineNodes("bottom"+alphabet[i],alphabet[i],0));
	      allNodes.put(makeLineNodes("top"+alphabet[i],alphabet[i],300));
	      allLines.put(makeLineEdges("bottom"+alphabet[i],"top"+alphabet[i]));
	    }
	    allLines.put(makeLineEdges("bottomA","bottomZ"));
	  }

  private String getCharForNumber(int i) {
      return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
  }
  
  
  //Erzeugt den File, wenn schon vorhanden wird dieser überschriben
  public void createFile(){
    try{
      
      
          PrintWriter jsonFileWriter = new PrintWriter(jsonFilePath);
          finaljO.put("nodes",allNodes);
          finaljO.put("edges",allLines);
          jsonFileWriter.write(finaljO.toString());
          jsonFileWriter.flush();
          jsonFileWriter.close();
          } 
      catch (IOException e) { e.printStackTrace(); }

  }
  
  
}