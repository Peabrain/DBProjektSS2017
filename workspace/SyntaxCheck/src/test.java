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
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:5432/Election";
	
	//  Database credentials
	static final String USER = "testuser";
	static final String PASS = "testpass";

	static public void main(String[] arg) throws IOException
	{		
		String url = "jdbc:postgresql://localhost:5432/Election";
		Properties props = new Properties();
		props.setProperty("user","testuser");
		props.setProperty("password","testpass");
//		props.setProperty("ssl","false");
		Connection conn = null;
		PreparedStatement pst = null;
		
		HashMap<Integer,tweet> tweets = new HashMap(); 
		HashMap<String,hashtag> hashtagcount = new HashMap();
		HashMap<String,time> timestam = new HashMap();
		HashMap<String,erscheintzsmmit> erscheintzsmmitMap = new HashMap();
		
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
			try 
			{
				conn = DriverManager.getConnection(url, props);
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			finally
			{
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
				
				
				Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
				InputStream ExcelFileToRead = new FileInputStream("american-election-tweets.xlsx");
				XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
				
				XSSFWorkbook test = new XSSFWorkbook(); 
				
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFRow row; 
				XSSFCell cell;

				Iterator rows = sheet.rowIterator();

				int zeile = 1;
				while (rows.hasNext())
				{
					String handle = null;
					String text = null;
					int anz_retweet = 0;
					int anz_likes = 0;
					String org_autor = null;
					String timestamp = null;

					System.out.print(zeile + " : ");
					row=(XSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					int Fehler = 0;
					while (cells.hasNext() && Fehler == 0)
					{
						cell=(XSSFCell) cells.next();
						int Spalte = cell.getColumnIndex();
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
						{
							String olds = cell.getStringCellValue();
							String news = replaceUmlaute(olds);
							char[] ascii1 = news.toCharArray();
							news = "";
							for(int i: ascii1) news += Character.toString((char)i);
							if(zeile > 1)
							{
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
							if(Spalte != 7 && Spalte != 8) Fehler++;
							else
							{
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
						tweets.put(zeile, new tweet(handle,text,anz_retweet,anz_likes,org_autor,timestamp));
					}
					zeile++;
					System.out.println();
				}
			}
		}
		for(Map.Entry<Integer, tweet> entry : tweets.entrySet())
		{
			int tweetid = entry.getKey();
			try
			{
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
	            
	            time ti = null;
	            if(timestam.containsKey(t.timestamp) == false)
	            	timestam.put(t.timestamp,new time());
            	ti = timestam.get(t.timestamp);
            	ti.getweetedam.add(tweetid);
            	
				List<String> hashtags = findHashtags(t.text);
				for(String s: hashtags)
				{
					if(hashtagcount.containsKey(s) == false)
					{
						hashtagcount.put(s, new hashtag(1,tweetid));
		            	ti.genutztam.add(s);
					}
					else
					{
						hashtag h = hashtagcount.get(s);
						h.haeufigkeit++;
						h.tweet.add(entry.getKey());
						if(ti.genutztam.contains(s) == false)
							ti.genutztam.add(s);
					}
				}
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
