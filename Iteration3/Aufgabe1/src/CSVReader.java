//Original: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
//An unser problem angepasst
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
public static String[] getHashtags(String file){
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String[] hashtags= new String[429];
        
        try {

            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            int counter=0;
            
            while ((line = br.readLine()) != null) {
                String[] oneLine = line.split(cvsSplitBy);
                
                hashtags[counter]=oneLine[0].replaceAll(" ", "");
                hashtags[counter]=hashtags[counter].substring(1,hashtags[counter].length()-1);
                
                System.out.println(hashtags[counter]);
                counter++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
        	
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hashtags;
	}
}

