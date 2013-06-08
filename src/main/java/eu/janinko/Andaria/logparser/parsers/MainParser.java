package eu.janinko.Andaria.logparser.parsers;
import eu.janinko.Andaria.logparser.InvalidPlayersState;
import eu.janinko.Andaria.logparser.MessageSender;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainParser implements Parser{
	private Log log;
	private MessageSender sender;

	private MessageLineParser messageLine;
	private SphereLineParser sphereLine;
	private StandardLineParser standardLine;
	private AccountLineParser accountLine;
	private PlayerLineParser playerLine;
	private PageParser pageParser;
	private TimeParser timeParser;
	
	
	public static void main(String[] args){		
		try {
			MainParser p = new MainParser(new FileReader("sphere2013-06-05.log"));
			p.start();
			p.parse();
			
			p.sender.print();
			p.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MainParser(Reader reader){
		log = new Log(reader);
		sender = new MessageSender();

		messageLine = new MessageLineParser(log);
		timeParser = new TimeParser(log);
		sphereLine = new SphereLineParser(log, sender, timeParser);
		standardLine = new StandardLineParser(log, sender, timeParser);
		accountLine = new AccountLineParser(log);
		playerLine = new PlayerLineParser(log, sender);
		pageParser = new PageParser(log, sender);
	}

	public void start(){
		Thread t = new Thread(sender);
		t.start();
	}
	
	public void stop(){
		sender.setRunning(false);
	}
	
	
	public void parse() throws IOException{
		while(log.hasNextLine()){
			log.nextLine();
			parseLine();
			if(log.getLineNumber() % 10000 == 0){
				System.out.print('.');
				if(log.getLineNumber() % 100000 == 0){
					System.out.print(' ');
				}
			}
		}
	}
	
	Matcher match_lSAVE = Pattern.compile("^(GC:|(World|Player|Multi|Context) data saved|World save completed).*").matcher("");
	@Override
	public void parseLine(){
		try{
			timeParser.parseLine();
			
			if(log.wl.startsWith(" POZOR ")){
				log.wl.substring(7);
				messageLine.parseLine("POZOR");
			}else if(log.wl.startsWith(" MySql:")){
				return;// TODO
			}else if(log.wl.startsWith(" -----")){
				return;// TODO
			}else if(log.wl.startsWith(" ")){
				log.wl.substring(1);
				standardLine.parseLine();
			}else if(log.wl.startsWith(":")){
				log.wl.substring(1);
				sphereLine.parseLine();
			}else if(log.wl.startsWith("ERROR:")){	//check for ERROR line
				log.wl.substring(6);
				messageLine.parseLine("ERROR");
			}else if(log.wl.startsWith("DEBUG:")){	//check for DEBUG line
				log.wl.substring(6);
				messageLine.parseLine("DEBUG");
			}else if(log.wl.startsWith("WARNING:")){	//check for WARNING line
				log.wl.substring(8);
				messageLine.parseLine("WARNING");
			}else if(log.wl.startsWith("CRITICAL:")){	//check for CRITICAL line
				log.wl.substring(9);
				messageLine.parseLine("CRITICAL");
			}else if(log.wl.startsWith("GM Page")){	//check for GM Page line
				log.wl.substring(7);
				pageParser.parseLine();
			}else if(log.wl.startsWith("'")){	//check for ' line
				sphereLine.parseLine();
			}else if(log.wl.startsWith("P'")){	//check for Player line
				playerLine.parseLine();
			}else if(log.wl.startsWith("A'")){	//check for Account line
				accountLine.parseLine();
			}else if(match_lSAVE.reset(log.wl).matches()){	//check for SAVE line
				return;
			}else{
				log.unknownLine();
			}
		}catch(StringIndexOutOfBoundsException ex){
			log.unknownLine("Too short!");
		}catch(InvalidPlayersState ex){
			log.unknownLine("InvalidPlayersState: " + ex.getMessage());
		}
	}
	

	public MessageSender getSender() {
		return sender;
	}
}