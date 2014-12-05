package uk.co.stikman.regexwordsolve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clue {

	private String			string;
	private Set<Character>	validChars;
	private Matcher			matcher;
	private final Solver	solver;
	private final int		index;
	private final Side		side;

	public Clue(Solver solver, Side side, int index) {
		this.solver = solver;
		this.index = index;
		this.side = side;
	}

	public void setString(String regex) {
		this.string = regex;
		Pattern p = Pattern.compile("^" + regex + "$");
		matcher = p.matcher("");
	}

	public String getString() {
		return string;
	}

	private int				ptr;
	private Set<Character>	digits;

	/**
	 * There's no error checking in here since it's assumed that Java's own
	 * regex stuff would have caught any errors in the constructor
	 */
	public void calculateValidChars() {
		ptr = 0;
		validChars = new HashSet<>();
		while (ptr < string.length()) {
			char ch = string.charAt(ptr++);
			if (isReserved(ch)) {
				switch (ch) {
				case '[':
					parseCharClass();
					break;
				case '\\':
					ch = string.charAt(ptr++);
					if (ch == 'd')
						validChars.addAll(getDigits());
					else if (ch == 's') {
						validChars.add(' ');
					} else
						validChars.add(ch);
					continue;
				case '.':
					validChars.addAll(solver.getChars());
				}

			} else {
				validChars.add(ch);
			}
		}
	}

	private Set<Character> getDigits() {
		if (digits == null) {
			digits = new HashSet<>();
			for (char c = '0'; c <= '9'; ++c)
				digits.add(c);
		}
		return digits;
	}

	private void parseCharClass() {
		//
		// look for ]
		//
		int offset = -1;
		char last = 0;
		boolean invert = false;
		Set<Character> set = new HashSet<>();
		while (ptr < string.length()) {
			char ch = string.charAt(ptr++);
			++offset;
			if (ch == ']')
				break;
			if (ch == '\\') {
				ch = string.charAt(ptr++);
				if (ch == 'd')
					validChars.addAll(getDigits());
				else if (ch == 's') {
					validChars.add(' ');
				} else
					validChars.add(ch);
				continue;
			}
			if (ch == '-') {
				ch = string.charAt(ptr++);
				addRange(last, ch);
				continue;
			}

			if (offset == 0 && ch == '^')
				invert = true;
			else
				set.add(ch);
			last = ch;
		}
		if (invert) {
			for (Character ch : solver.getChars())
				if (!set.contains(ch))
					validChars.add(ch);
		} else {
			validChars.addAll(set);
		}
	}

	private void addRange(char from, char to) {
		for (char ch = from; ch <= to; ++ch)
			validChars.add(ch);
	}

	private static boolean isReserved(char ch) {
		// [\^$.|?*+()
		switch (ch) {
		case '[':
		case '\\':
		case '^':
		case '|':
		case '?':
		case '*':
		case '+':
		case '(':
		case ')':
		case '.':
			return true;
		default:
			return false;
		}
	}

	public Set<Character> getValidChars() {
		return validChars;
	}

	public int getIndex() {
		return index;
	}

	public List<Set<Character>> solve(List<Set<Character>> start) {
		List<Set<Character>> res = new ArrayList<>();
		for (int i = 0; i < start.size(); ++i)
			res.add(new HashSet<>());
		solveInternal(start, res, null);
		return res;
	}

	/**
	 * Returns a <b>new</b> {@link List} of Set<Character>
	 * 
	 * @param start
	 * @return
	 */
	private void solveInternal(List<Set<Character>> start, List<Set<Character>> charset, List<String> words) {
		//
		// Turn the sets into arrays
		//
		List<char[]> avail = new ArrayList<>();
		for (Set<Character> set : start) {
			char[] arr = new char[set.size()];
			int i = 0;
			for (Character ch : set)
				arr[i++] = ch;
			avail.add(arr);
		}

		char[] chars = new char[avail.size()];
		int[] indexes = new int[avail.size()];

		int pos = 0;
		while (true) {
			pos = 0;
			while (indexes[pos] >= avail.get(pos).length) {
				indexes[pos] = 0;
				++pos;
				if (pos >= chars.length)
					return;
				++indexes[pos];
			}

			for (int i = 0; i < chars.length; ++i)
				chars[i] = avail.get(i)[indexes[i]];
			String s = new String(chars);
			matcher.reset(s);
			if (matcher.find()) {
				if (words != null)
					words.add(s);
				if (charset != null)
					for (int j = 0; j < s.length(); ++j)
						charset.get(j).add(chars[j]);
			}

			++indexes[0];
		}
	}

	public List<String> getWordSolutions(List<Set<Character>> starting) {
		List<String> res = new ArrayList<>();
		solveInternal(starting, null, res);
		return res;
	}

	@Override
	public String toString() {
		return "(" + side.getFriendly() + ") " + getString();
	}

	public Side getSide() {
		return side;
	}

}
