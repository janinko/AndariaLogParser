package eu.janinko.Andaria.logparser.parsers;

/**
 *
 * @author janinko
 */
public class WorkingLine implements CharSequence{
	private String wl;

	public WorkingLine(String wl) {
		this.wl = wl;
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

	@Override
	public int length() {
		return wl.length();
	}

	@Override
	public char charAt(int index) {
		return wl.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return wl.subSequence(start, end);
	}

	public void substring(int beginIndex){
		wl = wl.substring(beginIndex);
	}

	public boolean startsWith(String prefix){
		return wl.startsWith(prefix);
	}

	public boolean startsWith(String prefix, int toffset){
		return wl.startsWith(prefix, toffset);
	}

	public boolean contains(String s){
		return wl.contains(s);
	}

	public boolean matches(String regex){
		return wl.matches(regex);
	}

	public void replaceFirst(String regex, String replacement){
		wl = wl.replaceFirst(regex, replacement);
	}

	public String rest(){
		String ret = wl;
		wl = "";
		return ret;
	}

	@Override
	public String toString() {
		return wl;
	}

	int codePointAt(int index) {
		return wl.codePointAt(index);
	}

	public boolean equals(String str){
		return wl.equals(str);
	}
}
