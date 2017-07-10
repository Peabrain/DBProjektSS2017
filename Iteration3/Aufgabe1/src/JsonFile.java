import java.sql.*;
import org.json.*;
import java.sql.Date;
import java.time.Period;
import java.util.*;
import java.io.*;

//In dieser Javaklasse wird ein .json file erzeugt der alle Knoten
//d.h. Hashtags enthält und alle Kanten
//Kanten bestehen zwischen Knoten die zusammen benutzt werden
public class JsonFile{

  //makNode erstellt ein JsonObjekt welches einen einzelnen Knoten beschreibt
  public static JSONObject makeNode(String hashtag, Connection c, Date startDate){
    JSONObject jO = new JSONObject();
    try {
      Statement s = c.createStatement();
      ResultSet res;
      res = s.executeQuery("SELECT COUNT(*) FROM ErscheintZsmMit WHERE Name1='"+hashtag+"'");
      res.next();
      //die größe des Knotens wird bestimmt durch die Häufigkeiz seiner Benutzung zusammen mit anderen Hashtags
      //damit alle Knoten angezeigt werden, dh. eine size >0 haben wird der Wert aus der SQL Abfrage immer um eins erhöht
      //kleine Knoten mit size=1 stellen Hashtags dar die nur einzeln genutzt werden
      jO.put("size", (res.getInt(1))+1);
      res = s.executeQuery("SELECT Haeufigkeit_gesamt FROM Hashtag WHERE Name='"+hashtag+"'");
      res.next();
      jO.put("id", hashtag);
      jO.put("label", hashtag);
      //der x Wert wird fesgelegt durch die Häufigkeit der Nutzung dieses einzelnen Hashtags
      //damit die Knoten nicht zu nah beieinander liegen bei der darstellung, die Koordinate *10
      jO.put("x",(res.getInt("Haeufigkeit_gesamt"))*10);
      res = s.executeQuery("SELECT Timestamp FROM GenutztAm WHERE Name='"+hashtag+"'");
      res.next();
      int y = 0;
      Period temp = null;
      //mithilfe der Klasse Period wird mir die Differenz zwischen zwei Daten angegeben
      temp = temp.between(res.getDate(1).toLocalDate(),startDate.toLocalDate());
      y = temp.getDays();
      //diese Differenz legt die y-Koordinate fest, *100 um die Abstände zu vergrößern
      jO.put("y", (y*100));
      return jO;
    } catch(Exception e){System.out.println(""+e.getMessage());
      return jO;
    }
  }

//von der Main aus werden alle Knoten und Kanten erzeugt und dann in den .json File sgeschrieben, außedem wird hier die
//Verbindung zur Datenbank hergestellt und der Dateipfad für den zu erstellenden json File festgelegt
  public static void main(String[] args){
  	//Initialisierung der Variablen die für die SQL Abfragen nötig sind
    Connection c = null;
    ResultSet resN;
    ResultSet resN2;
    ResultSet resE;
    Date startDate = null; // wird bei makeNode() benötigt, bekommt das datem des ersten genutzten Hashtags als Startzeitpunkt
    //In den Arraylisten werden die Namen der Hashtags gespeichert, so dass diese alle zu Nodes umgewandelt und in den Graphen eingefügt werden können
    //die beiden edge Listen entahlten bei gleichem Index zusammen ein Hashtagpaar
    ArrayList<String> nodeArray = new ArrayList();
    ArrayList<String> edgeArray1 = new ArrayList();
    ArrayList<String> edgeArray2 = new ArrayList();
    //JSONObjects die benötigt werden für den Graphen
    JSONObject nodes = new JSONObject();	//enthält alle Knoten, dh. eine Liste aller Knoten in Json Format({"nodes": [aufzählung aller knoten...]})
    JSONObject edges = new JSONObject();	//enthält alle Kanten
    JSONObject finaljO = new JSONObject();	//ist das JSONOBJect welches in den .json File geschrieben wird, enthält also 
    JSONArray jnArray = new JSONArray();
    JSONArray jeArray = new JSONArray();
    final String jsonFilePath = "points.json";	//Filepath vom .json File wird festgelegt

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Election","postgres", "postgres");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        //create das JsonObjekt für Nodes, dh die Knoten
    try {
      Statement sN1 = c.createStatement();
      resN = sN1.executeQuery("SELECT * FROM GenutztAm ORDER BY Timestamp DESC LIMIT 1");

      resN.next();
      startDate = resN.getDate("Timestamp");
      resN2 = sN1.executeQuery("SELECT Name FROM Hashtag");
      while (resN2.next()) {
        nodeArray.add(resN2.getString(1));
      }

    } catch(Exception e) {e.printStackTrace();}
    System.out.println(nodeArray.size());
    for(int i = 0; i<nodeArray.size(); i++){
      jnArray.put(makeNode(nodeArray.get(i),c,startDate));
    }
    //create edges JSON OBJ
    try {
      Statement sE = c.createStatement();
      resE = sE.executeQuery("SELECT Name1 , Name2 FROM ErscheintZsmMit");
      while (resE.next()) {
        edgeArray1.add(resE.getString(1));
        edgeArray2.add(resE.getString(2));
      }
    }catch(Exception e){ e.printStackTrace();}

    for(int x = 0; x<edgeArray1.size(); x++){
      JSONObject jO = new JSONObject();
      jO.put("id", x);
      jO.put("source", edgeArray1.get(x));
      jO.put("target", edgeArray2.get(x));
      jeArray.put(jO);
    }
    finaljO.put("nodes",jnArray);
    finaljO.put("edges",jeArray);
    //füge JSONObjekt edges in JSON File ein
    try {
      PrintWriter jsonFileWriter = new PrintWriter(jsonFilePath);
      jsonFileWriter.write(finaljO.toString());
      jsonFileWriter.flush();
      jsonFileWriter.close();
    } catch (IOException e) { e.printStackTrace(); }
  }
}