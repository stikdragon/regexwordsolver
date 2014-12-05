package uk.co.stikman.regexwordsolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Cell {

	private int				y;
	private int				x;
	private Set<Character>	valid;
	private List<Clue>		clues	= new ArrayList<>();

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public void setValid(Set<Character> valid) {
		this.valid = valid;
	}

	public Set<Character> getValid() {
		return valid;
	}

	public void addClue(Clue c) {
		clues.add(c);
	}

	public List<Clue> getClues() {
		return clues;
	}

}
