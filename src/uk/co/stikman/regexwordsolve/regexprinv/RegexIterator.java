package uk.co.stikman.regexwordsolve.regexprinv;

public class RegexIterator {
	private Regex	regex;
	private State	state;

	public RegexIterator(Regex regex) {
		this.regex = regex;
		state = new State(regex.getNodes().size());
		int i = 0;
		for (Op op : regex.getNodes()) {
					
					++i;
		}
		
	}

}
