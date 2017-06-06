import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//STEP 1. Import required packages
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class test {
	static public void main(String[] arg) throws IOException
	{		
		// Globale Daten zur Verbindung zum SQL-Server
		String url = "jdbc:postgresql://localhost:5432/Election";
		Properties props = new Properties();
		props.setProperty("user","testuser");
		props.setProperty("password","testpass");
		Connection conn = null;
		PreparedStatement pst = null;
		
		HashMap<Integer,tweet> tweets = new HashMap(); 			// HashMap<[int] TweetID,[class] tweet>
		HashMap<String,hashtag> hashtagcount = new HashMap();	// HashMap<[String] Hashtag,[class] hashtag>
		HashMap<String,time> timestam = new HashMap();			// HashMap<[String] Timestamp, [class] time>				
		HashMap<String,erscheintzsmmit> erscheintzsmmitMap = new HashMap();	// HashMap<[String] Timestamp, [class] erscheintzsmmit>

		// Laden der Datenbanktreiber
		try 
		{
			Class.forName("org.postgresql.Driver");
		} 
		catch (ClassNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally
		{
			// Verbindung zum Server etablieren
			try 
			{
				conn = DriverManager.getConnection(url, props);
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}	
			finally
			{
				// Löschen der Datenbank
				String stm1 = "DELETE FROM genutztam;DELETE FROM getweetetam;DELETE FROM kommtvorin;DELETE FROM erscheintzsmmit;DELETE FROM tweet;DELETE FROM hashtag;DELETE FROM time";
				try
				{
		            pst = conn.prepareStatement(stm1);
		            pst.executeUpdate();
				}
				catch (SQLException e) 
				{
		            e.printStackTrace();
		        }
				

				// Laden und formatieren der 'american-election-tweets.xlsx'-Datei
				Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
				InputStream ExcelFileToRead = new FileInputStream("american-election-tweets.xlsx");
				XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
				XSSFWorkbook test = new XSSFWorkbook(); 
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFRow row; 
				XSSFCell cell;

				// Holen den Zeilen-Iterators
				Iterator rows = sheet.rowIterator();

				int zeile = 1;
				while (rows.hasNext())
				{
					// Bearbeiten der n.Zeile
					String handle = null;
					String text = null;
					int anz_retweet = 0;
					int anz_likes = 0;
					String org_autor = null;
					String timestamp = null;

					System.out.print("Bearbeite Zeile " + zeile);
					row=(XSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					int Fehler = 0;
					// Solange, wie kein Fehler in der Zeile ist, wird die Zeile abgearbeitet
					while (cells.hasNext() && Fehler == 0)
					{
						// Bearbeite Spalte
						cell=(XSSFCell) cells.next();
						int Spalte = cell.getColumnIndex();
						// Abfrage des Datentyps
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
						{
							// String-Datentyp
							String olds = cell.getStringCellValue();
							// Umwandlung des Strings mit neuen Zeichen, wie Umlaute und Sonderzeichen
							String news = replaceUmlaute(olds);
							char[] ascii1 = news.toCharArray();
							news = "";
							for(int i: ascii1) news += Character.toString((char)i);
							// Nur bearbeiten, wenn es nicht die 1.Zeile ist
							if(zeile > 1)
							{
								// Für die Zeile temporäre Variablen für die Klassen füllen
								switch(Spalte)
								{
								case 0:
								{
									handle = news;
								}break;
								case 1:
								{
									text = news;
								}break;
								case 3:
								{
									org_autor = news;
								}break;
								case 2:
								case 6:
								{
									if(news.compareTo("True") != 0 && news.compareTo("False") != 0)	Fehler++;
								}break;
								case 4:
								{
									if(p.matcher(news).matches() == false) Fehler++;
									else
								        timestamp = news.replace("T", " ");
								}break;
								}
							}
//							System.out.print("(" + olds + ", " + news+") ");
						}
						else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
						{
							// Nummerischer-Datentyp gilt nur für Spalte 7 und 8
							if(Spalte != 7 && Spalte != 8) Fehler++;
							else
							{
								// Für die Zeile temporäre Variablen für die Klassen füllen
								if(Spalte == 7)
									anz_retweet = (int)cell.getNumericCellValue();
								else
									anz_likes = (int)cell.getNumericCellValue();
							}
//							System.out.print(cell.getNumericCellValue()+" ");
						}
						else
						{
							//U Can Handel Boolean, Formula, Errors
						}
						Spalte++;
					}
					if(Fehler == 0 && zeile > 1)
					{
						// Wenn kein Fehler in der Zeile vorliegt, dann erstelle einen neuen Tweet in der Liste
						tweets.put(zeile, new tweet(handle,text,anz_retweet,anz_likes,org_autor,timestamp));
					}
					zeile++;
					System.out.println();
				}
			}
		}
		// Weiterverarbeitung der Tweet-Liste in SQL-Tweet-Tabelle
		for(Map.Entry<Integer, tweet> entry : tweets.entrySet())
		{
			int tweetid = entry.getKey();
			try
			{
				// Erstelle eine SQL-Anfrage
				String stm = "INSERT INTO tweet(tweetid, handle, Text, anzlikes, anzretweets, originalautor) VALUES(?, ?, ?, ?, ?, ?)";
				tweet t = entry.getValue();
	            pst = conn.prepareStatement(stm);
	            pst.setInt(1, tweetid);
	            pst.setString(2, t.handle);                    
	            pst.setString(3, t.text);                    
	            pst.setInt(4, t.anz_likes);                    
	            pst.setInt(5, t.anz_retweet);                    
	            pst.setString(6, t.org_autor);                    
	            pst.executeUpdate();
	            
	            // Wenn der Timestamp noch nicht vorhanden ist,
	            // erstelle den Timestamp in HashMap time
	            time ti = null;
	            if(timestam.containsKey(t.timestamp) == false)
	            	timestam.put(t.timestamp,new time());
            	ti = timestam.get(t.timestamp);
            	// Füge die aktuelle TweetID dem Timestamp hinzu
            	ti.getweetedam.add(tweetid);

            	// Hole alle Hashtag's, im Text des aktuellen Tweet's
				List<String> hashtags = findHashtags(t.text);
				// Verarbeite alle Hashtags
				for(String s: hashtags)
				{
					// Wenn der aktuelle Hashtag noch nicht existiert,
					if(hashtagcount.containsKey(s) == false)
					{
						// dann erstelle den Hashtag in der hashtagcount-Map
						// und füge die TweetID dem Hashtag hinzu
						hashtagcount.put(s, new hashtag(1,tweetid));
		            	ti.genutztam.add(s);
					}
					else
					{
						// ansonsten, hole den Hashtag und inkrementiere die Häufigkeit
						// und füge die TweetID dem Hashtag hinzu
						hashtag h = hashtagcount.get(s);
						h.haeufigkeit++;
						h.tweet.add(entry.getKey());
						if(ti.genutztam.contains(s) == false)
							ti.genutztam.add(s);
					}
				}
				// Gehe alle Kombinationen der  Hashtags durch 
				// und überprüfe, ob die Kombinationen vorhanden sind
				// und wenn ja, dann zähle die Häufigkeit
				for(int hs1 = 0;hs1 < hashtags.size() - 1;hs1++)
				{
					String hss1 = hashtags.get(hs1);
					for(int hs2 = hs1 + 1;hs2 < hashtags.size();hs2++)
					{
						String hss2 = hashtags.get(hs2);
						String hssCombi = hss1 + "," + hss2;
						String hssCombi_ = hss2 + "," + hss1;
						if(erscheintzsmmitMap.containsKey(hssCombi) == false)
						{
							if(erscheintzsmmitMap.containsKey(hssCombi_) == false)
								erscheintzsmmitMap.put(hssCombi_, new erscheintzsmmit(hss1,hss2));
							else
								erscheintzsmmitMap.get(hssCombi_).count += 1;
						}
						else
							erscheintzsmmitMap.get(hssCombi).count += 1;
					}
				}
			}
			catch (SQLException e) 
			{
	            e.printStackTrace();
	        }
		}
	
		// Erstelle den SQL-Tabelle der Hashtags auf dem SQL-Server
		for(Map.Entry<String, hashtag> entry : hashtagcount.entrySet())
		{
			String name = entry.getKey();
			System.out.println("Hashtag: " + entry.getKey() + ", " + entry.getValue().haeufigkeit);
			String stm = "INSERT INTO hashtag(name, haeufigkeit_gesamt) VALUES(?, ?)";
			hashtag t = entry.getValue();
			try
			{
	            pst = conn.prepareStatement(stm);
	            pst.setString(1, name);
	            pst.setInt(2, t.haeufigkeit);                    
	            pst.executeUpdate();
			}
			catch (SQLException e) 
			{
	            e.printStackTrace();
	        }

			// Füge zur Relation 'kommtvorin' den aktuellen Hashtag und allen 
			// dazugehörigen Tweets hinzu
			for(int l: t.tweet)
			{
				String stm1 = "INSERT INTO kommtvorin(name, tweetid) VALUES(?, ?)";
				try
				{
		            pst = conn.prepareStatement(stm1);
		            pst.setString(1, name);
		            pst.setInt(2, l);                    
		            pst.executeUpdate();
				}
				catch (SQLException e) 
				{
		            e.printStackTrace();
		        }
			}
		}
		// Fülle die SQL-Tabelle time in der Datenbank
		for(Map.Entry<String,time> entry : timestam.entrySet())
		{
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	java.util.Date timest = null;
			java.sql.Timestamp timeStampDate = null;
			try 
			{
				timest = format.parse(entry.getKey());
				timeStampDate = new Timestamp(timest.getTime());
				String stm = "INSERT INTO time(timestamp) VALUES(?)";
	            pst = conn.prepareStatement(stm);
	            pst.setTimestamp(1, timeStampDate);                    
	            pst.executeUpdate();
	            
	            time ti = entry.getValue();
	            // Füge alle TweetID's mit aktuellem Timestamp in die getweetetam-Relation ein
	            for(int i : ti.getweetedam)
	            {
	    			try 
	    			{
	    				stm = "INSERT INTO getweetetam(timestamp,tweetid) VALUES(?,?)";
	    	            pst = conn.prepareStatement(stm);
	    	            pst.setTimestamp(1, timeStampDate);                    
	    	            pst.setInt(2, i);                    
	    	            pst.executeUpdate();
	    			}	            	
	    			catch (SQLException e) 
	    			{
	    	            e.printStackTrace();
	    	        }
	            }
	            // Füge alle TweetID's mit aktuellem Timestamp in die genutztam-Relation ein
	            for(String i : ti.genutztam)
	            {
	    			try 
	    			{
	    				stm = "INSERT INTO genutztam(timestamp,name) VALUES(?,?)";
	    	            pst = conn.prepareStatement(stm);
	    	            pst.setTimestamp(1, timeStampDate);                    
	    	            pst.setString(2, i);                    
	    	            pst.executeUpdate();
	    			}	            	
	    			catch (SQLException e) 
	    			{
	    	            e.printStackTrace();
	    	        }
	            }
			} 
			catch (ParseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SQLException e) 
			{
	            e.printStackTrace();
	        }
		}
		// Erstelle die SQL-Tabelle zur Relation erscheintzsmmit in der Datenbank
		for(Map.Entry<String, erscheintzsmmit> entry : erscheintzsmmitMap.entrySet())
		{
			try 
			{
				String stm = "INSERT INTO erscheintzsmmit(name1,name2,haeufigkeitgesamt) VALUES(?,?,?)";
	            pst = conn.prepareStatement(stm);
	            pst.setString(1, entry.getValue().name1);
	            pst.setString(2, entry.getValue().name2);
	            pst.setInt(3, entry.getValue().count);                    
	            pst.executeUpdate();
			}	            	
			catch (SQLException e) 
			{
	            e.printStackTrace();
	        }
		}
	}
	// Tabelle zur Ersetzung von Umlauten und Sonderzeichen 
	private static String[][] UMLAUT_REPLACEMENTS = 
	{ 
		{ new String("Ä"), "Ae" }, 
		{ new String("Ü"), "Ue" }, 
		{ new String("Ö"), "Oe" }, 
		{ new String("ä"), "ae" }, 
		{ new String("ü"), "ue" }, 
		{ new String("ö"), "oe" }, 
		{ new String("ß"), "ss" },
		{ new String("→"), "->" },
		{ new String("…"), "..." },
		{ new String("’"), "\'" },
		{ new String("“"), "\"" },
		{ new String("”"), "\"" },
		{ new String("—"), "-" },
		{ new String("❌"), "X" },
		{ new String("‘"), "\'" },
		{ new String("✓"), "(ok)" },
		{ new String("–"), "-" },
		{ new String("⁰"), "^0" },
		{ new String(" ̶"), "-" },
		{ new String("✔"), "(ok)" },
		{ new String(" ̶"), "-" },
		{ new String("•"), "*" },
		{ new String("❤"), "<3" },
	};
	// Ersetze alle Umlaute und Sonderzeichen
	// Input: String
	// Output: neuer String
	public static String replaceUmlaute(String orig) 
	{
	    String result = orig;
	    String resultnew = "";

	    for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) 
	        result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
		for(int i = 0;i < result.length();i++)
			if(result.charAt(i) < 256)
				resultnew += result.charAt(i);

	    return resultnew;
	}
	// Finde alle Hashtag-Strings
	// Input: Text
	// Output: Liste von Hashtags
	static List<String> findHashtags(String s)
	{
		List<String> hashtags = new ArrayList<String>();
		
		Pattern MY_PATTERN = Pattern.compile("#(\\w+)\\b");
		Matcher mat = MY_PATTERN.matcher(s);
		while (mat.find()) 
		{
		  //System.out.println(mat.group(1));
			String sub = mat.group(1);
			if(hashtags.contains(sub) == false)
				hashtags.add(sub);
		}
		return hashtags;
	}
}
