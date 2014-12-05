package uk.co.stikman.regexwordsolve;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver {

	private Cell[]			cells;
	private List<Clue>		clues;
	private Set<Character>	chars	= new HashSet<>();
	private int				height;
	private int				width;
	private SolverOutput	output	= null;
	private int				timeout;

	public Solver(int width, int height) {
		this.width = width;
		this.height = height;
		setAvailableCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?");
		cells = new Cell[height * width];
		clues = new ArrayList<>();
		for (int i = 0; i < cells.length; ++i)
			cells[i] = new Cell(i % width, i / width);
	}

	public void setAvailableCharacters(String chars) {
		for (int i = 0; i < chars.length(); ++i)
			this.chars.add(chars.charAt(i));
	}

	public void addClue(int idx, String regex, Side side) {
		Clue c = new Clue(this, side, idx);
		c.setString(regex);
		clues.add(c);
	}

	public Cell getCell(int x, int y) {
		return cells[y * width + x];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public List<String> solve() throws TookTooLongException {
		long started = System.currentTimeMillis();
		
		List<Clue> clues = new ArrayList<>();
		clues.addAll(this.clues);

		//
		// Get the set of chars that can be involved in each of the clues, and
		// assign the Clues to each Cell
		//
		log("================================");
		log("Initial clue solutions:");
		log("================================");
		for (Clue c : clues) {
			if (c.getSide().isRow())
				for (int i = 0; i < width; ++i)
					getCell(i, c.getIndex()).addClue(c);
			else
				for (int i = 0; i < height; ++i)
					getCell(c.getIndex(), i).addClue(c);
			c.calculateValidChars();
			log(c.toString() + " > available chars: " + c.getValidChars().toString());
		}

		//
		// Sort in order so we can do the smallest ones first
		//
		clues.sort(new Comparator<Clue>() {
			@Override
			public int compare(Clue o1, Clue o2) {
				return o1.getValidChars().size() - o2.getValidChars().size();
			}
		});

		//
		// Sense checking
		//
		for (Cell cell : cells)
			if (cell.getClues().size() < 2)
				throw new RuntimeException("Cell " + cell.toString() + " can't be solved because it doesn't have enough clues");

		log("================================");
		log("Cross-referenced clue solutions (sorted by complexity):");
		log("================================");
		while (!clues.isEmpty()) {
			//
			// For each cell we can now work out what's valid via the intersection of 
			// the available chars
			//
			for (Cell cell : cells) {
				Set<Character> res = new HashSet<>(cell.getClues().get(0).getValidChars());
				for (int i = 1; i < cell.getClues().size(); ++i)
					res = intersectSets(res, cell.getClues().get(i).getValidChars());
				cell.setValid(res);
			}

			//
			// Solve the top clue and update what's available for the cell. When we're 
			// done we remove the clue from the list and re-run the intersection
			//
			Clue clue = clues.get(0);
			clues.remove(0);
			List<Set<Character>> starting = new ArrayList<>();
			if (clue.getSide().isRow()) {
				for (int i = 0; i < width; ++i)
					starting.add(new HashSet<>(getCell(i, clue.getIndex()).getValid()));
			} else {
				for (int i = 0; i < height; ++i)
					starting.add(new HashSet<>(getCell(clue.getIndex(), i).getValid()));
			}

			List<Set<Character>> possible = clue.solve(starting);
			log(clue.toString() + " > Possible: " + possible.toString());

			if (clue.getSide().isRow()) {
				for (int i = 0; i < width; ++i)
					getCell(i, clue.getIndex()).setValid(possible.get(i));
			} else {
				for (int i = 0; i < height; ++i)
					getCell(clue.getIndex(), i).setValid(possible.get(i));
			}
		}

		log("================================");
		log("Final list of characters valid in each cell:");
		log("================================");
		for (Cell c : cells)
			log(c.getX() + ", " + c.getY() + " = " + c.getValid().toString());

		clues.addAll(this.clues);

		log("================================");
		log("Solution counts per clue:");
		log("================================");

		//
		// So now for each clue we can calculate (hopefully) a small set of strings that
		// satisfy each clue it and do a cross-reference to find a set that satisfies the 
		// whole puzzle
		//
		long perms = 1;
		List<List<String>> solutions = new ArrayList<>();
		for (Clue clue : clues) {
			List<Set<Character>> starting = new ArrayList<>();
			if (clue.getSide().isRow()) {
				for (int i = 0; i < width; ++i)
					starting.add(new HashSet<>(getCell(i, clue.getIndex()).getValid()));
			} else {
				for (int i = 0; i < height; ++i)
					starting.add(new HashSet<>(getCell(clue.getIndex(), i).getValid()));
			}
			List<String> sol = clue.getWordSolutions(starting);
			testTimeout(started);
			solutions.add(sol);
			log(clue.toString() + " > " + sol.size() + " solutions");
			perms *= sol.size();
		}

		log("TOTAL: " + perms + " combinations to try");

		//
		// Try all permutations
		//
		List<String> results = new ArrayList<>();
		char[] grid = new char[width * height];
		String[] strings = new String[solutions.size()];
		int[] indexes = new int[solutions.size()];

		int pos = 0;
		while (true) {
			pos = 0;
			testTimeout(started);
			while (indexes[pos] >= solutions.get(pos).size()) {
				indexes[pos] = 0;
				++pos;
				if (pos >= solutions.size()) {
					if (results.isEmpty())
						log("no solutions found");
					else
						log(results.size() + " solutions found.  first: " + results.get(0));
					return results;
				}
				++indexes[pos];
			}

			for (int i = 0; i < strings.length; ++i)
				strings[i] = solutions.get(i).get(indexes[i]);

			//
			// fill in the grid, if any conflicts then abandon
			//
			for (int i = 0; i < grid.length; ++i)
				grid[i] = 0;
			boolean fail = false;
			for (int i = 0; i < strings.length; ++i) {
				Clue clue = clues.get(i);
				String s = strings[i];
				if (clue.getSide().isRow()) {
					for (int j = 0; j < s.length(); ++j) {
						char ch = grid[j + width * clue.getIndex()];
						if (ch != 0 && ch != s.charAt(j)) {
							fail = true;
							break;
						}
						grid[j + width * clue.getIndex()] = s.charAt(j);
					}
				} else {
					for (int j = 0; j < s.length(); ++j) {
						char ch = grid[clue.getIndex() + width * j];
						if (ch != 0 && ch != s.charAt(j)) {
							fail = true;
							break;
						}
						grid[clue.getIndex() + width * j] = s.charAt(j);
					}
				}
				if (fail)
					break;
			}

			if (!fail)
				results.add(new String(grid));

			++indexes[0];
		}
	}

	private void testTimeout(long started) throws TookTooLongException {
		long dt = System.currentTimeMillis() - started;
		if (dt > getTimeout())
			throw new TookTooLongException();
	}

	private <T> Set<T> intersectSets(Set<T> a, Set<T> b) {
		Set<T> big;
		Set<T> small;
		Set<T> res = new HashSet<T>();
		if (a.size() <= b.size()) {
			big = a;
			small = b;
		} else {
			big = b;
			small = a;
		}
		for (T e : big)
			if (small.contains(e))
				res.add(e);
		return res;
	}

	public Set<Character> getChars() {
		return chars;
	}

	public SolverOutput getOutput() {
		return output;
	}

	public void setOutput(SolverOutput output) {
		this.output = output;
	}

	private void log(String msg) {
		if (output != null)
			output.log(msg);
	}

	public void setTimeout(int ms) {
		this.timeout = ms;
	}

	public int getTimeout() {
		return timeout;
	}

}
