package uk.co.stikman.regexwordsolve;

public enum Side {
	N(false, "North"), 
	E(true, "East"), 
	S(false, "South"), 
	W(true, "West");

	private boolean	isRow;
	private String	friendly;

	private Side(boolean isRow, String friendly) {
		this.isRow = isRow;
		this.friendly = friendly;
	}

	public boolean isRow() {
		return isRow;
	}

	public String getFriendly() {
		return friendly;
	}

}
