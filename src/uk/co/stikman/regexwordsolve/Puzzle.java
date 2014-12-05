package uk.co.stikman.regexwordsolve;

import java.util.ArrayList;
import java.util.List;

public class Puzzle {
	private List<PuzzleRegex>	clues		= new ArrayList<>();
	private String				name;
	private String				group;
	private int					width;
	private int					height;
	private List<String>		solutions	= new ArrayList<>();
	private String				message;
	private Status				status		= Status.NONE;

	public String getPattern(Side side, int idx) {
		for (PuzzleRegex pr : clues)
			if (pr.getSide() == side && pr.getIndex() == idx)
				return pr.getRegex();
		return "";
	}

	public void setPattern(Side side, int idx, String p) {
		clues.add(new PuzzleRegex(side, idx, p));
		if (side.isRow())
			height = Math.max(height, idx + 1);
		else
			width = Math.max(width, idx + 1);
	}

	public List<PuzzleRegex> getClues() {
		return clues;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		switch (getStatus()) {
		case ERROR:
			return "[Error] " + group + ", " + name + ": " + message;
		case NONE:
			return "[None] " + group + ", " + name;
		case NOSOLUTIONS:
			return "[No Solutions] " + group + ", " + name + ": " + message;
		case SOLVED:
			return "[Solved] " + group + ", " + name + ": " + message + ", " + getSolutions().size() + " solutions, first = " + getSolutions(); //.replaceAll(".", "*");
		case TIMEOUT:
			return "[Timed Out] " + group + ", " + name + ": " + message;
		default:
			return "[???] " + group + ", " + name + ": " + message;
		}

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void addSolution(String s) {
		solutions.add(s);
	}

	public List<String> getSolutions() {
		return solutions;
	}

	public void setStatus(Status s) {
		setStatus(s, "");
	}

	public void setStatus(Status s, String msg) {
		this.status = s;
		this.message = msg;
	}

	public String getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}

}
