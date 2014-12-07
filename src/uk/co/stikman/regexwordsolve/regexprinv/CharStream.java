package uk.co.stikman.regexwordsolve.regexprinv;

class CharStream {
	private String	src;
	private int		len;
	private int		ptr;

	public CharStream(String src) {
		this.src = src;
		this.len = src.length();
		this.ptr = 0;
	}

	public char next() {
		if (!hasMore())
			throw new RuntimeException("Unexpected end of string");
		return src.charAt(ptr++);
	}

	public boolean hasMore() {
		return (ptr < len);
	}

	public void rewind() {
		if (ptr < 1)
			throw new RuntimeException("Cannot rewind");
		--ptr;
	}

	public void expect(char ch) {
		if (!hasMore())
			throw new RuntimeException("Unexpected end of string: Expected " + ch);
		char x = next();
		if (x != ch)
			throw new RuntimeException("Expected " + ch + ", found " + x);
	}
}
