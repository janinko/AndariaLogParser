package eu.janinko.Andaria.logparser.parsers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author janinko
 */
public class TimeParser implements Parser{
	private final Log log;
	private Calendar c;

	public TimeParser(Log log) {
		this.log = log;
	}


	Matcher match_tFIRSTLINE = Pattern.compile("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.*").matcher("");
	Matcher match_tHOURMINUTE = Pattern.compile("^[0-9][0-9]:[0-9][0-9]:.*").matcher("");
	Matcher match_tSCRIPTNAME = Pattern.compile("\\([a-zA-Z_-]+\\.scp,[0-9]+\\).*").matcher("");
	Matcher match_tSECONDS = Pattern.compile("^[0-5]{0,1}[0-9] .*").matcher("");
	Matcher match_tWEIRDSTUFF = Pattern.compile("^[0-9a-f]{3}:.*").matcher("");
	@Override
	public void parseLine() {

		// firstline date
		if(match_tFIRSTLINE.reset(log.wl).matches()){
			int year=log.wl.codePointAt(0)*1000 + log.wl.codePointAt(1)*100 + log.wl.codePointAt(2)*10 + log.wl.codePointAt(3) - '0'*1111 ;
			int month=log.wl.codePointAt(5)*10+log.wl.codePointAt(6) - '0'*11;
			int day=log.wl.codePointAt(8)*10+log.wl.codePointAt(9) - '0'*11;

			if(c == null){
				c = new GregorianCalendar(year, month-1,day, 0, 0);
			}else if(c.get(Calendar.YEAR) != year || c.get(Calendar.MONTH)+1 != month || c.get(Calendar.DAY_OF_MONTH) != day){
				log.unknownLine("Wrong date: " + year + "-" + month + "-" + day + " x "
			                 + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH));
			}
			log.wl.substring(19);
		}

		int hour = -1;
		int minute = -1;
		int second = -1;

		// chceck hour and minute
		if(match_tHOURMINUTE.reset(log.wl).matches()){	//check time format
			hour=log.wl.codePointAt(0)*10+log.wl.codePointAt(1) - '0'*11;
			minute=log.wl.codePointAt(3)*10+log.wl.codePointAt(4) - '0'*11;
			log.wl.substring(6);
		}else{
			log.unknownLine("Wrong time format");
		}

		// check for (*.scp,123) substr and remove if present
		if(match_tSCRIPTNAME.reset(log.wl).matches()){
			log.wl.replaceFirst("\\([a-zA-Z_-]+\\.scp,[0-9]+\\)", "");
		}

		// check for seconds
		if(match_tSECONDS.reset(log.wl).matches()){
			second=log.wl.charAt(0) - '0';

			if(log.wl.charAt(1) >= '0' && log.wl.charAt(1) <= '9'){
				second*=10;
				second+=log.wl.charAt(1) - '0';
				log.wl.substring(3);
			}else{
				log.wl.substring(2);
			}
		} //check if there are the weird stuff
		else if(match_tWEIRDSTUFF.reset(log.wl).matches()){
			log.wl.substring(3);
		}

		setTime(hour, minute, second);
	}


	private void setTime(int hour, int minute, int second){
		if(hour <0 || hour >= 60 || minute <0 || minute >= 60 || second < -1 || second >= 60 ){
			throw new IllegalArgumentException();
		}
		if(second == -1){
			setTime(hour, minute);
		}else{
			if(hour <= c.get(Calendar.HOUR_OF_DAY) &&  minute <= c.get(Calendar.MINUTE) && second < c.get(Calendar.SECOND) ){
				log.unknownLine("Wrong time: " + hour + ":" + minute + ":" + second + " x "
			                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
			}else{
				setTime(hour, minute);
				c.set(Calendar.SECOND, second);
			}
		}
	}

	private void setTime(int hour, int minute){

		if(hour < c.get(Calendar.HOUR_OF_DAY)){
			log.unknownLine("Wrong time: " + hour + ":" + minute + " x "
		                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
		}else if(hour == c.get(Calendar.HOUR_OF_DAY) && minute < c.get(Calendar.MINUTE)){
			log.unknownLine("Wrong time: " + hour + ":" + minute + " x "
	                 + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
		}else{
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, 0);
		}
	}

	public Calendar getCalendar(){
		return c;
	}

}
