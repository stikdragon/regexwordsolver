package uk.co.stikman.regexwordsolve.regexprinv;


public class State {
	int[]		indexes;
	int[]		lengths;
	private int	size;

	public State(int size) {
		this.size = size;
		indexes = new int[size];
		lengths = new int[size];
	}

	public boolean next() {
		int pos = 0;
		++indexes[0];
		while (indexes[pos] >= lengths[pos]) {
			indexes[pos] = 0;
			++pos;
			if (pos >= size)
				return false;
			++indexes[pos];
		}
		return true;
	}
	
	public void resetFrom(int idx) {
		for (int i = idx; i < indexes.length; ++i)
			indexes[i] = 0;
	}
	
	public void render(Regex regex, RenderTarget target) {
		RootOp node = regex.getRoot();
		node.render(this, target);
	}

}
