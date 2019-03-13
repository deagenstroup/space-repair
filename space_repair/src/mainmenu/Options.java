package mainmenu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Options {
    public int resWidth, resHeight;
    public String theme;
    
    public Options() {
        resWidth = 1920;
        resHeight = 1080;
        theme = "space";
    }
    
    public boolean writeOutOptions() {
        try {
            FileWriter fr = new FileWriter("../options");
            PrintWriter pw = new PrintWriter(fr);
            
            pw.println(resWidth);
            pw.println(resHeight);
            pw.println(theme);
            
            pw.close();
        } catch(IOException e) {
            System.out.println("Error: writing to options file");
            return false;
        }
        return true;
    }
    
    public boolean readInOptions() {
        try {
            FileReader fr = new FileReader("../options");
            BufferedReader br = new BufferedReader(fr);
            String temp;
            
            temp = br.readLine();
            resWidth = Integer.parseInt(temp);
            
            temp = br.readLine();
            resHeight = Integer.parseInt(temp);
            
            theme = br.readLine();
            
            br.close();
        } catch(IOException e) {
            System.out.println("Error: reading in options from file");
            return false;
        }
        return true;
    }
}
