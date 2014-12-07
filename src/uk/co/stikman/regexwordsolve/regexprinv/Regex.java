package uk.co.stikman.regexwordsolve.regexprinv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Regex {
	private String			source;
	private RootOp			root;
	private List<Op>		nodes;
	private List<GroupOp>	groups;
	private List<Character>	charset;
	private int				maxLength;

	/**
	 * @param source
	 */
	public Regex(String source) {
		super();
		this.source = source;
		setCharset(" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`{|}~");
		compile();
		number();
		maxLength = 100;
	}

	public void setCharset(String s) {
		this.charset = new ArrayList<>();
		for (int i = 0; i < s.length(); ++i)
			charset.add(s.charAt(i));
	}

	public String getSource() {
		return source;
	}

	private void compile() {
		root = new RootOp(this);
		CharStream cs = new CharStream(source);
		root.setChild(parseExpression(cs, ""));
	}

	/**
	 * Leaves pointer on the thing that ended this, unless it was an EOL
	 * condition, in which case you must check for CharStream.hasMore()
	 * 
	 * @param cs
	 * @param stopOnChars
	 * @return
	 */
	private Op parseExpression(CharStream cs, String stopOnChars) {
		OpSequence seq = new OpSequence();
		CountOp cnt;
		while (cs.hasMore()) {
			char ch = cs.next();
			if (stopOnChars.indexOf(ch) != -1) {
				cs.rewind();
				break;
			}
			switch (ch) {
			case '[':
				cs.rewind();
				seq.append(parseCharClass(cs));
				break;
			case '\\':
				cs.rewind();
				seq.append(parseSlash(cs));
				break;
			case '+':
				if (seq.isEmpty())
					throw new RuntimeException("+ must follow another token");
				cnt = new CountOp(this, 1, -1);
				cnt.setChild(seq.replaceLast(cnt));
				break;
			case '*':
				if (seq.isEmpty())
					throw new RuntimeException("* must follow another token");
				cnt = new CountOp(this, 0, -1);
				cnt.setChild(seq.replaceLast(cnt));
				break;
			case '?':
				if (seq.isEmpty())
					throw new RuntimeException("? must follow another token");
				cnt = new CountOp(this, 0, 1);
				cnt.setChild(seq.replaceLast(cnt));
				break;
			case '(':
				cs.rewind();
				seq.append(parseGroup(cs));
				break;
			case '.':
				seq.append(new AnyCharOp(this));
				break;
			case '|':
				if (seq.isEmpty())
					throw new RuntimeException("| must follow another token");
				OrOp orop = new OrOp(this);
				orop.addChild(seq.replaceLast(orop));

				//
				// continue compiling expression until we get to another |, or we reach the end 
				//
				Op next = parseExpression(cs, "|)");
				while (next != null) {
					orop.addChild(next);
					if (!cs.hasMore()) {
						next = null; // end of the expression
					} else {
						char c = cs.next();
						if (c == ')') {
							cs.rewind();
							return seq.getFirst();
						} else if (c == '|') {
							next = parseExpression(cs, "|)");
						} else
							assert (false); //derp
					}
				}
				return seq.getFirst();
			default:
				seq.append(new CharSetOp(this, ch));
			}
		}
		if (seq.isEmpty())
			throw new RuntimeException("Empty () found");
		return seq.getFirst();
	}

	private Op parseGroup(CharStream cs) {
		cs.expect('(');
		GroupOp res = new GroupOp(this);
		res.setChild(parseExpression(cs, ")"));
		cs.expect(')');
		return res;
	}

	private Op parseSlash(CharStream cs) {
		cs.expect('\\');
		char ch = cs.next();
		HashSet<Character> set = new HashSet<Character>();
		switch (ch) {
		case 's':
			set.add(' ');
			break;
		case 'd':
			addRange(set, '0', '9');
			break;
		default:
			set.add(ch);
		}
		return new CharSetOp(this, set, false);
	}

	private Op parseCharClass(CharStream cs) {
		int offset = -1;
		char last = 0;
		boolean invert = false;
		Set<Character> set = new HashSet<>();
		cs.expect('[');
		while (cs.hasMore()) {
			char ch = cs.next();
			++offset;
			if (ch == ']') // end of class
				break;
			if (ch == '\\') {
				ch = cs.next();
				if (ch == 'd')
					addRange(set, '0', '9');
				else if (ch == 's')
					set.add(' ');
				else
					set.add(ch);
				continue;
			}
			if (ch == '-') {
				ch = cs.next();
				if (ch == ']') {
					set.add(last);
					set.add('-');
					break;
				} else {
					addRange(set, last, ch);
				}
				continue;
			}

			if (offset == 0 && ch == '^')
				invert = true;
			else
				set.add(ch);
			last = ch;
		}
		return new CharSetOp(this, set, invert);
	}

	private void addRange(Set<Character> set, char from, char to) {
		for (char ch = from; ch <= to; ++ch)
			set.add(ch);
	}

	/**
	 * Walk the compiled structure and get a list of Ops, and number the groups
	 */
	private void number() {
		groups = new ArrayList<>();
		nodes = new ArrayList<>();

		root.walk(new WalkTarget() {
			@Override
			public void process(Op op) {
				nodes.add(op);
				if (op instanceof GroupOp)
					groups.add((GroupOp) op);
			}
		});
		int i = 0;
		for (GroupOp go : groups)
			go.setGroupNumber(i++);
		i = 0;
		for (Op op : nodes)
			op.setId(i++);
	}

	public RootOp getRoot() {
		return root;
	}

	public List<Op> getNodes() {
		return nodes;
	}

	public List<GroupOp> getGroups() {
		return groups;
	}

	public List<Character> getCharSet() {
		return charset;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
