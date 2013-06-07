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
	WorkingLine wl;
	private String nextline;
	private int linenum;

	public Log(Reader reader){
		in = new BufferedReader(reader);
	}

	public String currentLine(){
		return cline;
	}

	public String nextLine(){
		if(hasNextLine()) linenum++;

		cline = nextline;
		wl = new WorkingLine(cline);
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
