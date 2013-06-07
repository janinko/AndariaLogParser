package eu.janinko.Andaria.logparser.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janinko
 */
public class Log {
	private BufferedReader in;
	private String cline;
	String wl;
	private String nextline;
	private int linenum;

	public Log(Reader reader){
		in = new BufferedReader(reader);
	}

	public String currentLine(){
		return cline;
	}

	public String getUntil(String s){
		int pos = 0;
		while(!wl.startsWith(s, pos) && pos < wl.length()){
			pos++;
		}

		String ret = wl.substring(0, pos);
		wl = wl.substring(pos);
		return ret;
	}
	
	public String nextLine(){
		if(hasNextLine()) linenum++;

		cline = nextline;
		nextline = null;
		return cline;
	}

	public boolean hasNextLine(){
		if(nextline != null) return true;

		try {
			nextline = in.readLine();
		} catch (IOException ex) {
			Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
		}
		return nextline != null;
	}

	public int getLineNumber(){
		return linenum;
	}


	public void unknownLine(){
		unknownLine("");
	}

	public void unknownLine(String message){
		if(message.equals("Too short!")) return;
		System.err.println("Unknown input, line " + linenum +":  " + message);
		System.err.println(cline);
		if(!Objects.equals(cline, wl)){
			for(int i=wl.length(), j=cline.length(); i<j; i++){
				System.err.print(' ');
			}
			System.err.println("^");
		}
	}

}
