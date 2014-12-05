package uk.co.stikman.regexwordsolve;

public class PuzzleRegex {
	private Side	side;
	private int		index;
	private String	regex;

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public PuzzleRegex(Side side, int index, String regex) {
		super();
		this.side = side;
		this.index = index;
		this.regex = regex;
	}

	public PuzzleRegex() {
		super();
	}
	
	@Override
	public String toString() {
		return "(" + side + ") " + regex;
	}

}
